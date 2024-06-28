package com.pentacode.pentacode_middleware.email_gateway.dto;


import com.pentacode.pentacode_middleware.email_gateway.enu.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRequest {
    private String to;
    private String subject;
    private String body;
    private EmailType type;
    private EmailPrority emailPrority;
}
