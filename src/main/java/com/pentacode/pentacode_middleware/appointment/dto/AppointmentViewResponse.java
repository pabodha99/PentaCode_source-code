package com.pentacode.pentacode_middleware.appointment.dto;

import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotMasked;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentViewResponse {
    private String statusCode;
    private String statusDetails;
    private Page<AppointmentMasked> appointmentMaskeds;
}
