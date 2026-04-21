package com.example.jobportal.repository;

import com.example.jobportal.model.Job;
import com.example.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByEmployer(User employer);
    List<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
