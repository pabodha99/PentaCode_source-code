package com.pentacode.pentacode_middleware.appointment.service;

import com.pentacode.pentacode_middleware.appointment.dto.AppointmentCreateResponse;
import com.pentacode.pentacode_middleware.appointment.dto.AppointmentMasked;
import com.pentacode.pentacode_middleware.appointment.dto.AppointmentViewResponse;
import com.pentacode.pentacode_middleware.appointment.entity.Appointment;
import com.pentacode.pentacode_middleware.appointment.repository.AppointmentRepository;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotCreateRequest;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotCreateResponse;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotMasked;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotViewResponse;
import com.pentacode.pentacode_middleware.freeslots.entity.FreeSlot;
import com.pentacode.pentacode_middleware.freeslots.repository.FreeSlotRepository;
import com.pentacode.pentacode_middleware.registration.entity.User;
import com.pentacode.pentacode_middleware.registration.enums.Role;
import com.pentacode.pentacode_middleware.registration.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AppointmentService {
    private static Logger audit = LogManager.getLogger("audit-log");

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private FreeSlotRepository freeSlotRepository;

    public ResponseEntity<AppointmentCreateResponse> createAppointment(String slotId, User user, String log) {
        try {
            //data validation
            if (!user.getRole().equals(Role.PATIENT)) {
                audit.info(log + ",create-appointment,error,E7192,dont have permissions to create");
                return ResponseEntity.ok(AppointmentCreateResponse.builder().statusCode("E7192")
                        .statusDetails("dont have permissions to create appointments").build());
            }
            Optional<Appointment> opt = appointmentRepository.findByPatientIdAndSlotId(user.getId(), Integer.parseInt(slotId));
            if (opt.isPresent()) {
                audit.info(log + ",create-appointment,error,E5794,appointment already exists");
                return ResponseEntity.ok(AppointmentCreateResponse.builder().statusCode("E5794")
                        .statusDetails("appointment already exists").build());
            }
            Optional<FreeSlot> optionalFreeSlot = freeSlotRepository.findById(Integer.parseInt(slotId));
            if (!optionalFreeSlot.isPresent()) {
                audit.info(log + ",create-appointment,error,E9756,no free slot with id " + slotId);
                return ResponseEntity.ok(AppointmentCreateResponse.builder().statusCode("E9756")
                        .statusDetails("no free slot with id " + slotId).build());
            }
            var appointment = Appointment.builder().createdAt(LocalDateTime.now()).patient(user).freeSlot(optionalFreeSlot.get()).build();
            appointmentRepository.save(appointment);
            FreeSlot freeSlot = optionalFreeSlot.get();
            var freeSlotMasked = FreeSlotMasked.builder().id(freeSlot.getId()).start(freeSlot.getStart()).end(freeSlot.getEnd())
                    .createdAt(freeSlot.getCreatedAt()).doctorEmail(freeSlot.getDoctor().getEmail()).doctorFirstname(freeSlot.getDoctor().getFirstname())
                    .doctorLastname(freeSlot.getDoctor().getLastname()).build();
            var appointmentMasked = AppointmentMasked.builder().id(appointment.getId()).createdAt(appointment.getCreatedAt())
                    .patientId(user.getId()).patientFirstName(user.getFirstname()).patientLastName(user.getLastname())
                    .freeSlotMasked(freeSlotMasked).build();
            return ResponseEntity.ok(AppointmentCreateResponse.builder().appointmentMasked(appointmentMasked).statusCode("S1000")
                    .statusDetails("appointment created").build());
        } catch (Exception e) {
            audit.info(log + ",create-appointment,error,E4562,system error," + e);
            return ResponseEntity.ok(AppointmentCreateResponse.builder().statusCode("E4562").statusDetails("system error").build());
        }
    }

    public ResponseEntity<AppointmentViewResponse> getMyAppointments(int pageNumber, int pageSize, User user, String log) {
        try {
            if (!user.getRole().equals(Role.PATIENT)) {
                audit.info(log + ",view-appointments-patient,error,E7192,dont have permissions to view");
                return ResponseEntity.ok(AppointmentViewResponse.builder().statusCode("E7192")
                        .statusDetails("dont have permissions to view appointments").build());
            }
            Pageable requestPage = null;
            //special case when front end need full list of products (condition - > if pageNumber is -1)
            if (pageNumber == -1) {
                int count = (int) appointmentRepository.count();
                pageNumber = 0;
                pageSize = count;
            }
            requestPage = PageRequest.of(pageNumber, pageSize);
            Page<Appointment> appointments = appointmentRepository.findByPatientId(user.getId(), requestPage);
            Page<AppointmentMasked> maskedAppointmentList = appointments.map(AppointmentMasked::new);
            return ResponseEntity.ok(AppointmentViewResponse.builder().appointmentMaskeds(maskedAppointmentList)
                    .statusCode("S1000").statusDetails("download successful").build());
        } catch (Exception e) {
            audit.info(log + ",view-freeslot,view-appointments-patient,E4562,system error," + e);
            return ResponseEntity.ok(AppointmentViewResponse.builder().statusCode("E4562").statusDetails("system error").build());
        }
    }

    public ResponseEntity<AppointmentViewResponse> getAppointments(int pageNumber, int pageSize, int slotId, User user, String log) {
        try {
            if (!user.getRole().equals(Role.DOCTOR)) {
                audit.info(log + ",view-appointments-doctor,error,E7192,dont have permissions to view");
                return ResponseEntity.ok(AppointmentViewResponse.builder().statusCode("E7192")
                        .statusDetails("dont have permissions to view appointments").build());
            }
            Pageable requestPage = null;
            //special case when front end need full list of products (condition - > if pageNumber is -1)
            if (pageNumber == -1) {
                int count = (int) appointmentRepository.count();
                pageNumber = 0;
                pageSize = count;
            }
            requestPage = PageRequest.of(pageNumber, pageSize);
            Page<Appointment> appointments = null;
            if (slotId == -1)
                appointments = appointmentRepository.findByDoctorId(user.getId(), requestPage);
            else
                appointments = appointmentRepository.findByDoctorIdAndSlotId(user.getId(), slotId, requestPage);
            Page<AppointmentMasked> maskedAppointmentList = appointments.map(AppointmentMasked::new);
            return ResponseEntity.ok(AppointmentViewResponse.builder().appointmentMaskeds(maskedAppointmentList)
                    .statusCode("S1000").statusDetails("download successful").build());
        } catch (Exception e) {
            audit.info(log + ",view-appointments-doctor,error,E4562,system error," + e);
            return ResponseEntity.ok(AppointmentViewResponse.builder().statusCode("E4562").statusDetails("system error").build());
        }
    }
}
