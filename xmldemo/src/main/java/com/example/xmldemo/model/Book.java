package com.example.xmldemo.model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import java.util.List;

@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Book {
    @XmlAttribute
    private String id;
    
    @XmlElement(required = true)
    private String title;
    
    @XmlElement(required = true)
    private String author;
    
    @XmlElement
    private String publisher;
    
    @XmlElement
    private int year;
    
    @XmlElementWrapper(name = "genres")
    @XmlElement(name = "genre")
    private List<String> genres;
}