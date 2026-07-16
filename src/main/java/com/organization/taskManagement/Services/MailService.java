package com.organization.taskManagement.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtpMail(String email, String otp) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(email);
        helper.setSubject("Verify your email - OTP");

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background-color:#f4f6f8; padding:20px;">
               
                <div style="max-width:500px; margin:auto; background:white; padding:25px; border-radius:10px; text-align:center;">
                   
                    <h2 style="color:#333;">Email Verification</h2>
                   
                    <p style="font-size:14px; color:#555;">
                        Use the OTP below to verify your email address:
                    </p>
 
                    <div style="margin:20px 0; font-size:28px; letter-spacing:5px; font-weight:bold; color:#00b894;">
                        """ + otp + """
                    </div>
 
                    <p style="font-size:13px; color:#999;">
                        This OTP is valid for <b>5 minutes</b>.
                    </p>
 
                    <hr style="margin:20px 0;">
 
                    <p style="font-size:12px; color:#aaa;">
                        If you didn't request this, you can safely ignore this email.
                    </p>
 
                </div>
 
            </body>
            </html>
            """;

        helper.setText(htmlContent, true);
        javaMailSender.send(message);
    }

}
