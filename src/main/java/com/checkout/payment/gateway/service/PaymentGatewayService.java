package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankGatewayException;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PaymentDetails;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);
  private static final String BASE_PATH = "http://localhost:8080";
  private static final String PAYMENT_ENDPOINT = "/payments";

  private final PaymentsRepository paymentsRepository;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public PaymentGatewayService(PaymentsRepository paymentsRepository) {
    this.paymentsRepository = paymentsRepository;
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  public PaymentDetails getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PaymentDetails processPayment(PostPaymentRequest paymentRequest) {
    BankGatewayResponse bankResponse = sendPaymentToBankGateway(paymentRequest);
    
    PaymentDetails paymentDetails = new PaymentDetails(
        UUID.randomUUID(),
        bankResponse.authorized ? bankResponse.authorizationCode : null,
        bankResponse.authorized ? PaymentStatus.AUTHORIZED : PaymentStatus.REJECTED,
        paymentRequest.getCardNumberLastFour(),
        paymentRequest.getExpiryMonth(),
        paymentRequest.getExpiryYear(),
        paymentRequest.getCurrency(),
        paymentRequest.getAmount()
    );
    
    paymentsRepository.add(paymentDetails);
    return paymentDetails;
  }

  private BankGatewayResponse sendPaymentToBankGateway(PostPaymentRequest paymentRequest) {
    try {
      String requestBody = objectMapper.writeValueAsString(createBankPaymentRequest(paymentRequest));
      
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(BASE_PATH + PAYMENT_ENDPOINT))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(requestBody))
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      return getValidatedResponseFromBankGateway(response);
    } catch (BankGatewayException e) {
      throw e;
    } catch (Exception e) {
      // Only catch HTTP communication errors (network, serialization, etc.)
      throw new BankGatewayException("Error communicating with bank gateway: " + e.getMessage());
    }
  }

  private BankGatewayResponse getValidatedResponseFromBankGateway(HttpResponse<String> response) {
    if (response.statusCode() != 200) {
      throw new BankGatewayException("Error from bank gateway: " + response.statusCode() + " - " + response.body());
    }

    try {
      JsonNode jsonNode = objectMapper.readTree(response.body());
      boolean authorized = jsonNode.get("authorized").asBoolean();
      String authorizationCode = jsonNode.get("authorization_code").asText();
      
      return new BankGatewayResponse(authorized, authorizationCode);
    } catch (Exception e) {
      throw new BankGatewayException("Malformed response from bank gateway: " + response.body());
    }
  }

  private Object createBankPaymentRequest(PostPaymentRequest paymentRequest) {
    java.util.Map<String, Object> bankRequest = new java.util.HashMap<>();
    bankRequest.put("card_number", paymentRequest.getCardNumber());
    bankRequest.put("expiry_date", paymentRequest.getExpiryDate());
    bankRequest.put("currency", paymentRequest.getCurrency());
    bankRequest.put("amount", paymentRequest.getAmount());
    bankRequest.put("cvv", paymentRequest.getCvv());
    return bankRequest;
  }

  private static class BankGatewayResponse {
    public final boolean authorized;
    public final String authorizationCode;

    public BankGatewayResponse(boolean authorized, String authorizationCode) {
      this.authorized = authorized;
      this.authorizationCode = authorizationCode;
    }
  }
}
