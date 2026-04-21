package com.example.jobportal.config;

import com.example.jobportal.model.Job;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final JobRepository jobRepository;

    public DataSeeder(UserService userService, JobRepository jobRepository) {
        this.userService = userService;
        this.jobRepository = jobRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ensure mock employers exist
        User googleEmployer = userService.findByEmail("hr@google.com");
        if (googleEmployer == null) {
            googleEmployer = new User();
            googleEmployer.setFullName("Google HR Team");
            googleEmployer.setEmail("hr@google.com");
            googleEmployer.setPassword("password");
            googleEmployer.setRole("ROLE_EMPLOYER");
            googleEmployer = userService.registerUser(googleEmployer);
        }

        User netflixEmployer = userService.findByEmail("talents@netflix.com");
        if (netflixEmployer == null) {
            netflixEmployer = new User();
            netflixEmployer.setFullName("Netflix Talent Acquisition");
            netflixEmployer.setEmail("talents@netflix.com");
            netflixEmployer.setPassword("password");
            netflixEmployer.setRole("ROLE_EMPLOYER");
            netflixEmployer = userService.registerUser(netflixEmployer);
        }

        // Mock Student for easy login testing
        if (userService.findByEmail("student@university.edu") == null) {
            User student = new User();
            student.setFullName("Jane Doe");
            student.setEmail("student@university.edu");
            student.setPassword("password");
            student.setRole("ROLE_STUDENT");
            student.setDept("Computer Science");
            student.setYearOfStudy("3rd Year");
            userService.registerUser(student);
        }

        // Seed 18 Jobs if DB is empty
        if (jobRepository.count() == 0) {
            List<Job> initialJobs = Arrays.asList(
                    createJob("Senior Software Engineer", "Build scalable backend services using Java and Spring Boot. You will interact heavily with distributed systems.", "Java, Spring Boot, Microservices", "150k to 200k", googleEmployer),
                    createJob("Frontend Developer (React)", "Looking for an expert React developer to architect our next-generation video streaming interface.", "React, TypeScript, Redux", "130k to 160k", netflixEmployer),
                    createJob("Data Scientist", "Analyze large datasets to improve video recommendation algorithms.", "Python, TensorFlow, SQL", "140k to 180k", netflixEmployer),
                    createJob("Cloud Infrastructure Engineer", "Design and maintain Kubernetes clusters serving millions of requests globally.", "Kubernetes, AWS, Terraform", "145k to 175k", googleEmployer),
                    createJob("Product Manager", "Lead product strategy for enterprise cloud tools. Prior technical background preferred.", "Agile, Jira, Leadership", "130k to 160k", googleEmployer),
                    createJob("DevOps Engineer", "Streamline our CI/CD pipelines and monitor production system health.", "Docker, Jenkins, Linux", "120k to 150k", netflixEmployer),
                    createJob("UX/UI Designer", "Design beautiful and seamless user experiences for web and mobile platforms.", "Figma, Sketch, Prototyping", "₹12,00,000 - ₹18,00,000", googleEmployer),
                    createJob("Machine Learning Engineer", "Deploy models to production for large-scale language processing.", "Python, PyTorch, MLOps", "₹28,00,000 - ₹40,00,000", googleEmployer),
                    createJob("Mobile App Developer (iOS)", "Build native iOS applications that provide elegant experiences to users.", "Swift, iOS, CoreData", "₹16,00,000 - ₹24,00,000", netflixEmployer),
                    createJob("Database Administrator", "Ensure high availability and performance of huge MySQL databases.", "MySQL, PostgreSQL, Tuning", "₹14,00,000 - ₹20,000,000", googleEmployer),
                    createJob("Cybersecurity Analyst", "Protect our cloud infrastructure from vulnerabilities and active threat models.", "Security, Networking, Pentesting", "₹15,00,000 - ₹22,00,000", googleEmployer),
                    createJob("Full Stack Developer", "Develop both frontend UI and backend APIs for internal CRM tools.", "JavaScript, Node.js, React", "₹14,00,000 - ₹20,00,000", netflixEmployer),
                    createJob("Site Reliability Engineer (SRE)", "Focus on uptime and system observability.", "Go, Prometheus, Grafana", "₹20,00,000 - ₹30,00,000", netflixEmployer),
                    createJob("Engineering Manager", "Manage a team of 10+ software developers and guide technical decisions.", "Management, Architecture, Agile", "₹35,00,000 - ₹50,00,000", googleEmployer),
                    createJob("QA Automation Engineer", "Develop automated testing suites to catch regressions before they hit production.", "Selenium, Cypress, Java", "₹10,00,000 - ₹15,00,000", netflixEmployer),
                    createJob("Game Developer", "Develop interactive mini-games for promotional campaigns.", "C#, Unity, 3D Math", "₹12,00,000 - ₹18,00,000", netflixEmployer),
                    createJob("Technical Writer", "Write and maintain developer documentation for our public APIs.", "Writing, APIs, Markdown", "₹8,00,000 - ₹12,00,000", googleEmployer),
                    createJob("Data Engineer", "Build and optimize ETL pipelines moving petabytes of data.", "Spark, Hadoop, Python", "₹18,00,000 - ₹26,00,000", netflixEmployer)
            );

            jobRepository.saveAll(initialJobs);
        }
    }

    private Job createJob(String title, String desc, String skills, String salary, User employer) {
        Job job = new Job();
        job.setTitle(title);
        job.setDescription(desc);
        job.setSkillsRequired(skills);
        job.setSalary(salary);
        job.setEmployer(employer);
        return job;
    }
}
