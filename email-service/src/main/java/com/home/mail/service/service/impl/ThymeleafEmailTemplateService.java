package com.home.mail.service.service.impl;

import com.home.mail.service.service.EmailTemplateService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class ThymeleafEmailTemplateService implements EmailTemplateService {


    private final TemplateEngine templateEngine;

    public ThymeleafEmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String getSubject(String templateName) {
        Context context = new Context();
        return templateEngine.process("emails/" + templateName + "_subject", context);
    }

    @Override
    public String getContent(String templateName) {
        Context context = new Context();
        return templateEngine.process("emails/" + templateName + "_content", context);
    }
}
