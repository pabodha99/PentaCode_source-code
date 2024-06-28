package com.pentacode.pentacode_middleware.appointment.controller;

import com.pentacode.pentacode_middleware.appointment.dto.AppointmentCreateResponse;
import com.pentacode.pentacode_middleware.appointment.dto.AppointmentViewResponse;
import com.pentacode.pentacode_middleware.appointment.service.AppointmentService;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotCreateRequest;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotCreateResponse;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotViewResponse;
import com.pentacode.pentacode_middleware.registration.entity.User;
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
@RequestMapping("/api/v1/appointment")
public class AppointmentController {

    private static Logger audit = LogManager.getLogger("audit-log");

    @Autowired
    private AppointmentService appointmentService;

    //E4562 -> system error
    //E1232 -> request error
    //E7192 -> dont have permissions to create appointments
    //E5794 -> appointment already exists
    //E9756 -> no free slot with id
    @PostMapping("/create")
    public ResponseEntity<AppointmentCreateResponse> createFreeSlot(@RequestParam("slotId") String slotId, HttpServletRequest request, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "create-appointment,start,ok");
        if (slotId != null) {
            audit.info(log + "," + "create-appointment," + loggedUser.getEmail());
            return appointmentService.createAppointment(slotId, loggedUser, log);
        } else {
            audit.info(log + ",create-appointment,error,E1232,Slot id is null");
            return ResponseEntity.ok(AppointmentCreateResponse.builder().statusCode("E1232").statusDetails("Slot id is null").build());
        }
    }

    //E4562 -> system error
    //E1232 -> request error
    //E7192 -> dont have permissions to view appointments
    @GetMapping("/view")
    public ResponseEntity<AppointmentViewResponse> getMyAppointments(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize,
                                                                     HttpServletRequest request, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "view-appointments-patient,start,ok");
        audit.info(log + "," + "view-appointments-patient," + loggedUser.getEmail());
        return appointmentService.getMyAppointments(pageNumber, pageSize, loggedUser, log);
    }

    //E4562 -> system error
    //E1232 -> request error
    //E7192 -> dont have permissions to view appointments
    @GetMapping("/viewByDoctor")
    public ResponseEntity<AppointmentViewResponse> getAppointments(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "5") int slotId,
                                                                   HttpServletRequest request, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "view-appointments-doctor,start,ok");
        audit.info(log + "," + "view-appointments-doctor," + loggedUser.getEmail());
        return appointmentService.getAppointments(pageNumber, pageSize, slotId, loggedUser, log);
    }

    private String getMandotaryLogString(HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        String localIp = httpServletRequest.getLocalAddr();
        String remoteIp = httpServletRequest.getRemoteAddr();
        return sessionId + "," + "|" + userAgent + "|" + "," + localIp + "," + remoteIp + "," + "pentacode-middleware";
    }
}
