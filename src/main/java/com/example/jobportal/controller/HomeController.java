package com.example.jobportal.controller;

import com.example.jobportal.model.User;
import com.example.jobportal.security.CustomUserDetails;
import com.example.jobportal.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return "index";
        }
        
        User user = customUserDetails.getUser();
        String role = user.getRole();
        
        if ("ROLE_EMPLOYER".equals(role)) {
            return "redirect:/employer/dashboard";
        } else if ("ROLE_STUDENT".equals(role)) {
            return "redirect:/student/dashboard";
        }
        
        return "index";
    }

}
