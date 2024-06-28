package com.pentacode.pentacode_middleware.freeslots.controller;

import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotCreateRequest;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotCreateResponse;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotViewResponse;
import com.pentacode.pentacode_middleware.freeslots.service.FreeSlotService;
import com.pentacode.pentacode_middleware.registration.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/freeslot")
public class FreeSlotController {

    private static Logger audit = LogManager.getLogger("audit-log");

    @Autowired
    private FreeSlotService freeSlotService;

    //E4562 -> system error
    //E1232 -> request error
    //E7192 -> dont have permissions to create free slots
    @PostMapping("/create")
    public ResponseEntity<FreeSlotCreateResponse> createFreeSlot(@RequestBody FreeSlotCreateRequest freeSlotCreateRequest, HttpServletRequest request, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "create-freeslot,start,ok");
        if (freeSlotCreateRequest != null) {
            audit.info(log + "," + "create-freeslot," + loggedUser.getEmail());
            return freeSlotService.createFreeSlot(freeSlotCreateRequest, loggedUser, log);
        } else {
            audit.info(log + ",create-freeslot,error,E1232,Request object is null");
            return ResponseEntity.ok(FreeSlotCreateResponse.builder().statusCode("E1232").statusDetails("Request object is null").build());
        }
    }

    //E4562 -> system error
    //E1232 -> request error
    //E7192 -> dont have permissions to view free slots
    @GetMapping("/view")
    public ResponseEntity<FreeSlotViewResponse> getFreeSlots(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize,
                                                             HttpServletRequest request, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "view-freeslot,start,ok");
        audit.info(log + "," + "view-freeslot," + loggedUser.getEmail());
        return freeSlotService.getAllFreeSlots(pageNumber, pageSize, loggedUser, log);
    }

    //E4562 -> system error
    //E1232 -> request error
    //E7192 -> dont have permissions to view created slots
    @GetMapping("/viewByDoctor")
    public ResponseEntity<FreeSlotViewResponse> getCreatedSlots(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize,
                                                             HttpServletRequest request, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "view-created-freeslot,start,ok");
        audit.info(log + "," + "view-created-freeslot," + loggedUser.getEmail());
        return freeSlotService.getAllFreeSlotsByCreatedDoctor(pageNumber, pageSize, loggedUser, log);
    }

    private String getMandotaryLogString(HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        String localIp = httpServletRequest.getLocalAddr();
        String remoteIp = httpServletRequest.getRemoteAddr();
        return sessionId + "," + "|" + userAgent + "|" + "," + localIp + "," + remoteIp + "," + "pentacode-middleware";
    }
}
