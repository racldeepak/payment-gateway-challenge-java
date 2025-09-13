package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.PaymentDetails;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository {

  private final HashMap<UUID, PaymentDetails> payments = new HashMap<>();

  public void add(PaymentDetails payment) {
    payments.put(payment.getId(), payment);
  }

  public Optional<PaymentDetails> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

}
