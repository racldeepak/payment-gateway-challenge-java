package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;

public class PaymentDetails {
  private UUID id;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String authorizationCode;
  
  private PaymentStatus status;
  private int cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private int amount;

  public PaymentDetails() {}

  public PaymentDetails(UUID id, String authorizationCode, PaymentStatus status, 
                       int cardNumberLastFour, int expiryMonth, int expiryYear, 
                       String currency, int amount) {
    this.id = id;
    this.authorizationCode = authorizationCode;
    this.status = status;
    this.cardNumberLastFour = cardNumberLastFour;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.currency = currency;
    this.amount = amount;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getAuthorizationCode() {
    return authorizationCode;
  }

  public void setAuthorizationCode(String authorizationCode) {
    this.authorizationCode = authorizationCode;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public int getCardNumberLastFour() {
    return cardNumberLastFour;
  }

  public void setCardNumberLastFour(int cardNumberLastFour) {
    this.cardNumberLastFour = cardNumberLastFour;
  }

  public int getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(int expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public int getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(int expiryYear) {
    this.expiryYear = expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "PaymentDetails{" +
        "id=" + id +
        ", authorizationCode='" + authorizationCode + '\'' +
        ", status=" + status +
        ", cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        '}';
  }
}
