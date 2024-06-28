package com.pentacode.pentacode_middleware.freeslots.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeSlotCreateRequest {
    private String startTime;
    private String endTime;
}
