package com.checkout.payment.gateway.enums;

public enum Currency {
  USD("USD"),
  EUR("EUR"),
  GBP("GBP");

  private final String value;

  Currency(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}