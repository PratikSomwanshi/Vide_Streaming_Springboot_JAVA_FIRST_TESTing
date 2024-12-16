package com.wanda.dto;

import lombok.Data;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;

import java.io.InputStream;

public class ServeVideo {

    private FileSystemResource resource;
    private String contentType;
    private HttpHeaders headers;
    private InputStream inputStream;


    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public ServeVideo() {
    }

    public ServeVideo(FileSystemResource resource, String contentType) {
        this.resource = resource;
        this.contentType = contentType;
    }

    public ServeVideo(FileSystemResource resource, String contentType, HttpHeaders headers) {
        this.resource = resource;
        this.contentType = contentType;
        this.headers = headers;
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

    @Override
    public String toString() {
        return "ServeVideo{" +
                "resource=" + resource +
                ", contentType='" + contentType + '\'' +
                ", headers=" + headers +
                ", inputStream=" + inputStream +
                '}';
    }
}
