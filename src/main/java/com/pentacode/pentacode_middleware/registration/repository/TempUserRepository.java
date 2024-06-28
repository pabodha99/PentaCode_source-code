package com.pentacode.pentacode_middleware.registration.repository;

import com.pentacode.pentacode_middleware.registration.entity.TempUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TempUserRepository extends JpaRepository<TempUser, Integer> {
    Optional<TempUser> findTop1ByEmailOrderByIdDesc(String email);
}
