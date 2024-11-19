package com.example.xmldemo.repository;

import com.example.xmldemo.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Custom queries can be added here
}