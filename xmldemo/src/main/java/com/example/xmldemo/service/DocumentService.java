package com.example.xmldemo.service;

import com.example.xmldemo.model.Document;
import com.example.xmldemo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DocumentService {
    
    @Autowired
    private DocumentRepository documentRepository;

    public List<Document> getAllDocumentsByUserId(String userId) {
        return documentRepository.findAllByUserId(userId);
    }

    public Document getDocumentByIdAndUserId(Long id, String userId) {
        return documentRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new AccessDeniedException("Document not found or access denied"));
    }

    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    public void deleteDocumentByIdAndUserId(Long id, String userId) {
        Document document = getDocumentByIdAndUserId(id, userId);
        documentRepository.delete(document);
    }
}