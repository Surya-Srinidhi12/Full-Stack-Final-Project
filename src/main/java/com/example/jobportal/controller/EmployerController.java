package com.example.jobportal.controller;

import com.example.jobportal.model.Application;
import com.example.jobportal.model.Job;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.ApplicationRepository;
import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.security.CustomUserDetails;
import com.example.jobportal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User employer = userDetails.getUser();
        List<Job> jobs = jobRepository.findByEmployer(employer);
        model.addAttribute("jobs", jobs);
        return "employer/dashboard";
    }

    @GetMapping("/job/post")
    public String postJobPage(Model model) {
        model.addAttribute("job", new Job());
        return "employer/post-job";
    }

    @PostMapping("/job/post")
    public String postJob(@ModelAttribute("job") Job job, @AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
        User employer = userRepository.findById(userDetails.getUser().getId()).orElse(null);
        job.setEmployer(employer);
        jobRepository.save(job);
        redirectAttributes.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/employer/dashboard";
    }

    @GetMapping("/job/{id}/applicants")
    public String viewApplicants(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null || !job.getEmployer().getId().equals(userDetails.getUser().getId())) {
            return "redirect:/employer/dashboard";
        }
        
        List<Application> applications = applicationRepository.findByJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        return "employer/applicants";
    }

    @PostMapping("/application/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id, @RequestParam("status") String status, 
                                          @AuthenticationPrincipal CustomUserDetails userDetails,
                                          RedirectAttributes redirectAttributes) {
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null && app.getJob().getEmployer().getId().equals(userDetails.getUser().getId())) {
            app.setStatus(status);
            applicationRepository.save(app);
            
            String subject = "Update on your application for " + app.getJob().getTitle();
            String body = "Hello " + app.getApplicant().getFullName() + ",\n\n" +
                          "Your application status has been updated to: " + status + ".\n\n" +
                          "Best Regards,\n" + app.getJob().getEmployer().getFullName();
            
            // Send the actual email asynchronously or wait (waiting here for MVP simplicity)
            emailService.sendEmail(app.getApplicant().getEmail(), subject, body);
            
            redirectAttributes.addFlashAttribute("success", "Application status updated and notification sent.");
            return "redirect:/employer/job/" + app.getJob().getId() + "/applicants";
        }
        return "redirect:/employer/dashboard";
    }
}
