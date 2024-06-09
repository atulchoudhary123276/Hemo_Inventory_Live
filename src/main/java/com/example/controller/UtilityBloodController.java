package com.example.controller;

import com.example.dto.BloodStockDto;
import com.example.dto.UserRegisterDto;
import com.example.service.BloodReportService;
import com.example.service.BloodRequestsService;
import com.example.service.BloodStockService;
import com.example.service.SortingAndFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class UtilityBloodController {
    @Autowired
    SortingAndFilterService sortingAndFilterService;
    @Autowired
    BloodRequestsService bloodRequestsService;
    @Autowired
    BloodStockService bloodStockService;
    @Autowired
    BloodReportService bloodReportService;

    //----- After redirecting to Dashboard -----
    @GetMapping
    public String getDashboard(HttpServletRequest request,Model model) {
        HttpSession session = request.getSession();
        if (session==null){
            return "redirect:/login";
        }
        String role = (String) session.getAttribute("role");
        model.addAttribute("coinValue",session.getAttribute("coinValue"));
        model.addAttribute("name",session.getAttribute("name"));
        model.addAttribute("userId",session.getAttribute("userId"));
        model.addAttribute("role",role);
        model.addAttribute("dob",session.getAttribute("dob"));
        model.addAttribute("createdBy",session.getAttribute("createdBy"));
        model.addAttribute("createdOn",session.getAttribute("createdOn"));
        model.addAttribute("bloodGroup",session.getAttribute("bloodGroup"));
       if (role.equalsIgnoreCase("ADMIN")) {
            return "admin/dashboard";
        } else if (role.equalsIgnoreCase("AGENT")) {
            return "agent/dashboard";
        } else {
            return "enduser/user";
        }
    }

    @GetMapping(value = "/createagent")
    public String createAgent(Model model,HttpSession session) {
        if (session!=null){
            model.addAttribute("coinValue",session.getAttribute("coinValue"));
            model.addAttribute("name",session.getAttribute("name"));
        }
        return "admin/createagent";
    }

    @GetMapping(value = "/alluserslist")
    public String displayAlluserslist(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        String userName = (String) session.getAttribute("userId");
        if (role == null) {
            return "redirect:/login";
        }
        model.addAttribute("coinValue",session.getAttribute("coinValue"));
        model.addAttribute("name",session.getAttribute("name"));
        if (role != null && role.equalsIgnoreCase("ADMIN")) {
            model.addAttribute("allUsers", sortingAndFilterService.getAllUsersByRole());
            return role.toLowerCase() + "/alluserslist";
        } else {
            model.addAttribute("allUsers", sortingAndFilterService.getAllUsersByUserName(userName));
            return role.toLowerCase() + "/alluserslist";

        }
    }

    @PostMapping(value = "/users")
    public String getAllUsersBy(@RequestParam("sortOption") String sortBy, Model model, @RequestParam("username") String username
            , @RequestParam("filterOption") String filterBy, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("role");
        String name = (String) request.getSession().getAttribute("name");
        String userName = (String) request.getSession().getAttribute("userId");
        List<UserRegisterDto> userRegisterDtos = new ArrayList<>();
        if (role != null && role.equalsIgnoreCase("ADMIN")) {
            userRegisterDtos = sortingAndFilterService.getAllUsersByRole();
        } else {
            userRegisterDtos = sortingAndFilterService.getAllUsersByUserName(userName);
        }
        userRegisterDtos = sortingAndFilterService.sortBy(sortBy, userRegisterDtos);
        model.addAttribute("allUsers", userRegisterDtos);
        if (filterBy != null) {
            switch (filterBy){
                case "byAgent":
                    userRegisterDtos = sortingAndFilterService.filterByAgent(userRegisterDtos,username);
                    model.addAttribute("allUsers", userRegisterDtos);
                    break;
                case "createdBetween":
                    userRegisterDtos = sortingAndFilterService.filterByDate(startDate, endDate, userRegisterDtos);
                    model.addAttribute("allUsers", userRegisterDtos);
                    break;
                case  "notLoggedIn":
                    userRegisterDtos = sortingAndFilterService.notLoggedInUser(userRegisterDtos);
                    model.addAttribute("allUsers", userRegisterDtos);
                    break;
                case "byUsername":
                    if (username != null && !username.isEmpty()) {
                        model.addAttribute("allUsers", sortingAndFilterService.filterByUsername(username, userRegisterDtos));
                    }
                    break;
                default:
                    model.addAttribute("allUsers",userRegisterDtos);
            }
        }
        model.addAttribute("name", name);
        model.addAttribute("role", role);
        model.addAttribute("coinValue",request.getSession().getAttribute("coinValue"));
        return role.toLowerCase() + "/alluserslist";

    }

    @GetMapping(value = "/bloodrequests")
    public String getBloodRequests(HttpServletRequest request, Model model, @RequestParam(name = "input", required = false) String input,
                                   @RequestParam(name = "status", required = false) String status
            , @RequestParam(name = "filterOption", required = false) String filterBy, @RequestParam(name = "startDate", required = false) String startDate, @RequestParam(name = "endDate", required = false) String endDate) throws ParseException {
        String userName = (String) request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("role");
        Integer coinvalue = bloodReportService.getCoinvalue(userName);
        model.addAttribute("coinValue",request.getSession().getAttribute("coinValue"));
        model.addAttribute("name",request.getSession().getAttribute("name"));
        request.getSession().setAttribute("coinValue",coinvalue);
        if (userName == null || role == null) {
            model.addAttribute("errorMsg", "You are not logged In");
            return "redirect:/login";
        }
        List<HashMap<String, Object>> bloodBankList = bloodRequestsService.getBloodBankList();
        if (role != null && role.equalsIgnoreCase("ADMIN")) {
            model.addAttribute("getBloodRequests", bloodRequestsService.filterBloodRequest(bloodBankList, filterBy, input, status, startDate, endDate));
            return "admin/bloodrequests";
        }
        if (role.equalsIgnoreCase("ENDUSER")) {
            List<HashMap<String, Object>> enduserRequest = bloodRequestsService.findEnduserRequest(bloodBankList, userName);
            model.addAttribute("requests", bloodRequestsService.filterBloodRequest(enduserRequest, filterBy, input, status, startDate, endDate));
            return "enduser/requeststatus";

        } else {
            List<HashMap<String, Object>> mapList = bloodRequestsService.agentUserRequest(bloodBankList, userName);
            model.addAttribute("getBloodRequests", bloodRequestsService.filterBloodRequest(mapList, filterBy, input, status, startDate, endDate));
            return "agent/bloodrequests";
        }
    }

    @PostMapping(value = "/approvedRequest")
    public String approvedRequest(RedirectAttributes redirectAttributes, @RequestParam("reqid") String reqId, @RequestParam("status") String status) {
        String statusResult = bloodRequestsService.updateApprovedRequest(reqId, status);
        redirectAttributes.addFlashAttribute("statusResult", statusResult);
        return "redirect:/dashboard/bloodrequests";
    }

    @PostMapping(value = "/rejectRequest")
    public String rejectRequest(Model model, @RequestParam("type") String type, @RequestParam("userName") String userName, @RequestParam("reqid") String reqId, @RequestParam("status") String status) {
        model.addAttribute("userName", userName);
        model.addAttribute("reqid", reqId);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        return "admin/rejectremark";
    }

    @PostMapping(value = "/proceedRejectRequest")
    public String proceedRejectRequest(@RequestParam("reqid") String reqId, @RequestParam("status") String status, @RequestParam("remark") String remark) {
        bloodRequestsService.updateRejectRequest(reqId, status, remark);
        return "redirect:/dashboard/bloodrequests";
    }

    @GetMapping(value = "/bloodstock")
    public String getBloodStock(Model m,HttpSession session) {
        List<BloodStockDto> bloodStock = bloodStockService.getBloodStock();
        m.addAttribute("stock", bloodStock);
        if (session!=null) {
            m.addAttribute("coinValue", session.getAttribute("coinValue"));
            m.addAttribute("name", session.getAttribute("name"));
        }
        return "admin/bloodstock";
    }

    @GetMapping(value = "/bloodreport")
    public String getRequestReport(HttpSession session, Model model) {
        if (session == null) {
            return "redirect:/login";
        }
        model.addAttribute("coinValue", session.getAttribute("coinValue"));
        model.addAttribute("name", session.getAttribute("name"));
        String role = (String) session.getAttribute("role");
        String userName = (String) session.getAttribute("userId");
        if (role != null && role.equalsIgnoreCase("ADMIN")) {
            model.addAttribute("bloodReport", bloodReportService.getRequestReport(role, userName));
            return "admin/bloodreport";
        } else {
            model.addAttribute("bloodReport", bloodReportService.getRequestReport(role, userName));
            return "agent/bloodreport";
        }
    }

    @GetMapping(value = "/coinvalue")
    public String getCoinValue(Model model, HttpSession session) {
        if (session == null) {
            return "redirect:/login";
        }
        String userName = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        model.addAttribute("coinValue", session.getAttribute("coinValue"));
        model.addAttribute("name", session.getAttribute("name"));
        if (role.equalsIgnoreCase("ADMIN")) {
            List<Map<String, String>> coinReportForAdmin = bloodReportService.findCoinReportForAdmin();
            model.addAttribute("getCoinReport", coinReportForAdmin);
            // Add data to model
            model.addAttribute("totalUserCoins", bloodReportService.calculateTotalCoinsForAd(coinReportForAdmin, "userCoins"));
            model.addAttribute("totalAgentCoins", bloodReportService.calculateTotalCoinsForAd(coinReportForAdmin, "agentCoins"));
            model.addAttribute("totalAdminCoins", bloodReportService.calculateTotalCoinsForAd(coinReportForAdmin, "adminCoins"));
            return "admin/coinvaluereport";
        } else {
            HashMap<String, Object[]> coinByBloodGroup = bloodReportService.findCoinByBloodGroup(userName);
            model.addAttribute("getCoinReport", coinByBloodGroup);
            model.addAttribute("totalAgentCoins", bloodReportService.calculateTotalCoinsForAg(coinByBloodGroup, 1));
            return "agent/coinvaluereport";
        }
    }
}

