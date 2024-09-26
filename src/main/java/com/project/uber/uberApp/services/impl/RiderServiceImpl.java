package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.RideRequestDto;
import com.project.uber.uberApp.dto.RiderDto;
import com.project.uber.uberApp.entities.*;
import com.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.project.uber.uberApp.entities.enums.RideStatus;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.RideRequestRepository;
import com.project.uber.uberApp.repositories.RiderRepository;
import com.project.uber.uberApp.services.DriverService;
import com.project.uber.uberApp.services.RideService;
import com.project.uber.uberApp.services.RiderService;
import com.project.uber.uberApp.strategies.RideStrategyManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Slf4j
@Service
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper; // Ensure consistent naming
    private final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final DriverService driverService;

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {
        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);
            Rider   rider = getCurrentRider();
        log.info(rideRequest.toString());
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);
        Double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);

        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        List<Driver> drivers = rideStrategyManager.driverMatchingStrategy(rider.getRating()).findMatchingDriver(rideRequest);
        return modelMapper.map(savedRideRequest, RideRequestDto.class);
    }



    @Override
    public RideDto cancelRide(Long rideId) {

        Rider curreRider = getCurrentRider();
        Ride ride = rideService.getRideById(rideId);
        if(!curreRider.equals(ride.getRider())){
            throw new RuntimeException("Rider is not allowed to cancel the ride with id " + rideId);
        }
        if(ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride Cannot be cancelled " + ride.getRideStatus());
        }
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.CANCELLED);
        driverService.updateDriverAvailability(ride.getDriver(),true);

        return modelMapper.map(savedRide, RideDto.class);


    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {
        return null;
    }

    @Override
    public RiderDto getMyProfile() {


        Rider currentRider  = getCurrentRider();
        return modelMapper.map(currentRider, RiderDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {

        Rider currentRider = getCurrentRider();
        return  rideService.getAllRidesOfRider(currentRider , pageRequest).map(
                ride -> modelMapper.map(ride, RideDto.class));

    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider =  Rider.builder().user(user).rating(0.0).build();

        return riderRepository.save(rider);
    }
    @Override
    public Rider getCurrentRider(){
        return riderRepository.findById(1L).orElseThrow(()-> new ResourceNotFoundException(
                "Rider not found with id " + 1
        ));
    }
}
