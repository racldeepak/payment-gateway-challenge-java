package com.checkout.payment.gateway.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PostPaymentMalformedRequestTest {

  @Autowired
  private MockMvc mvc;
  
  @Autowired
  private ObjectMapper objectMapper;

  private Map<String, Object> getSampleValidPaymentRequest() {
    LocalDate today = LocalDate.now();
    Map<String, Object> request = new HashMap<>();
    request.put("cardNumber", "0123456789012345");
    request.put("expiryMonth", 12);
    request.put("expiryYear", today.getYear() + 1);
    request.put("currency", "USD");
    request.put("amount", 10);
    request.put("cvv", "123");
    return request;
  }

  private void assertPaymentRequest(Map<String, Object> paymentRequest) throws Exception {
    mvc.perform(post("/payment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Rejected: Invalid payment request"));
  }

  @Test
  void requestWithInvalidCardNumberReturns400() throws Exception {
    Map<String, Object> paymentRequest = getSampleValidPaymentRequest();
    
    String[] invalidCardNumbers = {
        null,
        "abcdefghijklmnop",
        "1234567890a12345",
        "1234",
        "1234567890123",
        "12345678901234567890"
    };

    for (String cardNumber : invalidCardNumbers) {
      paymentRequest.put("cardNumber", cardNumber);
      assertPaymentRequest(paymentRequest);
    }
  }

  @Test
  void requestWithInvalidExpiryMonthReturns400() throws Exception {
    Map<String, Object> paymentRequest = getSampleValidPaymentRequest();
    
    Object[] invalidExpiryMonths = {null, "1234", -1, 0, 13};

    for (Object expiryMonth : invalidExpiryMonths) {
      paymentRequest.put("expiryMonth", expiryMonth);
      assertPaymentRequest(paymentRequest);
    }
  }

  @Test
  void requestWithInvalidExpiryYearReturns400() throws Exception {
    Map<String, Object> paymentRequest = getSampleValidPaymentRequest();
    
    Object[] invalidExpiryYears = {null, "1234", 2024, 2032};

    for (Object expiryYear : invalidExpiryYears) {
      paymentRequest.put("expiryYear", expiryYear);
      assertPaymentRequest(paymentRequest);
    }
  }

  @Test
  void requestWithInvalidCurrencyReturns400() throws Exception {
    Map<String, Object> paymentRequest = getSampleValidPaymentRequest();
    
    Object[] invalidCurrencies = {null, "INVALID", 123};

    for (Object currency : invalidCurrencies) {
      paymentRequest.put("currency", currency);
      assertPaymentRequest(paymentRequest);
    }
  }

  @Test
  void requestWithInvalidAmountReturns400() throws Exception {
    Map<String, Object> paymentRequest = getSampleValidPaymentRequest();
    
    Object[] invalidAmounts = {null, "INVALID", -1, 0};

    for (Object amount : invalidAmounts) {
      paymentRequest.put("amount", amount);
      assertPaymentRequest(paymentRequest);
    }
  }

  @Test
  void requestWithInvalidCvvReturns400() throws Exception {
    Map<String, Object> paymentRequest = getSampleValidPaymentRequest();
    
    String[] invalidCvvs = {null, "1", "12", "12345", "INVALID"};

    for (String cvv : invalidCvvs) {
      paymentRequest.put("cvv", cvv);
      assertPaymentRequest(paymentRequest);
    }
  }

  @Test
  void requestWithExpiryDateInPastReturns400() throws Exception {
    Map<String, Object> paymentRequest = getSampleValidPaymentRequest();
    LocalDate today = LocalDate.now();
    
    // Test cases for dates in the past
    Object[][] invalidExpiryDates = {
        {today.getMonthValue(), today.getYear() - 1},
        {today.getMonthValue() - 1, today.getYear()}
    };

    for (Object[] expiryDate : invalidExpiryDates) {
      paymentRequest.put("expiryMonth", expiryDate[0]);
      paymentRequest.put("expiryYear", expiryDate[1]);
      assertPaymentRequest(paymentRequest);
    }
  }
}