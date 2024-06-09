package com.example.controller;

import com.example.dto.UserRequestDto;
import com.example.service.UserRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

//it only handlse the enduser donar/receive blood request
@Controller
public class UserRequestController {
    @Autowired
    UserRequestService userRequestService;
    @GetMapping(value = "/donorreceiverrequest")
    public String userRequest() {
        return "enduser/donorreceiverrequest";
    }

    // Handle blood requirement request
    @PostMapping("/submitBloodForm")
    public String bloodRequest(@ModelAttribute UserRequestDto bloodRequestDTO,
                               Model model, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        String userName = (String) session.getAttribute("userId");
        String bloodGroup = (String) session.getAttribute("bloodGroup");
        if (bloodRequestDTO.getType().equalsIgnoreCase("donar")) {
            bloodRequestDTO.setBloodGroup(bloodGroup);
        }

        try {
            String result = userRequestService.handleBloodRequirementRequest(bloodRequestDTO, userName);
            model.addAttribute("donateResult", result);
            return "/enduser/user";
        } catch (Exception e) {
//            e.printStackTrace();
            model.addAttribute("formatError", e.getMessage());
            return "enduser/donorreceiverrequest";
        }
    }
}
