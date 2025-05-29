USE emails;

DROP TABLE IF EXISTS emails;

CREATE TABLE IF NOT EXISTS emails (
                                             id BINARY(16) PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    email_type VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT,
    language VARCHAR(10) NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN NOT NULL,
    error_message TEXT,

    INDEX idx_recipient_email (recipient_email),
    INDEX idx_email_type (email_type),
    INDEX idx_sent_at (sent_at),
    INDEX idx_success (success)
    );

CREATE OR REPLACE VIEW failed_emails AS
SELECT id, recipient_email, email_type, sent_at, error_message
FROM emails
WHERE success = false;

CREATE OR REPLACE VIEW successful_emails_by_type AS
SELECT email_type, COUNT(*) as count
FROM emails
WHERE success = true
GROUP BY email_type;
