package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.RiderDto;
import com.project.uber.uberApp.entities.Driver;
import com.project.uber.uberApp.entities.Ride;
import com.project.uber.uberApp.entities.RideRequest;
import com.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.project.uber.uberApp.entities.enums.RideStatus;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.DriverRepository;
import com.project.uber.uberApp.services.DriverService;
import com.project.uber.uberApp.services.RideRequestService;
import com.project.uber.uberApp.services.RideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceimpl implements DriverService {

    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;


    @Override
    public RideDto cancelRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);

        Driver driver = getCurrentDriver();
        if(!driver.getAvailable()){
            throw new RuntimeException("Driver cannot accept due to unavailability");
        }

        if(ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride Cannot be cancelled " + ride.getRideStatus());
        }
        rideService.updateRideStatus(ride, RideStatus.CANCELLED);

        updateDriverAvailability(driver, true);
        return modelMapper.map(ride, RideDto.class);
    }


   private final RideRequestService rideRequestService;
    @Override
    @Transactional
    public RideDto acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);
        if(rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING))
        {
            throw new RuntimeException("RideRequest cannot be accepted, status is " + rideRequest.getRideRequestStatus());
        }

        Driver currDriver = getCurrentDriver();
        if(!currDriver.getAvailable()){
            throw new RuntimeException("Driver cannot accept due to unavailability");
        }

        Driver savedDriver = updateDriverAvailability(currDriver, false);


        Ride ride = rideService.createNewRide(rideRequest, savedDriver);

        return modelMapper.map(ride, RideDto.class);
    }

    @Override
    public RideDto startRide(Long rideId, String otp) {
       Ride ride = rideService.getRideById(rideId);
       Driver driver = getCurrentDriver();

       if(!driver.equals(ride.getDriver())){
           throw new RuntimeException("Driver cannot start a ride he has not accepted it earlier");
       }
       if(ride.getRideStatus().equals(RideStatus.CONFIRMED)){
           throw new RuntimeException("Ride status is not confirmed hence cannot be started, status "  +ride.getRideStatus());
       }
       if(otp.equals(ride.getOtp())){
           throw new RuntimeException("OTP is not valid " + otp);
       }

      ride.setStartedAt(LocalDateTime.now());
       Ride savedRide  = rideService.updateRideStatus(ride, RideStatus.ONGOING);
       return modelMapper.map(savedRide, RideDto.class);
    }

    @Override
    public RideDto endRide(Long rideId) {
        return null;
    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {
        return null;
    }

    @Override
    public DriverDto getMyProfile() {
        Driver currDriver = getCurrentDriver();
        return modelMapper.map(currDriver, DriverDto.class);

    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {
        Driver currDriver = getCurrentDriver();
        return rideService.getAllRidesOfDriver(currDriver, pageRequest).map(
                ride -> modelMapper.map(ride, RideDto.class)
        );
    }

    @Override
    public Driver getCurrentDriver() {
        return driverRepository.findById(2L).orElseThrow(()->new ResourceNotFoundException("Driver not found with" + "id" + 2));
    }


    @Override
    public Driver updateDriverAvailability(Driver driver , boolean available){
        driver.setAvailable(true);
        return driverRepository.save(driver);
    }
}
