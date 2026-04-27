package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.VolunteerApplication;

public interface VolunteerApplicationRepository extends JpaRepository<VolunteerApplication, Long> {
}
