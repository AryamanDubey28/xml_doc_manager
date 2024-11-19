package com.example.xmldemo.repository;

import com.example.xmldemo.model.Document;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Custom queries can be added here
    List<Document> findByUserId(String userId);
    Optional<Document> findByIdAndUserId(Long id, String userId);
    List<Document> findAllByUserId(String userId);
    
}