package com.example.repository;

import com.example.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DatabaseRepository extends JpaRepository<UserModel, Long> {
    UserModel findByUserName(String userName);

    @Query("SELECT u FROM UserModel u WHERE u.role = :role1 OR u.role = :role2")
    List<UserModel> findAllByRoleIn(String role1, String role2);
    List<UserModel> findAllByCreatedBy(String userName);
    @Query("SELECT coinValue FROM UserModel  WHERE userName=:userName1")
    Integer getCoinValue(String userName1);
}
