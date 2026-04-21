package com.example.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@jobportal.com"); // Replaced by SMTP provider anyway usually
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            // Log it but do not crash the app if email fails (like auth failure)
            System.err.println("Failed to send email to " + to + ". Please check your application.properties SMTP configurations! Error: " + e.getMessage());
        }
    }
}
