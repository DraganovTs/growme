package com.home.mail.repository;

import com.home.mail.model.EmailRecord;
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
    void deleteOlderThan(@Param("cutoffDate") Instant cutoffDate);
}
