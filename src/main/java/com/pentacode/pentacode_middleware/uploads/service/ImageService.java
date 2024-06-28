package com.pentacode.pentacode_middleware.uploads.service;

import com.pentacode.pentacode_middleware.registration.dto.RegisterResponse;
import com.pentacode.pentacode_middleware.registration.entity.User;
import com.pentacode.pentacode_middleware.registration.enums.Role;
import com.pentacode.pentacode_middleware.security.dto.LoggedUser;
import com.pentacode.pentacode_middleware.uploads.dto.ImageList;
import com.pentacode.pentacode_middleware.uploads.dto.ImageResponse;
import com.pentacode.pentacode_middleware.uploads.dto.UploadRequest;
import com.pentacode.pentacode_middleware.uploads.dto.UploadResponse;
import com.pentacode.pentacode_middleware.uploads.entity.Image;
import com.pentacode.pentacode_middleware.uploads.repository.ImageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import org.springframework.data.domain.Page;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class ImageService {

    private static Logger audit = LogManager.getLogger("audit-log");

    @Autowired
    private ImageRepository imageRepository;

    @Value("${app.patient.image.path}")
    private String baseImagePath;

    public ResponseEntity<UploadResponse> uploadImage(UploadRequest uploadRequest, User user, String log) {
        try {
            //data validation
            if (uploadRequest.getImage() == null || uploadRequest.getImage().isEmpty()) {
                audit.info(log + ",upload,error,E1232,image is null or empty");
                return ResponseEntity.ok(UploadResponse.builder().statusCode("E1232").statusDetails("E1232,image is null or empty").build());
            }
            String originalFileName = uploadRequest.getImage().getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = "patient_" + user.getId() + "_" + System.currentTimeMillis() + fileExtension;
            String userDirPath = baseImagePath + "/patient_" + user.getId();
            File userDir = new File(userDirPath);
            if (!userDir.exists()) {
                userDir.mkdirs();
            }
            String filePath = userDirPath + "/" + newFileName;
            LocalDateTime now = LocalDateTime.now();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(uploadRequest.getImage().getBytes());
            }
            Image image = Image.builder().imagePath(userDirPath + "/" + newFileName).createdAt(now)
                    .description(uploadRequest.getDescription()).owner(user).build();
            imageRepository.save(image);
            return ResponseEntity.ok(UploadResponse.builder().statusCode("S1000").statusDetails("image uploaded").build());
        } catch (Exception e) {
            audit.info(log + ",upload,error,E4562,system error," + e);
            return ResponseEntity.ok(UploadResponse.builder().statusCode("E4562").statusDetails("system error").build());
        }
    }

    public ResponseEntity<ImageResponse> getImagesForEachPatient(int pageNumber, int pageSize, User user, String log) {
        try {
            //data validation
            if (!user.getRole().equals(Role.PATIENT)) {
                audit.info(log + ",view-uploads,error,E7192,dont have permissions to view");
                return ResponseEntity.ok(ImageResponse.builder().imageList(null).statusCode("E7192")
                        .statusDetails("dont have permissions to view").build());
            }
            Pageable requestPage = PageRequest.of(pageNumber, pageSize);
            Page<Image> listOfImages = imageRepository.findByOwnerIdOrderByCreatedAtDesc(user.getId(), requestPage);
            Page<ImageList> imageLists = listOfImages.map(ImageList::new);
            return ResponseEntity.ok(ImageResponse.builder().imageList(imageLists)
                    .statusCode("S1000").statusDetails("download successful").build());
        } catch (Exception e) {
            audit.info(log + ",view-uploads,error,E4562,system error," + e);
            return ResponseEntity.ok(ImageResponse.builder().imageList(null).statusCode("E4562").statusDetails("system error").build());
        }
    }
}
