package com.home.mail.service.service.impl;

import com.home.mail.service.exception.EmailSendingException;
import com.home.mail.service.model.entity.EmailRecord;
import com.home.mail.service.repository.EmailRecordRepository;
import com.home.mail.service.service.EmailSenderService;
import com.home.mail.service.service.EmailTemplateService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    private final SendGrid sendGrid;
    private final EmailTemplateService templateService;
    private final EmailRecordRepository emailRecordRepository;

    public EmailSenderServiceImpl( EmailTemplateService templateService,
                                  EmailRecordRepository emailRecordRepository) {
        this.sendGrid = new SendGrid(sendGridApiKey);
        this.templateService = templateService;
        this.emailRecordRepository = emailRecordRepository;
    }

    @Override
    public void sendProductAddConfirmation(String email) {
        String subject = templateService.getSubject("product_add_confirmation");
        String content = templateService.getContent("product_add_confirmation");
        sendAndStoreEmail(email, subject, content , "PRODUCT_ADD_CONFIRMATION");
    }

    @Override
    public void sendAccountDeletionConfirmation(String email) {
        String subject = templateService.getSubject("account_deletion_confirmation");
        String content = templateService.getContent("account_deletion_confirmation");
        sendAndStoreEmail(email, subject, content , "PRODUCT_DELETION_CONFIRMATION" );
    }

    @Override
    public void sendAccountCompletionConfirmation(String email) {
        String subject = templateService.getSubject("account_completion_confirmation");
        String content = templateService.getContent("account_completion_confirmation");
        sendAndStoreEmail(email, subject, content , "PRODUCT_COMPLETION_CONFIRMATION");
    }

    @Override
    public void sendAccountUpdateConfirmation(String email) {
        String subject = templateService.getSubject("account_update_confirmation");
        String content = templateService.getContent("account_update_confirmation");
        sendAndStoreEmail(email, subject, content,"PRODUCT_ADD_CONFIRMATION");
    }
    private void sendAndStoreEmail(String email, String subject, String content,
                                   String emailType) {
        EmailRecord record = EmailRecord.builder()
                .recipientEmail(email)
                .emailType(emailType)
                .subject(subject)
                .content(content)
                .language("us") //TODO automate language
                .success(false)
                .build();

        try {
            Response response = sendViaSendGrid(email, subject, content);

            record.setSuccess(true);
            emailRecordRepository.save(record);

            log.debug("Email sent and stored successfully to {}", email);

        } catch (IOException ex) {
            record.setSuccess(false);
            record.setErrorMessage(ex.getMessage());
            emailRecordRepository.save(record);

            log.error("Failed to send email to {}", email, ex);
            throw new EmailSendingException("Failed to send email");
        }
    }

    private Response sendViaSendGrid(String email, String subject, String content) throws IOException {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(email);
        Content emailContent = new Content("text/html", content);
        Mail mail = new Mail(from, subject, to, emailContent);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        return sendGrid.api(request);
    }

}
