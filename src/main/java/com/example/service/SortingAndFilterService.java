package com.example.service;

import com.example.dto.UserRegisterDto;
import com.example.model.UserModel;
import com.example.repository.DatabaseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SortingAndFilterService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    DatabaseRepository databaseRepository;
    public List<UserRegisterDto> sortBy(String sortBy,List<UserRegisterDto> allUsers){
        switch (sortBy){
            case "byname":{
                return allUsers.stream().sorted(
                        Comparator.comparing(userRegisterDto -> userRegisterDto.getName().toLowerCase()))
                        .collect(Collectors.toList());
            }
            case "createdby":{
                return allUsers.stream().sorted(
                                Comparator.comparing(UserRegisterDto::getCreatedBy))
                        .collect(Collectors.toList());
            }
            case "bybloodgroup":{
                return allUsers.stream().sorted(
                                Comparator.comparing(UserRegisterDto::getBloodGroup))
                        .collect(Collectors.toList());

            }
            default:return allUsers;

        }
    }
    public List<UserRegisterDto> filterByUsername(String userName, List<UserRegisterDto> allUsers){
        return allUsers.stream().filter(user->user.getUserName().equals(userName)).collect(Collectors.toList());
    }
    public List<UserRegisterDto> filterByAgent(List<UserRegisterDto> allUsers,String userName){
        return allUsers.stream()
                .filter(user -> user.getCreatedBy().equals(userName))
                .collect(Collectors.toList());

    }
    public List<UserRegisterDto> filterByDate(String startDate, String endDate, List<UserRegisterDto> allUsers)  {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);

        return allUsers.stream()
                .filter(user -> {
                    LocalDateTime userDateTime = user.getCreatedOn();
                    return userDateTime.compareTo(startDateTime) >= 0 && userDateTime.compareTo(endDateTime) <= 0;
                })
                .collect(Collectors.toList());
    }


    private List<UserRegisterDto> convertToDtoList(List<UserModel> userModels) {
        List<UserRegisterDto> users=new ArrayList<>();
        for (UserModel user :userModels) {
            users.add(modelMapper.map(user, UserRegisterDto.class));
        }
        return users;
    }
    public List<UserRegisterDto> getAllUsersByRole(){
        return convertToDtoList(databaseRepository.findAllByRoleIn("ENDUSER","AGENT"));

    }
    public List<UserRegisterDto> getAllUsersByUserName(String userName){
        return convertToDtoList(databaseRepository.findAllByCreatedBy(userName));
    }
    private static final Map<String, Boolean> allActiveUsers = new HashMap<>();

    public void updateActiveUsers(String userName, String cause){
        if(userName != null){
            if(cause.equalsIgnoreCase("add")){
                allActiveUsers.put(userName, true);
            }
            if(cause.equalsIgnoreCase("remove")) {
                allActiveUsers.remove(userName);
            }
        }
    }

//    public Map<String, Boolean> getAllActiveUsers() {
//        return allActiveUsers;
//    }

    public List<UserRegisterDto> notLoggedInUser(List<UserRegisterDto> allUserList){
        List<UserRegisterDto> notLoggedInUsers = new ArrayList<>();
        for (UserRegisterDto user : allUserList) {
            if (!allActiveUsers.containsKey(user.getUserName())) {
                notLoggedInUsers.add(user);
            }
        }
        return notLoggedInUsers;
    }
}
