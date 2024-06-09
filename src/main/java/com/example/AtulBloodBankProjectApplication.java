package com.example;

import com.example.service.AdminService;
import com.example.service.BloodStockService;
import org.modelmapper.ModelMapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
@SpringBootApplication
public class AtulBloodBankProjectApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AtulBloodBankProjectApplication.class, args);
        AdminService adminService = context.getBean(AdminService.class);
        BloodStockService bloodStockService=context.getBean(BloodStockService.class);
        adminService.setAdmin();  //for set admin at application starting
        bloodStockService.initializeBloodStock(); //for setting the stock table

    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
