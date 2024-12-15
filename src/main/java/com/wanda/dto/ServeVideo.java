package com.wanda.dto;

import lombok.Data;
import org.springframework.core.io.FileSystemResource;

public class ServeVideo {

    private FileSystemResource resource;
    private String contentType;

    public ServeVideo() {
    }

    public ServeVideo(FileSystemResource resource, String contentType) {
        this.resource = resource;
        this.contentType = contentType;
    }

    public FileSystemResource getResource() {
        return resource;
    }

    public void setResource(FileSystemResource resource) {
        this.resource = resource;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
