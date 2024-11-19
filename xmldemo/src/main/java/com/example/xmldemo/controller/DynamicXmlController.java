package com.example.xmldemo.controller;
import com.example.xmldemo.service.DynamicXmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/xml")
public class DynamicXmlController {

    @Autowired
    private DynamicXmlService dynamicXmlService;

    @PostMapping("/parse")
    public ResponseEntity<?> parseXml(@RequestBody Map<String, String> request) {
        try {
            String xmlContent = request.get("xml");
            
            if (!dynamicXmlService.isValidXml(xmlContent)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid XML content"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", dynamicXmlService.parseXmlToMap(xmlContent));
            response.put("metadata", dynamicXmlService.getXmlMetadata(xmlContent));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}