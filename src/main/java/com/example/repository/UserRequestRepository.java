package com.example.repository;

import com.example.model.BloodStockModel;
import com.example.model.UserModel;
import com.example.model.UserRequestModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRequestRepository extends JpaRepository<UserRequestModel, Long> {
    List<UserRequestModel> findAllByuserId(UserModel userModel);
}

