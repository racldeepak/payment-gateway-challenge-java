package com.checkout.payment.gateway.exception;

public class BankGatewayException extends RuntimeException {
  public BankGatewayException(String message) {
    super(message);
  }
}