package com.wanda.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Videos")
public class Video {

    public Video() {
    }

    public Video(String title, String description, String contentType, String filePath) {
        this.title = title;
        this.description = description;
        this.contentType = contentType;
        this.filePath = filePath;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String description;

    private String contentType;

    private String filePath;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
