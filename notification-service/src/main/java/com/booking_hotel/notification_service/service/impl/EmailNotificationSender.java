package com.booking_hotel.notification_service.service.impl;

import com.booking_hotel.notification_service.entity.NotificationType;
import com.booking_hotel.notification_service.service.NotificationSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailNotificationSender(
            JavaMailSender mailSender,
            @Value("${app.mail.from:${spring.mail.username:}}") String fromEmail
    ) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    public boolean supports(NotificationType type) {
        return type == NotificationType.EMAIL;
    }

    @Override
    public void send(String recipient, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipient);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        if (StringUtils.hasText(fromEmail)) {
            mailMessage.setFrom(fromEmail);
        }

        mailSender.send(mailMessage);
    }
}
