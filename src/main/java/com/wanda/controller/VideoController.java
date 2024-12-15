package com.wanda.controller;

import com.wanda.dto.ServeVideo;
import com.wanda.service.VideoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VideoController {

    private VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/video")
    public String saveVideo(@RequestParam("file") MultipartFile file, String title, String description) {
        String s = this.videoService.saveVideo(
                file,
                title,
                description
        );

        return s;
    }

    @GetMapping("/video/serve/{videoId}")
    public ResponseEntity<FileSystemResource> serveVideo(@PathVariable String videoId) {
        ServeVideo video = this.videoService.serveVideo(videoId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(video.getContentType()))
                .body(video.getResource());
    }
}
