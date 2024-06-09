package com.example.service;

import com.example.model.BloodStockModel;
import com.example.model.UserModel;
import com.example.model.UserRequestModel;
import com.example.repository.BloodStockRepository;
import com.example.repository.DatabaseRepository;
import com.example.repository.UserRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BloodReportService {
    @Autowired
    BloodStockRepository bloodStockRepository;
    @Autowired
    DatabaseRepository databaseRepository;
    @Autowired
    UserRequestRepository userRequestRepository;

    //for updating coin value
    public void coinUpdate(Long reqID) {
        UserRequestModel userRequestModel = userRequestRepository.getById(reqID);
        String bloodGroup = userRequestModel.getBloodGroup();
        String type = userRequestModel.getType();
        Integer quantity = Math.toIntExact(userRequestModel.getQuantity());
        Integer coinRate = bloodStockRepository.getByBloodGroup(bloodGroup).getCoinValue();
        String username = userRequestModel.getUser().getUserName();
        UserModel byUsername = databaseRepository.findByUserName(username);
        String createdBy = byUsername.getCreatedBy();
        Integer userCoinValue = byUsername.getCoinValue();
        UserModel adminModel = databaseRepository.findByUserName("Admin");
        Integer adminCoinValue = adminModel.getCoinValue();
        if (type.equalsIgnoreCase("donar")) {
            userCoinValue = userCoinValue + (quantity * coinRate);
            if (createdBy.equalsIgnoreCase("auto")) {
                adminCoinValue = adminCoinValue + (quantity * coinRate);
            } else {
                UserModel agentModel = databaseRepository.findByUserName(createdBy);
                Integer agentCoinValue = agentModel.getCoinValue();
                float commision = agentModel.getCommission();
                Integer agentCoinValue1 = (int) ((quantity * coinRate * commision) / 100);
                adminCoinValue = adminCoinValue + (userCoinValue - agentCoinValue1);
                agentModel.setCoinValue(agentCoinValue + agentCoinValue1);
                databaseRepository.save(agentModel);  //update coin of agent
            }
            byUsername.setCoinValue(userCoinValue);
            adminModel.setCoinValue(adminCoinValue);
            databaseRepository.save(adminModel);  //update coin of admin
            databaseRepository.save(byUsername);  //update coin of enduser
        }

    }

    //find  coin value for each request on  admin dashboard
    public List<Map<String, String>> findCoinReportForAdmin() {
        List<UserRequestModel> allUsersRequest = userRequestRepository.findAll();
        List<UserRequestModel> filteredRequests = allUsersRequest.stream()
                .filter(userRequestModel -> userRequestModel.getType().equals("donar"))
                .filter(userRequestModel -> userRequestModel.getStatus().equals("accepted"))
                .collect(Collectors.toList());
        return filteredRequests.stream()
                .map(request -> {
                    Map<String, String> row = new HashMap<>();
                    row.put("userName", request.getUser().getUserName());
                    row.put("bloodGroup", request.getBloodGroup());
                    Integer coinValue = request.getUser().getCoinValue();
                    row.put("userCoins", String.valueOf(coinValue));
                    String createdBy = request.getUser().getCreatedBy();
                    if (createdBy.equalsIgnoreCase("auto") || createdBy.equalsIgnoreCase("Self")
                            || createdBy.equalsIgnoreCase("Admin")) {
                        row.put("agentCoins", " - ");
                        row.put("adminCoins", String.valueOf(coinValue));
                    } else {
                        int coinValue1 = (int) (coinValue * databaseRepository.findByUserName(createdBy).getCommission() / 100);
                        row.put("agentCoins", String.valueOf(coinValue1));
                        row.put("adminCoins", String.valueOf(coinValue - coinValue1));

                    }
                    return row;
                }).collect(Collectors.toList());

    }

    //find coin value by bloodgroup and rate
    public HashMap<String, Object[]> findCoinByBloodGroup(String userNameToCheck) {
        List<UserRequestModel> allUsersRequest = userRequestRepository.findAll();
        HashMap<String, Object[]> bloodGroupCoinsMap = new HashMap<>();
        List<UserRequestModel> collect = allUsersRequest.stream()
                .filter(userRequestModel -> userRequestModel.getUser().getCreatedBy().equals(userNameToCheck))
                .filter(userRequestModel -> userRequestModel.getType().equals("donar"))
                .filter(userRequestModel -> userRequestModel.getStatus().equals("accepted"))
                .collect(Collectors.toList());
        float commission = databaseRepository.findByUserName(userNameToCheck).getCommission();
        for (UserRequestModel userRequestModel : collect) {
            String bloodGroup = userRequestModel.getBloodGroup();
            BloodStockModel byBloodGroup = bloodStockRepository.getByBloodGroup(bloodGroup);
            double coinsRate = byBloodGroup.getCoinValue();
            double rate = commission;
            int userCoins = (int) (userRequestModel.getQuantity() * coinsRate);
            int agentCoins = (int) (userCoins * commission / 100);

            // Update blood group coins map
            if (bloodGroupCoinsMap.containsKey(bloodGroup)) {
                Object[] coinsAndRate = bloodGroupCoinsMap.get(bloodGroup);
                coinsAndRate[0] = (int) coinsAndRate[0] + userCoins; // Update user coins
                coinsAndRate[1] = (int) coinsAndRate[1] + agentCoins; // Update agent coins
            } else {
                Object[] coinsAndRate = new Object[]{userCoins, agentCoins, rate};
                bloodGroupCoinsMap.put(bloodGroup, coinsAndRate);
            }
        }
        return bloodGroupCoinsMap;
    }

    //for finding the blood group wise report of reject and approved requests of donar type
    public List<Map<String, String>> getRequestReport(String role, String userName) {
        List<UserRequestModel> allRequestsList = new ArrayList<>();
        if (role.equalsIgnoreCase("ADMIN")) {
            allRequestsList = userRequestRepository.findAll();
        } else {
            allRequestsList = userRequestRepository.findAll().stream()
                    .filter(userRequestModel -> userRequestModel.getUser().getCreatedBy().equals(userName))
                    .collect(Collectors.toList());
        }
        // Initialize counters for each blood group
        Map<String, Integer> approvedCounts = new HashMap<>();
        Map<String, Integer> rejectedCounts = new HashMap<>();
        Map<String, Long> quantityCounts = new HashMap<>();

        // Iterate through the list of requests
        for (UserRequestModel request : allRequestsList) {
            String bloodGroup = request.getBloodGroup();
            String status = request.getStatus();
            String type = request.getType();
            // Update approved/rejected counts based on status
            if (status.equalsIgnoreCase("accepted") && type.equalsIgnoreCase("donar")) {
                quantityCounts.put(bloodGroup, quantityCounts.getOrDefault(bloodGroup, 0L) + request.getQuantity());
                approvedCounts.put(bloodGroup, approvedCounts.getOrDefault(bloodGroup, 0) + 1);
            } else if (status.equalsIgnoreCase("rejected") && type.equalsIgnoreCase("donar")) {
                rejectedCounts.put(bloodGroup, rejectedCounts.getOrDefault(bloodGroup, 0) + 1);
            }
        }
        // Prepare the report
        List<Map<String, String>> report = new ArrayList<>();
        List<BloodStockModel> all = bloodStockRepository.findAll();
        for (BloodStockModel blood : all) {
            int approved = approvedCounts.getOrDefault(blood.getBloodGroup(), 0);
            int rejected = rejectedCounts.getOrDefault(blood.getBloodGroup(), 0);
            long quantity = quantityCounts.getOrDefault(blood.getBloodGroup(), 0L);
            Map<String, String> row = new HashMap<>();
            row.put("bloodGroup", blood.getBloodGroup());
            row.put("approvedRequests", String.valueOf(approved));
            row.put("rejectedRequests", String.valueOf(rejected));
            row.put("coinValue", String.valueOf(blood.getCoinValue() * quantity));
            report.add(row);
        }

        return report;

    }
    //get coin value
    public Integer getCoinvalue(String userName){
         return databaseRepository.getCoinValue(userName);
    }

    // Method to calculate total user coins
    public int calculateTotalCoinsForAd(List<Map<String, String>> coinReports, String key) {
        int total = 0;
        for (Map<String, String> report : coinReports) {
            String value = report.get(key);
            if (value != null && !value.equals(" - ")) {
                total += Integer.parseInt(value);
            }
        }
        return total;
    }

    // Method to calculate total coins based on index (0 for user, 1 for agent, 2 for admin)
    public double calculateTotalCoinsForAg(Map<String, Object[]> bloodGroupCoinsMap, int index) {
        double total = 0.0;
        for (Object[] coinsAndRate : bloodGroupCoinsMap.values()) {
            Object coinValue = coinsAndRate[index];
            if (coinValue instanceof Integer) {
                total += (Integer) coinValue;
            } else if (coinValue instanceof Double) {
                total += (Double) coinValue;
            }
        }
        return total;
    }
}
