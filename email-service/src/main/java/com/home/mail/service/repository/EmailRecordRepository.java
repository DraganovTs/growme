package com.home.mail.service.repository;

import com.home.mail.service.model.entity.EmailRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface EmailRecordRepository extends JpaRepository<EmailRecord, UUID> {

    @Modifying
    @Query("DELETE FROM EmailRecord e WHERE e.sentAt < :cutoffDate")
    int deleteOlderThan(@Param("cutoffDate") Instant cutoffDate);
}
