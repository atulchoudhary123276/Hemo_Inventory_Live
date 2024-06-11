package com.example.service;

import com.example.model.UserModel;
import com.example.repository.DatabaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.time.LocalDateTime;

@Service
public class AdminService {
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    @Autowired
    DatabaseRepository databaseRepository;

    public void setAdmin() {
        UserModel admin = new UserModel();
        admin.setUserName("Admin");
        admin.setName("Admin....");
        admin.setDob(Date.valueOf("2000-01-01"));
        admin.setBloodGroup("B+");
        admin.setPassword("admin123");
        admin.setCreatedOn(LocalDateTime.now());
        admin.setCreatedBy("Self");
        admin.setRole("ADMIN");
        admin.setAddress("GZB");
        admin.setFirstLogin(false);  //for not gives the option of password updation at first login
        Iterable<UserModel> users = databaseRepository.findAll();
        if (!users.iterator().hasNext()) {
            databaseRepository.save(admin);
        } else {
            log.info("Admin already present");
        }
    }
//    @PostConstruct
//    public void unlockAdminLocked(){
//        UserModel admin = databaseRepository.findByUserName("Admin");
//        if (admin!=null){
//            admin.setLocked(false);
//            databaseRepository.save(admin);
//        }
//    }
}
