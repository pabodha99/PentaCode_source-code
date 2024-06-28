package com.pentacode.pentacode_middleware.freeslots.dto;

import com.pentacode.pentacode_middleware.freeslots.entity.FreeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeSlotCreateResponse {
    private String statusCode;
    private String statusDetails;
    private FreeSlotMasked freeSlot;
}
