package com.pentacode.pentacode_middleware.freeslots.service;

import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotCreateRequest;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotCreateResponse;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotMasked;
import com.pentacode.pentacode_middleware.freeslots.dto.FreeSlotViewResponse;
import com.pentacode.pentacode_middleware.freeslots.entity.FreeSlot;
import com.pentacode.pentacode_middleware.freeslots.repository.FreeSlotRepository;
import com.pentacode.pentacode_middleware.registration.entity.User;
import com.pentacode.pentacode_middleware.registration.enums.Role;
import com.pentacode.pentacode_middleware.uploads.dto.ImageList;
import com.pentacode.pentacode_middleware.uploads.dto.ImageResponse;
import com.pentacode.pentacode_middleware.uploads.dto.UploadResponse;
import com.pentacode.pentacode_middleware.uploads.entity.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FreeSlotService {
    private static Logger audit = LogManager.getLogger("audit-log");

    @Autowired
    private FreeSlotRepository freeSlotRepository;

    public ResponseEntity<FreeSlotCreateResponse> createFreeSlot(FreeSlotCreateRequest freeSlotCreateRequest, User user, String log) {
        try {
            //data validation
            if (freeSlotCreateRequest.getStartTime() == null || freeSlotCreateRequest.getEndTime() == null) {
                audit.info(log + ",create-freeslot,error,E1232,start time or end time is null");
                return ResponseEntity.ok(FreeSlotCreateResponse.builder().statusCode("E1232").statusDetails("E1232,start time or end time is null").build());
            }
            if (!user.getRole().equals(Role.DOCTOR)) {
                audit.info(log + ",create-freeslot,error,E7192,dont have permissions to create");
                return ResponseEntity.ok(FreeSlotCreateResponse.builder().statusCode("E7192")
                        .statusDetails("dont have permissions to create free slots").build());
            }
            var freeSlot = FreeSlot.builder().doctor(user).createdAt(LocalDateTime.now()).start(LocalDateTime.parse(freeSlotCreateRequest.getStartTime()))
                    .end(LocalDateTime.parse(freeSlotCreateRequest.getEndTime()))
                    .build();
            freeSlotRepository.save(freeSlot);
            var freeSlotMasked = FreeSlotMasked.builder().id(freeSlot.getId()).start(freeSlot.getStart()).end(freeSlot.getEnd())
                    .createdAt(freeSlot.getCreatedAt()).doctorEmail(freeSlot.getDoctor().getEmail()).doctorFirstname(freeSlot.getDoctor().getFirstname())
                    .doctorLastname(freeSlot.getDoctor().getLastname()).build();
            return ResponseEntity.ok(FreeSlotCreateResponse.builder().freeSlot(freeSlotMasked).statusCode("S1000").statusDetails("slot created").build());
        } catch (Exception e) {
            audit.info(log + ",create-freeslot,error,E4562,system error," + e);
            return ResponseEntity.ok(FreeSlotCreateResponse.builder().statusCode("E4562").statusDetails("system error").build());
        }
    }

    public ResponseEntity<FreeSlotViewResponse> getAllFreeSlots(int pageNumber, int pageSize, User user, String log) {
        try {
            if (!user.getRole().equals(Role.PATIENT)) {
                audit.info(log + ",view-freeslot,error,E7192,dont have permissions to view");
                return ResponseEntity.ok(FreeSlotViewResponse.builder().statusCode("E7192")
                        .statusDetails("dont have permissions to view free slots").build());
            }
            Pageable requestPage = null;
            //special case when front end need full list of products (condition - > if pageNumber is -1)
            if (pageNumber == -1) {
                int count = (int) freeSlotRepository.count();
                pageNumber = 0;
                pageSize = count;
            }
            requestPage = PageRequest.of(pageNumber, pageSize);
            Page<FreeSlot> freeSlotsList = freeSlotRepository.findAllActiveSlots(requestPage);
            Page<FreeSlotMasked> maskedFreeSlots = freeSlotsList.map(FreeSlotMasked::new);
            return ResponseEntity.ok(FreeSlotViewResponse.builder().freeSlotList(maskedFreeSlots)
                    .statusCode("S1000").statusDetails("download successful").build());
        } catch (Exception e) {
            audit.info(log + ",view-freeslot,error,E4562,system error," + e);
            return ResponseEntity.ok(FreeSlotViewResponse.builder().statusCode("E4562").statusDetails("system error").build());
        }
    }

    public ResponseEntity<FreeSlotViewResponse> getAllFreeSlotsByCreatedDoctor(int pageNumber, int pageSize, User user, String log) {
        try {
            if (!user.getRole().equals(Role.DOCTOR)) {
                audit.info(log + ",view-created-freeslot,error,E7192,dont have permissions to view");
                return ResponseEntity.ok(FreeSlotViewResponse.builder().statusCode("E7192")
                        .statusDetails("dont have permissions to view created slots").build());
            }
            Pageable requestPage = null;
            //special case when front end need full list of products (condition - > if pageNumber is -1)
            if (pageNumber == -1) {
                int count = (int) freeSlotRepository.count();
                pageNumber = 0;
                pageSize = count;
            }
            requestPage = PageRequest.of(pageNumber, pageSize);
            Page<FreeSlot> freeSlotsList = freeSlotRepository.findActiveSlotsByUserId(user.getId(), requestPage);
            Page<FreeSlotMasked> maskedFreeSlots = freeSlotsList.map(FreeSlotMasked::new);
            return ResponseEntity.ok(FreeSlotViewResponse.builder().freeSlotList(maskedFreeSlots)
                    .statusCode("S1000").statusDetails("download successful").build());
        } catch (Exception e) {
            audit.info(log + ",view-created-freeslot,error,E4562,system error," + e);
            return ResponseEntity.ok(FreeSlotViewResponse.builder().statusCode("E4562").statusDetails("system error").build());
        }
    }
}
