package com.application.service;

import com.application.exception.mail.MailNotSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender sender;

    @Async
    public void sendEmail(String subject, String message, String to) {
        try {
            MimeMessageHelper helper = new MimeMessageHelper(sender.createMimeMessage());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message);
            sender.send(helper.getMimeMessage());
            log.info("Email send successfully, subject {}, to {}", subject, to);
        } catch (Exception e) {
            log.warn("Failed to send email with subject {}, due to {}", subject, e.getMessage(), e);
            throw new MailNotSendException();
        }
    }
}
