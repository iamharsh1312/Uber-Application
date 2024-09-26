package com.project.uber.uberApp.services;

import com.project.uber.uberApp.entities.Payment;
import com.project.uber.uberApp.entities.Ride;

public interface PaymentService {

    void processPayment(Payment payment);

    Payment createNewPayment(Ride ride);
}
