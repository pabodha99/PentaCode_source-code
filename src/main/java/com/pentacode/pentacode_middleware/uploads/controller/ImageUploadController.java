package com.pentacode.pentacode_middleware.uploads.controller;

import com.pentacode.pentacode_middleware.registration.dto.RegisterRequest;
import com.pentacode.pentacode_middleware.registration.dto.RegisterResponse;
import com.pentacode.pentacode_middleware.registration.entity.User;
import com.pentacode.pentacode_middleware.registration.service.UserService;
import com.pentacode.pentacode_middleware.uploads.dto.ImageResponse;
import com.pentacode.pentacode_middleware.uploads.dto.UploadRequest;
import com.pentacode.pentacode_middleware.uploads.dto.UploadResponse;
import com.pentacode.pentacode_middleware.uploads.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/image")
public class ImageUploadController {

    private static Logger audit = LogManager.getLogger("audit-log");

    @Autowired
    private ImageService imageService;

    //E4562 -> system error
    //E1232 -> request error
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadImage(@ModelAttribute UploadRequest uploadRequest, HttpServletRequest request, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "upload,start,ok");
        if (uploadRequest != null) {
            audit.info(log + "," + "upload," + loggedUser.getEmail());
            return imageService.uploadImage(uploadRequest, loggedUser, log);
        } else {
            audit.info(log + ",upload,error,E1232,Request object is null");
            return ResponseEntity.ok(UploadResponse.builder().statusCode("E1232").statusDetails("Request object is null").build());
        }
    }

    //E4562 -> system error
    //E1232 -> request error
    //E7192 -> dont have permissions to view
    @GetMapping("/upload")
    public ResponseEntity<ImageResponse> getImages(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize,
                                                   HttpServletRequest request, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "view-uploads,start,ok");
        audit.info(log + "," + "view-uploads," + loggedUser.getEmail());
        return imageService.getImagesForEachPatient(pageNumber, pageSize, loggedUser, log);
    }

    private String getMandotaryLogString(HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        String localIp = httpServletRequest.getLocalAddr();
        String remoteIp = httpServletRequest.getRemoteAddr();
        return sessionId + "," + "|" + userAgent + "|" + "," + localIp + "," + remoteIp + "," + "pentacode-middleware";
    }
}
