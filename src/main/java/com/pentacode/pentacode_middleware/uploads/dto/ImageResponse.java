package com.pentacode.pentacode_middleware.uploads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private String statusCode;
    private String statusDetails;
    private Page<ImageList> imageList;
}
