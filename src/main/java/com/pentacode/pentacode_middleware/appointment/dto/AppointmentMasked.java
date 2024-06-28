package com.pentacode.pentacode_middleware.appointment.dto;

import com.pentacode.pentacode_middleware.appointment.entity.Appointment;
import com.pentacode.pentacode_middleware.freeslots.entity.FreeSlot;
import com.pentacode.pentacode_middleware.registration.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotMasked;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentMasked {
    private int id;
    private LocalDateTime createdAt;
    private int patientId;
    private String patientFirstName;
    private String patientLastName;
    private FreeSlotMasked freeSlotMasked;

    public AppointmentMasked(Appointment appointment) {
        this.id = appointment.getId();
        this.createdAt = appointment.getCreatedAt();
        this.patientId = appointment.getPatient().getId();
        this.patientFirstName = appointment.getPatient().getFirstname();
        this.patientLastName = appointment.getPatient().getLastname();
        this.freeSlotMasked = new FreeSlotMasked(appointment.getFreeSlot());
    }
}
