package com.pentacode.pentacode_middleware.freeslots.repository;

import com.pentacode.pentacode_middleware.freeslots.entity.FreeSlot;
import com.pentacode.pentacode_middleware.uploads.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeSlotRepository extends JpaRepository<FreeSlot, Integer> {
    @Query("SELECT f FROM FreeSlot f WHERE f.end > CURRENT_TIMESTAMP")
    Page<FreeSlot> findAllActiveSlots(Pageable pageable);

    @Query("SELECT f FROM FreeSlot f WHERE f.doctor.id = :userId AND f.end > CURRENT_TIMESTAMP")
    Page<FreeSlot> findActiveSlotsByUserId(int userId, Pageable pageable);
}
