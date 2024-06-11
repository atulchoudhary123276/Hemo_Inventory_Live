package com.example;

import com.example.service.AdminService;
import com.example.service.BloodStockService;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
@SpringBootApplication
public class AtulBloodBankProjectApplication {
    private static final Logger log = LoggerFactory.getLogger(AtulBloodBankProjectApplication.class);

    public static void main(String[] args) {
        log.info("::::::::::::::Application Starting:::::::::");
        ConfigurableApplicationContext context = SpringApplication.run(AtulBloodBankProjectApplication.class, args);
        AdminService adminService = context.getBean(AdminService.class);
        BloodStockService bloodStockService=context.getBean(BloodStockService.class);
        adminService.setAdmin();  //for set admin at application starting
        bloodStockService.initializeBloodStock(); //for setting the stock table
        log.info("::::::::::::::Application Started:::::::::");
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
