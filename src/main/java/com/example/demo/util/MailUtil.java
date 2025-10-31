package com.example.demo.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.*;

@Component
public class MailUtil {
  @Autowired
  private JavaMailSender mailSender;

  public void sendMail(String from, String to, String title, String text) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(title);
      helper.setText(text, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
}
