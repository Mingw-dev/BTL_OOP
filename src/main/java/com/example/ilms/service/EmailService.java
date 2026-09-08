package com.example.ilms.service;

import com.example.ilms.mail.dto.MailRequest;
import com.example.ilms.mail.template.ForgotPasswordTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void sendMimeMessage(MailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(senderEmail, "ILMS System");
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), true);
            mailSender.send(message);
            log.info("Email đã được gửi thành công tới: {}", request.getTo());

        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email tới {}: {}", request.getTo(), e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi gửi email: {}", e.getMessage());
        }
    }

    @Async
    public void sendForgotPasswordEmail(String toEmail, String newPassword) {
        String htmlContent = ForgotPasswordTemplate.buildTemplate(newPassword);

        MailRequest request = MailRequest.builder()
                .to(toEmail)
                .subject("[ILMS] Mật khẩu mới của bạn")
                .body(htmlContent)
                .build();

        sendMimeMessage(request);
    }
}