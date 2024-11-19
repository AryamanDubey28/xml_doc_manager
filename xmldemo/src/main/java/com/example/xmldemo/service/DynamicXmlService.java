package com.example.xmldemo.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.StringReader;
import java.util.*;
import org.xml.sax.InputSource;

@Service
public class DynamicXmlService {
    
    /**
     * Converts any XML string into a generic map structure that can be easily
     * serialized to JSON and handled by the frontend
     */
    public Map<String, Object> parseXmlToMap(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
        
        return elementToMap(doc.getDocumentElement());
    }
    
    private Map<String, Object> elementToMap(Element element) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        // Add attributes if present
        NamedNodeMap attributes = element.getAttributes();
        if (attributes.getLength() > 0) {
            Map<String, String> attrMap = new LinkedHashMap<>();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                attrMap.put(attr.getNodeName(), attr.getNodeValue());
            }
            result.put("@attributes", attrMap);
        }
        
        // Process child nodes
        NodeList children = element.getChildNodes();
        Map<String, List<Object>> childrenMap = new LinkedHashMap<>();
        
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childName = child.getNodeName();
                Object childValue;
                
                // Convert child element
                if (hasChildElements(child)) {
                    childValue = elementToMap((Element) child);
                } else {
                    // For simple elements with just text content
                    childValue = child.getTextContent();
                }
                
                // Group similar child elements into arrays
                childrenMap.computeIfAbsent(childName, k -> new ArrayList<>())
                    .add(childValue);
            }
        }
        
        // Add all child elements to result
        childrenMap.forEach((key, value) -> {
            result.put(key, value.size() == 1 ? value.get(0) : value);
        });
        
        return result;
    }
    
    private boolean hasChildElements(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validates if the input is well-formed XML
     */
    public boolean isValidXml(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new InputSource(new StringReader(xmlContent)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extracts metadata about the XML structure
     */
    public Map<String, Object> getXmlMetadata(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
        Element root = doc.getDocumentElement();
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("rootElement", root.getTagName());
        metadata.put("namespaces", getNamespaces(root));
        metadata.put("structure", analyzeStructure(root));
        
        return metadata;
    }
    
    private Set<String> getNamespaces(Element element) {
        Set<String> namespaces = new HashSet<>();
        String xmlns = element.getAttribute("xmlns");
        if (!xmlns.isEmpty()) {
            namespaces.add(xmlns);
        }
        
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            if (attr.getNodeName().startsWith("xmlns:")) {
                namespaces.add(attr.getNodeValue());
            }
        }
        
        return namespaces;
    }
    
    private Map<String, Object> analyzeStructure(Element element) {
        Map<String, Object> structure = new LinkedHashMap<>();
        structure.put("name", element.getTagName());
        
        // Analyze attributes
        NamedNodeMap attributes = element.getAttributes();
        if (attributes.getLength() > 0) {
            List<String> attrList = new ArrayList<>();
            for (int i = 0; i < attributes.getLength(); i++) {
                attrList.add(attributes.item(i).getNodeName());
            }
            structure.put("attributes", attrList);
        }
        
        // Analyze child elements
        NodeList children = element.getChildNodes();
        Map<String, Integer> childElements = new LinkedHashMap<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childName = child.getNodeName();
                childElements.merge(childName, 1, Integer::sum);
            }
        }
        
        if (!childElements.isEmpty()) {
            List<Map<String, Object>> childStructures = new ArrayList<>();
            childElements.forEach((name, count) -> {
                Map<String, Object> childInfo = new LinkedHashMap<>();
                childInfo.put("name", name);
                childInfo.put("count", count);
                childStructures.add(childInfo);
            });
            structure.put("children", childStructures);
        }
        
        return structure;
    }
}