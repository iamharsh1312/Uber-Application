package com.project.uber.uberApp.dto;

import com.project.uber.uberApp.entities.enums.PaymentMethod;
import com.project.uber.uberApp.entities.enums.RideStatus;

import java.time.LocalDateTime;

public class RideDto {
    private Long id;

    private PointDto pickupLocation;

    private PointDto dropOffLocation;

    private LocalDateTime createdTime;

    private DriverDto driver;
    private RiderDto rider;

    private PaymentMethod paymentMethod;

    private RideStatus rideStatus;

    private Double fare;
    private String otp;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
