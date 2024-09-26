package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
