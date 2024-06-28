package com.pentacode.pentacode_middleware.freeslots.dto;

import com.pentacode.pentacode_middleware.freeslots.entity.FreeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeSlotViewResponse {
    private String statusCode;
    private String statusDetails;
    private Page<FreeSlotMasked> freeSlotList;
}
