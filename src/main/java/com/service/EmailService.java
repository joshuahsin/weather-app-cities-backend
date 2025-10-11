package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTemporaryPassword(String to, String username, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset - Weather Cities App");
        message.setText(buildEmailContent(username, temporaryPassword));
        
        mailSender.send(message);
    }

    private String buildEmailContent(String username, String temporaryPassword) {
        return String.format("""
            Hello %s,
            
            You have requested a password reset for your Weather Cities account.
            
            Your temporary password is: %s
            
            Please log in with this temporary password and change it to a new password as soon as possible.
            
            If you did not request this password reset, please ignore this email.
            
            Best regards,
            Weather Cities Team
            """, username, temporaryPassword);
    }
}
