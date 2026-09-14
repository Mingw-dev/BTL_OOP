package com.example.ilms.service;

import com.example.ilms.mail.dto.MailRequest;
import com.example.ilms.mail.template.ForgotPasswordTemplate;
import com.example.ilms.mail.template.PasswordChangedAlert;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    private void sendMail(MailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(senderEmail, "ILMS Support");
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), true);

            mailSender.send(message);
            log.info("Email đã được gửi thành công tới: {}", request.getTo());

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Lỗi đóng gói email tới {}: {}", request.getTo(), e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi gửi email tới {}: {}", request.getTo(), e.getMessage());
        }
    }

    @Async
    public void sendForgotPasswordEmail(String toEmail, String resetUrl) {
        String htmlContent = ForgotPasswordTemplate.buildTemplate(resetUrl);

        MailRequest request = MailRequest.builder()
                .to(toEmail)
                .subject("[ILMS] Yêu cầu đặt lại mật khẩu tài khoản")
                .body(htmlContent)
                .build();

        sendMail(request);
    }

    @Async
    public void sendPasswordChangedAlert(String toEmail) {
        String htmlContent = PasswordChangedAlert.buildTemplate(toEmail);

        MailRequest request = MailRequest.builder()
                .to(toEmail)
                .subject("[ILMS] Cảnh báo bảo mật: Mật khẩu đã được thay đổi")
                .body(htmlContent)
                .build();

        sendMail(request);
    }

}