package com.pentacode.pentacode_middleware.uploads.repository;

import com.pentacode.pentacode_middleware.uploads.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    Page<Image> findByOwnerIdOrderByCreatedAtDesc(int userId, Pageable pageable);
}
