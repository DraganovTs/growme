package com.home.mail.service.service.impl;

import com.home.mail.service.repository.EmailRecordRepository;
import com.home.mail.service.service.EmailRecordsCleanupService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class EmailRecordsCleanupServiceImpl implements EmailRecordsCleanupService {

    private final EmailRecordRepository emailRecordRepository;

    public EmailRecordsCleanupServiceImpl(EmailRecordRepository emailRecordRepository) {
        this.emailRecordRepository = emailRecordRepository;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    @Override
    public void cleanupOldRecords() {
        Instant cutoffDate = Instant.now().minus(7, ChronoUnit.DAYS);

        try {
            int deletedCount = emailRecordRepository.deleteOlderThan(cutoffDate);
            log.info("Deleted {} old email records", deletedCount);
        }catch (Exception e){
            log.error("Failed to clean up old email records" ,e);
            throw new EmailDeletionException("Failed to clean up old email records");
        }

    }
}
