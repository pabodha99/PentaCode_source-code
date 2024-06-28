package com.pentacode.pentacode_middleware.freeslots.dto;

import com.pentacode.pentacode_middleware.freeslots.entity.FreeSlot;
import com.pentacode.pentacode_middleware.registration.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeSlotMasked {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime createdAt;
    private String doctorFirstname;
    private String doctorLastname;
    private String doctorEmail;

    public FreeSlotMasked(FreeSlot freeSlot) {
        this.id = freeSlot.getId();
        this.start = freeSlot.getStart();
        this.end = freeSlot.getEnd();
        this.createdAt = freeSlot.getCreatedAt();
        this.doctorFirstname = freeSlot.getDoctor().getFirstname();
        this.doctorLastname = freeSlot.getDoctor().getLastname();
        this.doctorEmail = freeSlot.getDoctor().getEmail();
    }
}
