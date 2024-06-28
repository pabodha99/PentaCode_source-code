package com.pentacode.pentacode_middleware.appointment.repository;

import com.pentacode.pentacode_middleware.appointment.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.freeSlot.id = :slotId")
    Optional<Appointment> findByPatientIdAndSlotId(int patientId, int slotId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId ORDER BY a.createdAt DESC")
    Page<Appointment> findByPatientId(int patientId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.freeSlot.doctor.id = :doctorId ORDER BY a.createdAt DESC")
    Page<Appointment> findByDoctorId(int doctorId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.freeSlot.doctor.id = :doctorId AND a.freeSlot.id = :slotId ORDER BY a.createdAt DESC")
    Page<Appointment> findByDoctorIdAndSlotId(int doctorId, int slotId, Pageable pageable);
}
