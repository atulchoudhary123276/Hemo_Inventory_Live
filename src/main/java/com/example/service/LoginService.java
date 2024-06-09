package com.example.service;

import org.modelmapper.ModelMapper;
import com.example.dto.UserLoginDto;
import com.example.dto.UserRegisterDto;
import com.example.model.UserModel;
import com.example.repository.DatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService {
    @Autowired
    DatabaseRepository databaseRepository;
    @Autowired
    private ModelMapper modelMapper;

    //1st approach
    public UserRegisterDto checkUser(UserLoginDto userLoginDto) {
        //1st approach using getter and setter for converting model to dto
//                UserRegisterDto returnDto = new UserRegisterDto();
//                returnDto.setName(userModel.getName());
//                returnDto.setUserName(userModel.getUserName());
//                returnDto.setPassword(userModel.getPassword());
//                returnDto.setDob(userModel.getDob());
//                returnDto.setBloodGroup(userModel.getBloodGroup());
//                returnDto.setCreatedBy(userModel.getCreatedBy());
//                returnDto.setCreatedOn(userModel.getCreatedOn());
//                returnDto.setRole(userModel.getRole());

        //2nd approach use ModelMapper to convert model entity into Dto
        UserModel user = databaseRepository.findByUserName(userLoginDto.getUserName());
        if (user==null){
            return null;
        }
        if (userLoginDto.getPassword().equals(user.getPassword())) {
            // Successful login
            user.setLoginAttempts(0);
            databaseRepository.save(user);
            return convertToDto(user);
        } else {
            // Failed login attempt
            handleFailedLogin(user);
            return convertToDto(user);   //return with more than 0 login attemp
        }

    }

    //use for converting model to Dto using modelMapper
    private UserRegisterDto convertToDto(UserModel userModel) {
        return modelMapper.map(userModel, UserRegisterDto.class);
    }


    private void handleFailedLogin(UserModel user) {
        if (user != null) {
            // Increment login attempts
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            // Check if login attempts exceed the limit
            if (user.getLoginAttempts() >= 3) {
                // Lock the user account
                user.setLocked(true);
            }
            databaseRepository.save(user);
        }
    }

    public String updatePassword(UserLoginDto userLoginDto) {
        if (userLoginDto.getUserName()==null){
            return null;
        }
        UserModel user = databaseRepository.findByUserName(userLoginDto.getUserName());
        if (user.getPassword().equals(userLoginDto.getConfirmPassword())){
            user.setPassword(userLoginDto.getPassword());
            user.setFirstLogin(false);
            databaseRepository.save(user);
            return "ok";
        }
       return "mismatch";

    }

}
