package com.example.service;

import com.example.dto.UserRequestDto;
import com.example.model.BloodStockModel;
import com.example.model.UserRequestModel;
import com.example.repository.BloodStockRepository;
import com.example.repository.DatabaseRepository;
import com.example.repository.UserRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BloodRequestsService {
    @Autowired
    private UserRequestRepository userRequestRepository;
    @Autowired
    BloodStockService bloodStockService;
    @Autowired
    BloodReportService bloodReportService;
    @Autowired
    UserRequestService userRequestService;

    public List<HashMap<String, Object>> getBloodBankList() {
        List<UserRequestModel> list = userRequestRepository.findAll();
        List<HashMap<String, Object>> resultList = new ArrayList<>();
        for (UserRequestModel stock : list) {
            HashMap<String, Object> myResult = new HashMap<>();
            myResult.put("id", stock.getId());
            myResult.put("bloodGroup", stock.getBloodGroup());
            myResult.put("type", stock.getType());
            myResult.put("status", stock.getStatus());
            myResult.put("quantity", stock.getQuantity());
            myResult.put("createdBy", stock.getUser().getUserName());
            myResult.put("name", stock.getUser().getName());
            myResult.put("dob", stock.getUser().getDob());
            myResult.put("createdAt", stock.getCreatedOn());
            myResult.put("agent", stock.getCreatedBy());
            myResult.put("age", stock.getAge());
            myResult.put("remark", stock.getRemark());
            resultList.add(myResult);
        }
        Collections.reverse(resultList);
        return resultList;
    }

    //for showing user request on agent dashboard and admin
    public List<HashMap<String, Object>> agentUserRequest(List<HashMap<String, Object>> allRequestedUsers, String userNameToCheck) {
        List<HashMap<String, Object>> selectedReuqest = new ArrayList<>();
        for (HashMap<String, Object> userMap : allRequestedUsers) {
            String createdBy = (String) userMap.get("agent");
            if (createdBy != null && createdBy.equals(userNameToCheck)) {
                selectedReuqest.add(userMap);
            }
        }
        return selectedReuqest;
    }

    //for showing all requests by each user
    public List<HashMap<String, Object>> findEnduserRequest(List<HashMap<String, Object>> allRequestedUsers, String userNameToCheck) {
        List<HashMap<String, Object>> selectedRequest = new ArrayList<>();
        for (HashMap<String, Object> userMap : allRequestedUsers) {
            String userName = (String) userMap.get("createdBy");
            if (userName != null && userName.equals(userNameToCheck)) {
                selectedRequest.add(userMap);
            }
        }
        return selectedRequest;
    }

    public String updateApprovedRequest(String reqId, String status) {
        UserRequestModel model = userRequestRepository.getById(Long.valueOf(reqId));
        String checkForAvailableUnit = bloodStockService.setBloodStockRepository(model);
        if (checkForAvailableUnit.equalsIgnoreCase("ok")) {
            if (model.getType().equalsIgnoreCase("donar")) {
                bloodReportService.coinUpdate(Long.valueOf(reqId));  //for updating coin value
                if (!userRequestService.isEligibleForDonation(model.getUser())){
                    return "donar request is not accepted";
                }
            }
            model.setStatus(status);
            userRequestRepository.save(model);
            return "status updated";
        } else {
            return checkForAvailableUnit;
        }

    }

    public void updateRejectRequest(String reqId, String status, String remark) {
        UserRequestModel model = userRequestRepository.getById(Long.valueOf(reqId));
        model.setStatus(status);
        model.setRemark(remark);
        userRequestRepository.save(model);

    }

    //For blood request date filter
    public List<HashMap<String, Object>> filterBloodRequestByDate(String startDate, String endDate, List<HashMap<String, Object>> list) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);


        // Filter the list based on createdAt field
        return list.stream()
                .filter(user -> {
                    LocalDateTime userDateTime = (LocalDateTime) user.get("createdAt");
                    return userDateTime.compareTo(startDateTime) >= 0 && userDateTime.compareTo(endDateTime) <= 0;
                })
                .collect(Collectors.toList());
    }

    // filter by status, username and agent
    public List<HashMap<String, Object>> filterBloodRequest(List<HashMap<String, Object>> list, String filter, String input, String status, String startDate, String endDate) throws ParseException {
        if (filter == null) {
            return list;
        }
        switch (filter) {
            case "byStatus":
                return list.stream().filter(i -> i.get("status").equals(status)).collect(Collectors.toList());
            case "byAgent":
                return list.stream().filter(i -> !i.get("agent").toString().equalsIgnoreCase("auto")).collect(Collectors.toList());
            case "byUsername":
                return list.stream().filter(i -> i.get("createdBy").toString().equals(input)).collect(Collectors.toList());
            case "createdBetween":
                return filterBloodRequestByDate(startDate, endDate, list);
            default:
                return list;
        }
    }


}
