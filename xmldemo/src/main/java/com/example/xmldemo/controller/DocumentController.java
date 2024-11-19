package com.example.xmldemo.controller;

import com.example.xmldemo.model.Document;
import com.example.xmldemo.service.DocumentService;
import com.example.xmldemo.service.XmlMarshalService;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private XmlMarshalService xmlMarshalService;

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(documentService.getAllDocumentsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        Document document = documentService.getDocumentByIdAndUserId(id, userId);
        return ResponseEntity.ok(document);
    }

    @PostMapping
    public ResponseEntity<Document> createDocument(
            @RequestBody Document document,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        document.setUserId(userId);
        return ResponseEntity.ok(documentService.saveDocument(document));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(
            @PathVariable Long id,
            @RequestBody Document document,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        Document existingDocument = documentService.getDocumentByIdAndUserId(id, userId);
        
        existingDocument.setName(document.getName());
        existingDocument.setContent(document.getContent());
        existingDocument.setVersion(document.getVersion());
        
        return ResponseEntity.ok(documentService.saveDocument(existingDocument));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        documentService.deleteDocumentByIdAndUserId(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/marshal")
    public ResponseEntity<?> marshalXml(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String xml = request.get("xml");
            Class<?> targetClass = Class.forName(request.get("targetClass"));
            
            Object marshalledObject = xmlMarshalService.unmarshalXml(xml, targetClass);
            
            Map<String, Object> response = new HashMap<>();
            response.put("object", marshalledObject);
            response.put("metadata", xmlMarshalService.getObjectMetadata(marshalledObject));
            
            return ResponseEntity.ok(response);
        } catch (ClassNotFoundException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Target class not found: " + e.getMessage()));
        } catch (JAXBException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "XML marshalling failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }
}