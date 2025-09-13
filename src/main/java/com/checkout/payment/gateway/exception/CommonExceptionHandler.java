package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleEventProcessingException(EventProcessingException ex) {
    LOG.error("EventProcessingException happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Page not found"),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidPaymentException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPaymentException(InvalidPaymentException ex) {
    LOG.error("InvalidPaymentException happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Rejected: Invalid payment request"),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BankGatewayException.class)
  public ResponseEntity<ErrorResponse> handleBankGatewayException(BankGatewayException ex) {
    LOG.error("BankGatewayException happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Error processing payment"),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
