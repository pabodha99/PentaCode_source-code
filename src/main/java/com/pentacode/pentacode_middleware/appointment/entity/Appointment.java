package com.pentacode.pentacode_middleware.appointment.entity;

import com.pentacode.pentacode_middleware.freeslots.entity.FreeSlot;
import com.pentacode.pentacode_middleware.registration.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User patient;
    @ManyToOne(targetEntity = FreeSlot.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "freeslot_id")
    private FreeSlot freeSlot;
    private LocalDateTime createdAt;
}
