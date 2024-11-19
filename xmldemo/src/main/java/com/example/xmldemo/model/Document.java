package com.example.xmldemo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String version;
    
    private LocalDateTime createdAt;
    
    @Column(name = "user_id")
    private String userId;

    // Add getters and setters for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getContent() {
        return this.content;
    }

    public void setName(String newName) {
        this.name = newName;
    }
    public void setVersion(String newVersion) {
        this.version = newVersion;
    }
    public void setContent(String newContent) {
        this.content = newContent;
    }
   
}