package com.example.jobportal.controller;

import com.example.jobportal.model.User;
import com.example.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            org.springframework.ui.Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username and password.");
        }
        if (logout != null) {
            model.addAttribute("msg", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(org.springframework.ui.Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        if (userService.findByEmail(user.getEmail()) != null) {
            redirectAttributes.addFlashAttribute("error", "There is already an account registered with that email.");
            return "redirect:/register";
        }
        
        // Ensure role format is correct for Spring Security
        if(user.getRole() == null || user.getRole().isEmpty()){
             user.setRole("ROLE_STUDENT");
        } else if(!user.getRole().startsWith("ROLE_")) {
             user.setRole("ROLE_" + user.getRole());
        }

        userService.registerUser(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in.");
        return "redirect:/login";
    }
}
