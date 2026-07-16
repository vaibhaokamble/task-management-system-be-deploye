package com.organization.taskManagement.Repository;

import com.organization.taskManagement.Model.EmailOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtpEntity, Long> {
    Optional<EmailOtpEntity> findByEmail(String email);
}
