package com.pentacode.pentacode_middleware.uploads.dto;

import com.pentacode.pentacode_middleware.uploads.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageList {
    private byte[] image;
    private LocalDateTime createdAt;

    public ImageList(Image  image) {
        try {
            this.image = Files.readAllBytes(Path.of(image.getImagePath()));
            this.createdAt = image.getCreatedAt();
        }
        catch (Exception E){
            E.printStackTrace();
        }
    }
}
