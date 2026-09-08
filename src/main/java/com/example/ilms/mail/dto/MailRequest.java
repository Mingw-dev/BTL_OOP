package com.example.ilms.mail.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MailRequest {
    private String to;
    private String subject;
    private String body;
}