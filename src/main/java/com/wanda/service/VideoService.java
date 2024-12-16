package com.wanda.service;

import com.wanda.dto.ServeVideo;
import com.wanda.entity.Video;
import com.wanda.repository.VideoRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class VideoService {

    @Value("${video.directory}")
    private String VideoDIR;

    private VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    //
    public String saveVideo(MultipartFile file, String title, String description) {

        if (file.isEmpty()) {
            return "No file selected.";
        }

        try {
            Path path = Paths.get(VideoDIR);

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String originalFilename = file.getOriginalFilename();

            String contentType = file.getContentType();

            Video saveVideo = new Video();
            saveVideo.setTitle(title);
            saveVideo.setContentType(contentType);
            saveVideo.setDescription(description);

            String newFileName = UUID.randomUUID()+"_"+originalFilename;
            Path filePath = path.resolve(newFileName);

            saveVideo.setFilePath(filePath.toString());

        this.videoRepository.save(saveVideo);


        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "success";
        }catch(IOException e){
            e.printStackTrace();
            return "error";
        }

//

    }


    public ServeVideo serveVideo(String videoId, String range) {
        Optional<Video> existingVideo = this.videoRepository.findById(videoId);


        if (existingVideo.isPresent()) {
            System.out.println("range " + range);
            Video video = existingVideo.get();
            String strFilePath = video.getFilePath();
            String contentType = video.getContentType();
            Path filePath = Paths.get(strFilePath);

            if (!Files.exists(filePath)) {
                // Handle missing file scenario
                System.out.println("file not found");
                return null;
            }

            long fileLength = filePath.toFile().length();
            contentType = (contentType == null || contentType.isEmpty()) ? "application/octet-stream" : contentType;

            ServeVideo serveVideo = new ServeVideo();
            serveVideo.setContentType(contentType);

            if (range == null || !range.startsWith("bytes=")) {
                FileSystemResource resource = new FileSystemResource(filePath);
                serveVideo.setResource(resource);
                return serveVideo;
            }

            range = range.substring("bytes=".length());
            String[] browserRanges = range.split("-");
            long rangeStart = Long.parseLong(browserRanges[0]);
            long rangeEnd = fileLength;

            System.out.println("bytes=" + rangeStart + "-" + rangeEnd + "/" + fileLength);
            long contentLength = rangeEnd - rangeStart + 1;
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
            headers.setContentLength(contentLength);
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");
            headers.add("X-Content-Type-Options", "nosniff");

            try (InputStream inputStream = Files.newInputStream(filePath)) {
                inputStream.skip(rangeStart);
                serveVideo.setInputStream(inputStream);
                serveVideo.setHeaders(headers);
            } catch (IOException e) {
                System.out.println("inside inputstreram exception");
                e.printStackTrace();
                return null;
            }

            return serveVideo;
        }

        // Video not present
        System.out.println("video not found");
        return null;
    }


}
