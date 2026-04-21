package com.example.jobportal.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ROLE_STUDENT, ROLE_EMPLOYER, ROLE_ADMIN

    // Profile Details for Students
    private String dept;
    private String yearOfStudy;
    private String resumeFilePath;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Job> jobsPosted;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getResumeFilePath() { return resumeFilePath; }
    public void setResumeFilePath(String resumeFilePath) { this.resumeFilePath = resumeFilePath; }

    public List<Job> getJobsPosted() { return jobsPosted; }
    public void setJobsPosted(List<Job> jobsPosted) { this.jobsPosted = jobsPosted; }

    public List<Application> getApplications() { return applications; }
    public void setApplications(List<Application> applications) { this.applications = applications; }
}
