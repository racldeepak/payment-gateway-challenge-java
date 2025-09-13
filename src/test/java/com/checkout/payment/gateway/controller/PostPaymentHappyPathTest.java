package com.checkout.payment.gateway.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class PostPaymentHappyPathTest {

  @Autowired
  private MockMvc mvc;
  
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void requestWithValidCardIsAuthorised() throws Exception {
    Map<String, Object> paymentRequest = new HashMap<>();
    paymentRequest.put("cardNumber", "1111111111111111");
    paymentRequest.put("expiryMonth", 12);
    paymentRequest.put("expiryYear", 2025);
    paymentRequest.put("currency", "USD");
    paymentRequest.put("amount", 10);
    paymentRequest.put("cvv", 123);

    mvc.perform(post("/payment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(1111))
        .andExpect(jsonPath("$.expiryMonth").value(12))
        .andExpect(jsonPath("$.expiryYear").value(2025))
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.amount").value(10));
  }

  @Test
  void requestWithInvalidCardIsRejected() throws Exception {
    Map<String, Object> paymentRequest = new HashMap<>();
    paymentRequest.put("cardNumber", "2222222222222222");
    paymentRequest.put("expiryMonth", 12);
    paymentRequest.put("expiryYear", 2025);
    paymentRequest.put("currency", "GBP");
    paymentRequest.put("amount", 10);
    paymentRequest.put("cvv", 123);

    mvc.perform(post("/payment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(2222))
        .andExpect(jsonPath("$.expiryMonth").value(12))
        .andExpect(jsonPath("$.expiryYear").value(2025))
        .andExpect(jsonPath("$.currency").value("GBP"))
        .andExpect(jsonPath("$.amount").value(10));
  }

  @Test
  void paymentRequestsArePersisted() throws Exception {
    Map<String, Object> paymentRequest = new HashMap<>();
    paymentRequest.put("cardNumber", "1111111111111111");
    paymentRequest.put("expiryMonth", 12);
    paymentRequest.put("expiryYear", 2025);
    paymentRequest.put("currency", "USD");
    paymentRequest.put("amount", 10);
    paymentRequest.put("cvv", 123);

    MvcResult postResult = mvc.perform(post("/payment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andReturn();

    String responseContent = postResult.getResponse().getContentAsString();
    @SuppressWarnings("unchecked")
    Map<String, Object> postResponse = (Map<String, Object>) objectMapper.readValue(responseContent, Map.class);
    String paymentId = (String) postResponse.get("id");

    mvc.perform(get("/payment/" + paymentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(paymentId))
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(1111))
        .andExpect(jsonPath("$.expiryMonth").value(12))
        .andExpect(jsonPath("$.expiryYear").value(2025))
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.amount").value(10));
  }
}