package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Prescription;

public interface PrescriptionRepository  extends JpaRepository<Prescription, Integer>{
}

