package com.example.service;

import com.example.dto.UserRegisterDto;
import com.example.dto.UserRequestDto;
import com.example.model.BloodStockModel;
import com.example.model.UserModel;
import com.example.model.UserRequestModel;
import com.example.repository.BloodStockRepository;
import com.example.repository.DatabaseRepository;
import com.example.repository.UserRequestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserRequestService {
    @Autowired
    UserRequestRepository userRequestRepository;
    @Autowired
    DatabaseRepository databaseRepository;

    public String handleBloodRequirementRequest(UserRequestDto bloodRequestDTO, String userName) {
        UserRequestModel bloodBank = new UserRequestModel();
        bloodBank.setBloodGroup(bloodRequestDTO.getBloodGroup());
        bloodBank.setType(bloodRequestDTO.getType());
        bloodBank.setQuantity(bloodRequestDTO.getQuantity());
        bloodBank.setCreatedOn(LocalDateTime.now());
        bloodBank.setUpdatedTime(LocalDateTime.now());
        bloodBank.setUpdatedBy("admin");
        bloodBank.setStatus("pending");
        bloodBank.setRemark(" - ");
        UserModel userModel = databaseRepository.findByUserName(userName);
        bloodBank.setUser(userModel);
        bloodBank.setCreatedBy(userModel.getCreatedBy());
        // Calculate age based on date of birth
        if (userModel.getDob() != null) {
            Date dob = userModel.getDob();
            LocalDate today = LocalDate.now();
            LocalDate birthDate = dob.toLocalDate();
            bloodBank.setAge(Period.between(birthDate, today).getYears());

        }
        if (bloodRequestDTO.getType().equalsIgnoreCase("donar")) {
            if (!isEligibleForDonation(userModel)) {
                return "Please Donate Blood After 3 months";
            }
            userRequestRepository.save(bloodBank);

        }
        userRequestRepository.save(bloodBank);
        return "Successfully requested";
    }

    public boolean isEligibleForDonation(UserModel userModel) {
        List<UserRequestModel> allByuserId = userRequestRepository.findAllByuserId(userModel);
        List<UserRequestModel> collect = allByuserId.stream().filter(userRequestModel -> userRequestModel.getType().equalsIgnoreCase("donar"))
                .filter(userRequestModel -> userRequestModel.getStatus().equalsIgnoreCase("accepted"))
                .collect(Collectors.toList());
        // Find the nearest update based on the time difference
        Optional<UserRequestModel> nearestUpdate = collect.stream()
                .min(Comparator.comparingLong(userRequestModel ->
                        Math.abs(userRequestModel.getUpdatedTime().until(LocalDateTime.now(), java.time.temporal.ChronoUnit.SECONDS))));

        if (nearestUpdate.isEmpty()) {
            return true;
        }
        // Calculate current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Check if three months have passed since last donation
        return nearestUpdate.get().getUpdatedTime().plusMonths(3).isBefore(currentDateTime);

    }

}
