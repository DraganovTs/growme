package com.home.mail.service;

public interface EmailTemplateService {

    String getSubject(String templateName);
    String getContent(String templateName);
}
