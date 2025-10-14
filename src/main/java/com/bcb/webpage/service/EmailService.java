package com.bcb.webpage.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.bcb.webpage.model.webpage.entity.ConfigurationEmailAccountEntity;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final TemplateEngine templateEngine;

    public EmailService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * 
     * @return
     */
    public JavaMailSender getSystemEmailSender(ConfigurationEmailAccountEntity email) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.mime.charset", "UTF");
        
        mailSender.setJavaMailProperties(props);
        mailSender.setHost(email.getServerAddress());
        mailSender.setPort(Integer.parseInt(email.getServerPort()));
        mailSender.setUsername(email.getName());
        mailSender.setPassword(email.getAccess());

        return mailSender;
    }


    public void sendMimeMessage2(JavaMailSender sender, String template, Map<String, String> params) throws IOException, MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setReplyTo("servicioaclientes@bcbcasadebolsa.com");
        mimeMessageHelper.setFrom(params.get("from"));
        mimeMessageHelper.setTo(params.get("to"));
        mimeMessageHelper.setSubject(params.get("subject"));

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("username", params.get("username"));
        vars.put("magicLink", params.get("magicLink"));
        vars.put("expirationTime", params.get("expirationTime"));

        Context context = new Context();
        context.setVariables(vars);

        String processedContent = templateEngine.process(template, context);
        mimeMessage.setContent(processedContent, "text/html; charset=utf-8");

        sender.send(mimeMessage);
    }

    public void sendRecoverPasswordMessage(JavaMailSender sender, String template, Map<String, String> params) throws MessagingException {
        
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        String from = params.get("from");
        String to = params.get("to");
        String subject = params.get("subject");
        
        String title = params.get("subject"); // Title
        String username = params.get("username"); // Username
        String resetPasswordLink = params.get("resetPasswordLink"); // MagicLink

        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("title", title);
        vars.put("username", username);
        vars.put("expirationTime", params.get("expirationTime"));
        vars.put("resetPasswordLink", resetPasswordLink);

        Context context = new Context();
        context.setVariables(vars);

        String processedContent = templateEngine.process(template, context);
        mimeMessage.setContent(processedContent, "text/html; charset=utf-8");

        sender.send(mimeMessage);
    }

}
