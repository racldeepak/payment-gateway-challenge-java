package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.exception.InvalidPaymentException;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PaymentDetails;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.checkout.payment.gateway.service.PaymentValidationService;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;
  private final PaymentValidationService validationService;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService, 
                                 PaymentValidationService validationService) {
    this.paymentGatewayService = paymentGatewayService;
    this.validationService = validationService;
  }

  @GetMapping("/")
  public ResponseEntity<Map<String, String>> ping() {
    Map<String, String> response = Map.of("message", "pong");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/payment")
  public ResponseEntity<PostPaymentResponse> processPayment(@RequestBody Map<String, Object> paymentMap) {
    PostPaymentRequest payment = convertMapToPostPaymentRequest(paymentMap);
    validationService.validatePaymentRequest(payment);
    PaymentDetails paymentDetails = paymentGatewayService.processPayment(payment);
    PostPaymentResponse response = convertToPostPaymentResponse(paymentDetails);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/payment/{id}")
  public ResponseEntity<GetPaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    PaymentDetails paymentDetails = paymentGatewayService.getPaymentById(id);
    GetPaymentResponse response = convertToGetPaymentResponse(paymentDetails);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private PostPaymentRequest convertMapToPostPaymentRequest(Map<String, Object> paymentMap) {
    try {
      PostPaymentRequest request = new PostPaymentRequest();
      request.setCardNumber((String) paymentMap.get("cardNumber"));
      request.setExpiryMonth((Integer) paymentMap.get("expiryMonth"));
      request.setExpiryYear((Integer) paymentMap.get("expiryYear"));
      request.setCurrency((String) paymentMap.get("currency"));
      request.setAmount((Integer) paymentMap.get("amount"));
      request.setCvv((Integer) paymentMap.get("cvv"));
      return request;
    } catch (Exception e) {
      throw new InvalidPaymentException("Invalid payment request format: " + e.getMessage());
    }
  }

  private PostPaymentResponse convertToPostPaymentResponse(PaymentDetails paymentDetails) {
    PostPaymentResponse response = new PostPaymentResponse();
    response.setId(paymentDetails.getId());
    response.setStatus(paymentDetails.getStatus());
    response.setCardNumberLastFour(paymentDetails.getCardNumberLastFour());
    response.setExpiryMonth(paymentDetails.getExpiryMonth());
    response.setExpiryYear(paymentDetails.getExpiryYear());
    response.setCurrency(paymentDetails.getCurrency());
    response.setAmount(paymentDetails.getAmount());
    return response;
  }

  private GetPaymentResponse convertToGetPaymentResponse(PaymentDetails paymentDetails) {
    GetPaymentResponse response = new GetPaymentResponse();
    response.setId(paymentDetails.getId());
    response.setStatus(paymentDetails.getStatus());
    response.setCardNumberLastFour(paymentDetails.getCardNumberLastFour());
    response.setExpiryMonth(paymentDetails.getExpiryMonth());
    response.setExpiryYear(paymentDetails.getExpiryYear());
    response.setCurrency(paymentDetails.getCurrency());
    response.setAmount(paymentDetails.getAmount());
    return response;
  }
}
