package com.example.jobportal.repository;

import com.example.jobportal.model.Application;
import com.example.jobportal.model.Job;
import com.example.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicant(User applicant);
    List<Application> findByJob(Job job);
    Optional<Application> findByApplicantAndJob(User applicant, Job job);
}
