package com.wanda.controller;

import com.wanda.dto.ServeVideo;
import com.wanda.entity.Video;
import com.wanda.repository.VideoRepository;
import com.wanda.service.VideoService;
import com.wanda.utils.constants.VideoConstants;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
public class VideoController {

    private VideoService videoService;
    private VideoRepository videoRepository;



    public VideoController(VideoService videoService, VideoRepository videoRepository) {
        this.videoService = videoService;
        this.videoRepository = videoRepository;
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

//     String range

//    @GetMapping("/video/serve/{videoId}")
//    public ResponseEntity<?> serveVideo(
//            @PathVariable String videoId,
//            @RequestHeader(value = "Range", required = false) String range
//            )  {
//
//        System.out.println("range: " + range);
//
//        Optional<Video> existingVideo = this.videoRepository.findById(videoId);
//
//        if(existingVideo.isPresent()) {
//            System.out.println("existing video: " + existingVideo.get());
//
//            Video video = existingVideo.get();
//
//            String contentType = video.getContentType();
//            String filePath = video.getFilePath();
//
//            Path path = Paths.get(filePath);
//
//            long fileLength = path.toFile().length();
//
//            System.out.println("fileLength: " + fileLength);
//
//            long rangeStart = 0;
//            if (range != null && range.startsWith("bytes=")) {
//                range = range.substring("bytes=".length());
//                String[] ranges = range.split("-");
//                rangeStart = Long.parseLong(ranges[0]);
//            }
//
//            long rangeEnd = Math.min(rangeStart + VideoConstants.CHUNK_SIZE - 1, fileLength - 1); // 1 MB chunk
//            long contentLength = rangeEnd - rangeStart + 1;
//
//
//            byte[] buffer = new byte[(int) contentLength];
//
//            System.out.println("rangeStart: " + rangeStart);
//            System.out.println("rangeEnd: " + rangeEnd);
//
//            try (InputStream inputStream = Files.newInputStream(path)) {
//                inputStream.skip(rangeStart); // Skip to the start of the range
//                int bytesRead = inputStream.read(buffer, 0, (int) contentLength); // Read only the required chunk
//                if (bytesRead < contentLength) {
//                    contentLength = bytesRead; // Adjust if fewer bytes were read
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .contentType(MediaType.parseMediaType(contentType))
//                        .body("Failed to load video");
//            }
//
//
//
//
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
//            headers.setContentLength(contentLength);
//            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
//            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
//            headers.add(HttpHeaders.PRAGMA, "no-cache");
//            headers.add(HttpHeaders.EXPIRES, "0");
//            headers.add("X-Content-Type-Options", "nosniff");
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.parseMediaType(contentType))
//                    .headers(headers)
//                    .contentLength(contentLength)
//                    .body(buffer);
//        }
//
//        return ResponseEntity.status(400).body("Failed to load video");
//    }

    @GetMapping("/video/serve/{videoId}")
    public ResponseEntity<?> serveVideo(
            @PathVariable String videoId,
            @RequestHeader(value = "Range", required = false) String range
    ) {

        Optional<Video> existingVideo = this.videoRepository.findById(videoId);

        if (existingVideo.isPresent()) {
            Video video = existingVideo.get();

            String contentType = video.getContentType();
            String filePath = video.getFilePath();

            Path path = Paths.get(filePath);

            long fileLength = path.toFile().length();

            long rangeStart = 0;
            if (range != null && range.startsWith("bytes=")) {
                range = range.substring("bytes=".length());
                String[] ranges = range.split("-");
                rangeStart = Long.parseLong(ranges[0]);
            }

            long rangeEnd = Math.min(rangeStart + VideoConstants.CHUNK_SIZE - 1, fileLength - 1); // 1 MB chunk
            long contentLength = rangeEnd - rangeStart + 1;

            System.out.println("rangeStart: " + rangeStart);
            System.out.println("rangeEnd: " + rangeEnd);

            byte[] buffer = new byte[(int) contentLength];

            try (InputStream inputStream = Files.newInputStream(path)) {
                inputStream.skip(rangeStart); // Skip to the start of the range
                int bytesRead = inputStream.read(buffer, 0, (int) contentLength); // Read only the required chunk
                if (bytesRead < contentLength) {
                    contentLength = bytesRead; // Adjust if fewer bytes were read
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.parseMediaType(contentType))
                        .body("Failed to load video");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
            headers.setContentLength(contentLength); // Use contentLength
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");
            headers.add("X-Content-Type-Options", "nosniff");

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT) // 206 for partial content
                    .contentType(MediaType.parseMediaType(contentType))
                    .headers(headers)
                    .contentLength(contentLength)  // Use contentLength
                    .body(new ByteArrayResource(buffer));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to load video");
    }


}
