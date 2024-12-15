package com.wanda.service;

import com.wanda.dto.ServeVideo;
import com.wanda.entity.Video;
import com.wanda.repository.VideoRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


    public ServeVideo serveVideo(String videoId) {
        Optional<Video> existingVideo = this.videoRepository.findById(videoId);

        if(existingVideo.isPresent()) {
            Video video = existingVideo.get();
            String filePath = video.getFilePath();
            String contentType = video.getContentType();

            if(contentType.isEmpty()){
                contentType = "video/mp4";
            }

            FileSystemResource resource = new FileSystemResource(filePath);


            ServeVideo serveVideo = new ServeVideo();
            serveVideo.setResource(resource);
            serveVideo.setContentType(contentType);
            return serveVideo;
        }

        return null;
    }

}
