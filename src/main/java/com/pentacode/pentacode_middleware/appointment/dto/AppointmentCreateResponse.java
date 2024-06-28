package com.pentacode.pentacode_middleware.appointment.dto;

import com.pentacode.pentacode_middleware.appointment.entity.Appointment;
import com.pentacode.pentacode_middleware.freeslots.entity.FreeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreateResponse {
    private String statusCode;
    private String statusDetails;
    private AppointmentMasked appointmentMasked;
}
