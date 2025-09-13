package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.exception.InvalidPaymentException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import java.time.LocalDate;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class PaymentValidationService {

  public void validatePaymentRequest(PostPaymentRequest request) {
    validateCardNumber(request.getCardNumber());
    validateExpiryMonth(request.getExpiryMonth());
    validateExpiryYear(request.getExpiryYear());
    validateExpiryDate(request.getExpiryMonth(), request.getExpiryYear());
    validateCurrency(request.getCurrency());
    validateCvv(request.getCvv());
    validateAmount(request.getAmount());
  }

  private void validateCardNumber(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 14 || cardNumber.length() > 19 || !cardNumber.matches("\\d+")) {
      throw new InvalidPaymentException("Card number must be a numeric string between 14 and 19 digits long");
    }
  }

  private void validateExpiryMonth(int expiryMonth) {
    if (expiryMonth < 1 || expiryMonth > 12) {
      throw new InvalidPaymentException("Expiry month must be between 1 and 12");
    }
  }

  private void validateExpiryYear(int expiryYear) {
    int currentYear = LocalDate.now().getYear();
    if (expiryYear < currentYear || expiryYear > currentYear + 6) {
      throw new InvalidPaymentException("Invalid expiry year");
    }
  }

  private void validateExpiryDate(int expiryMonth, int expiryYear) {
    LocalDate today = LocalDate.now();
    LocalDate expiryDate = LocalDate.of(expiryYear, expiryMonth, today.getDayOfMonth());
    if (expiryDate.isBefore(today)) {
      throw new InvalidPaymentException("Expiry date cannot be in the past");
    }
  }

  private void validateCurrency(String currency) {
    if (currency == null || currency.trim().isEmpty()) {
      throw new InvalidPaymentException("Currency is required");
    }
    
    boolean isValidCurrency = Arrays.stream(Currency.values())
        .anyMatch(c -> c.getValue().equals(currency));
    
    if (!isValidCurrency) {
      throw new InvalidPaymentException("Invalid currency. Supported currencies: USD, EUR, GBP");
    }
  }

  private void validateCvv(int cvv) {
    if (cvv < 100 || cvv > 9999) {
      throw new InvalidPaymentException("CVV must be a 3 or 4-digit number");
    }
  }

  private void validateAmount(int amount) {
    if (amount <= 0) {
      throw new InvalidPaymentException("Invalid amount");
    }
  }
}
