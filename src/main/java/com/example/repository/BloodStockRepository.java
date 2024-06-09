package com.example.repository;

import com.example.model.BloodStockModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodStockRepository extends JpaRepository<BloodStockModel,Long> {
    BloodStockModel getByBloodGroup(String bloodgroup);
    List<BloodStockModel> findAllByBloodGroup(String bloodGroup);

}
