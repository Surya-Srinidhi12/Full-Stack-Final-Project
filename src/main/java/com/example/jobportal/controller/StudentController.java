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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Job> jobs;
        if (query != null && !query.isEmpty()) {
            jobs = jobRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        } else {
            jobs = jobRepository.findAll();
        }
        model.addAttribute("jobs", jobs);
        model.addAttribute("query", query);
        return "student/dashboard";
    }

    @GetMapping("/job/{id}")
    public String jobDetails(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null) return "redirect:/student/dashboard";
        
        User applicant = userRepository.findById(userDetails.getUser().getId()).orElse(null);
        Optional<Application> existingApp = applicationRepository.findByApplicantAndJob(applicant, job);
        
        model.addAttribute("job", job);
        model.addAttribute("hasApplied", existingApp.isPresent());
        return "student/job-details";
    }

    @PostMapping("/job/{id}/apply")
    public String applyForJob(@PathVariable Long id, 
                              @RequestParam("resume") MultipartFile file,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        Job job = jobRepository.findById(id).orElse(null);
        User applicant = userRepository.findById(userDetails.getUser().getId()).orElse(null);

        if (job != null && applicant != null) {
            Optional<Application> existingApp = applicationRepository.findByApplicantAndJob(applicant, job);
            if (existingApp.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "You have already applied for this job.");
                return "redirect:/student/job/" + id;
            }

            Application app = new Application();
            app.setJob(job);
            app.setApplicant(applicant);
            app.setStatus("APPLIED");

            if (!file.isEmpty()) {
                try {
                    Files.createDirectories(Paths.get(uploadDir));
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path targetLocation = Paths.get(uploadDir).resolve(fileName);
                    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                    app.setResumeFilePath(fileName);
                } catch (IOException ex) {
                    redirectAttributes.addFlashAttribute("error", "Could not upload resume.");
                    return "redirect:/student/job/" + id;
                }
            }

            applicationRepository.save(app);
            
            // Send confirmation email to student
            String studentSubject = "Application Successful: " + job.getTitle();
            String studentBody = "Hello " + applicant.getFullName() + ",\n\n" +
                  "You have successfully applied for the '" + job.getTitle() + "' position at " + job.getEmployer().getFullName() + ".\n\n" +
                  "We will notify you once the employer reviews your application.\n\nBest Regards,\nJobPortal Team";
            emailService.sendEmail(applicant.getEmail(), studentSubject, studentBody);

            // Send notification email to employer
            String employerSubject = "New Application Received: " + job.getTitle();
            String employerBody = "Hello " + job.getEmployer().getFullName() + ",\n\n" +
                  "A new student, " + applicant.getFullName() + ", has applied for your '" + job.getTitle() + "' position.\n\n" +
                  "Log in to the Employer Dashboard to review their profile and resume.\n\nBest Regards,\nJobPortal Team";
            emailService.sendEmail(job.getEmployer().getEmail(), employerSubject, employerBody);

            redirectAttributes.addFlashAttribute("success", "Successfully applied for the job!");
        }
        
        return "redirect:/student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User student = userRepository.findById(userDetails.getUser().getId()).orElse(null);
        List<Application> applications = applicationRepository.findByApplicant(student);
        model.addAttribute("student", student);
        model.addAttribute("applications", applications);
        return "student/profile";
    }
}
