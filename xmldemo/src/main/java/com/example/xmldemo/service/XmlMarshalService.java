package com.example.xmldemo.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class XmlMarshalService {
    
    private final Map<String, JAXBContext> contextCache = new HashMap<>();
    
    public Object unmarshalXml(String xml, Class<?> targetClass) throws JAXBException {
        JAXBContext context = contextCache.computeIfAbsent(
            targetClass.getName(),
            k -> {
                try {
                    return JAXBContext.newInstance(targetClass);
                } catch (JAXBException e) {
                    throw new RuntimeException("Failed to create JAXB context", e);
                }
            }
        );
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(new StringReader(xml));
    }
    
    public String marshalObject(Object object) throws JAXBException {
        JAXBContext context = contextCache.computeIfAbsent(
            object.getClass().getName(),
            k -> {
                try {
                    return JAXBContext.newInstance(object.getClass());
                } catch (JAXBException e) {
                    throw new RuntimeException("Failed to create JAXB context", e);
                }
            }
        );
        
        Marshaller marshaller = context.createMarshaller();
        // Use the string literal for the property
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        
        StringWriter sw = new StringWriter();
        marshaller.marshal(object, sw);
        return sw.toString();
    }
    
    public Map<String, Object> getObjectMetadata(Object obj) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("className", obj.getClass().getSimpleName());
        metadata.put("packageName", obj.getClass().getPackage().getName());
        metadata.put("fields", java.util.Arrays.stream(obj.getClass().getDeclaredFields())
            .map(field -> Map.of(
                "name", field.getName(),
                "type", field.getType().getSimpleName()
            ))
            .toList());
        return metadata;
    }
}