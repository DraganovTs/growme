package com.home.mail.service.impl;

import com.home.mail.exception.EmailSendingException;
import com.home.mail.repository.EmailRecordRepository;
import com.home.mail.service.EmailSenderService;
import com.home.mail.service.EmailTemplateService;
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
    

    private final EmailTemplateService templateService;
    private final EmailRecordRepository emailRecordRepository;
    private final EmailTemplateService emailTemplateService;

    public EmailSenderServiceImpl(EmailTemplateService templateService,
                                  EmailRecordRepository emailRecordRepository, EmailTemplateService emailTemplateService) {
        this.templateService = templateService;
        this.emailRecordRepository = emailRecordRepository;
        this.emailTemplateService = emailTemplateService;
    }

    @Override
    public void sendProductAddConfirmation(String email) {
        String subject = templateService.getSubject("product_add_confirmation");
        String content = templateService.getContent("product_add_confirmation");
        sendEmail(email, subject, content);
    }

    @Override
    public void sendAccountDeletionConfirmation(String email) {
        String subject = templateService.getSubject("account_deletion_confirmation");
        String content = templateService.getContent("account_deletion_confirmation");
        sendEmail(email, subject, content);
    }

    @Override
    public void sendAccountCompletionConfirmation(String email) {
        String subject = templateService.getSubject("account_completion_confirmation");
        String content = templateService.getContent("account_completion_confirmation");
        sendEmail(email, subject, content);
    }

    @Override
    public void sendAccountUpdateConfirmation(String email) {
        String subject = templateService.getSubject("account_update_confirmation");
        String content = templateService.getContent("account_update_confirmation");
        sendEmail(email, subject, content);
    }
    private void sendEmail(String toEmail, String subject, String htmlContent) {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                log.error("SendGrid API error: {} - {}", response.getStatusCode(), response.getBody());
                throw new EmailSendingException("Failed to send email via SendGrid");
            }
            log.debug("Email sent successfully to {}", toEmail);
        } catch (IOException ex) {
            log.error("Error sending email to {}", toEmail, ex);
            throw new EmailSendingException("Error communicating with SendGrid");
        }
    }
}
