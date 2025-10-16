package com.precious.user.kafka;

import com.precious.shared.dto.EmailNotification;
import com.precious.shared.kafka.KafkaTopics;
import com.precious.user.repository.NotificationLogRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final NotificationLogRepository logRepository;

    public EmailNotificationListener(JavaMailSender mailSender,
                                     @Value("${spring.mail.username}") String fromEmail,
                                     NotificationLogRepository logRepository) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.logRepository = logRepository;
    }

    @KafkaListener(topics = KafkaTopics.NOTIFICATION_EMAIL, groupId = "user-service-email-group")
    public void sendEmail(EmailNotification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(notification.getTo());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getBody(), true); // HTML
            mailSender.send(message);
            System.out.println("📧 Email отправлен: " + notification.getTo());
        } catch (MessagingException e) {
            System.err.println("❌ Ошибка отправки email: " + e.getMessage());
        }
    }
}