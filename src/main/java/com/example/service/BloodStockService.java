package com.example.service;

import com.example.dto.BloodStockDto;
import com.example.model.BloodStockModel;
import com.example.model.UserModel;
import com.example.model.UserRequestModel;
import com.example.repository.BloodStockRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class BloodStockService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    BloodStockRepository bloodStockRepository;
    public List<BloodStockDto> getBloodStock(){
         return convertToDto(bloodStockRepository.findAll());
    }
    private List<BloodStockDto> convertToDto(List<BloodStockModel> stockModels){
        List<BloodStockDto> users=new ArrayList<>();
        for (BloodStockModel user :stockModels) {
            users.add(modelMapper.map(user, BloodStockDto.class));
        }
        return users;
    }
    public void initializeBloodStock() {
        HashMap<String,Integer> bloodGroups=new HashMap<>();
        bloodGroups.put("A+",10);bloodGroups.put("A-",15);bloodGroups.put("B+",20);
        bloodGroups.put("B-",25);bloodGroups.put("O+",30);bloodGroups.put("O-",35);
        bloodGroups.put("AB+",40);bloodGroups.put("AB-",45);
        // Initialize data with blood group names and initial quantity 0
        bloodGroups.forEach((k,v) -> {
            BloodStockModel bloodStock = new BloodStockModel();
            bloodStock.setBloodGroup(k);
            bloodStock.setCoinValue(v);
            bloodStock.setAvaliableUnit(0L);
            bloodStock.setLastUpdate(LocalDateTime.now());
            List<BloodStockModel> byBloodGroup = bloodStockRepository.findAllByBloodGroup(k);
            int f = 1;
            for (BloodStockModel bloodStockModel : byBloodGroup) {
                if (bloodStockModel.getBloodGroup().equalsIgnoreCase(k)) {
                    f = 0;
                    break;
                }
            }
            if (f == 1) {
                System.out.println("set stock table");
                bloodStockRepository.save(bloodStock);
            }

        });
    }
    public String setBloodStockRepository(UserRequestModel requestModel){
        BloodStockModel byBloodGroup = bloodStockRepository.getByBloodGroup(requestModel.getBloodGroup());
        Long availableUnit=byBloodGroup.getAvaliableUnit();
        if (requestModel.getType().equalsIgnoreCase("donar")){
            byBloodGroup.setAvaliableUnit((availableUnit+ requestModel.getQuantity()));
            byBloodGroup.setLastUpdate(LocalDateTime.now());
            bloodStockRepository.save(byBloodGroup);
            return "ok";
        }
        else {  //handiling receiver request  in stock table

            Long remaningBloodUnit = availableUnit - requestModel.getQuantity();
            if (remaningBloodUnit >= 0) {
                byBloodGroup.setAvaliableUnit(remaningBloodUnit);
                byBloodGroup.setLastUpdate(LocalDateTime.now());
                return "ok";
            } else
                return "Stock not avaliable !please reject the Request";
        }
    }

}
