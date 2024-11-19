package com.example.xmldemo.controller;

import com.example.xmldemo.model.Document;
import com.example.xmldemo.service.DocumentService;
import com.example.xmldemo.service.XmlMarshalService;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        return ResponseEntity.ok(documentService.saveDocument(document));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/marshal")
    public ResponseEntity<?> marshalXml(@RequestBody Map<String, String> request) {
        try {
            // Get the XML content and target class from the request
            String xml = request.get("xml");
            Class<?> targetClass = Class.forName(request.get("targetClass"));
            
            // Unmarshal the XML to Java object
            Object marshalledObject = xmlMarshalService.unmarshalXml(xml, targetClass);
            
            // Create response with both the object and its metadata
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

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        Document existingDocument = documentService.getDocumentById(id);
        
        existingDocument.setName(document.getName());
        existingDocument.setContent(document.getContent());
        existingDocument.setVersion(document.getVersion());
        
        return ResponseEntity.ok(documentService.saveDocument(existingDocument));
    }
}