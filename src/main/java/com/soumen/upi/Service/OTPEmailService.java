package com.soumen.upi.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class OTPEmailService {
    private  final JavaMailSender mailSender;

    public OTPEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to,String otp) throws MessagingException {
        MimeMessage mimeMessage=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true);
        helper.setTo(to);
        helper.setSubject("Your OTP(One Time Password) message");
        String htmlMessage="<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>OTP Verification</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', sans-serif;\n" +
                "            background-color: #f4f4f4;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .container {\n" +
                "            background-color: #ffffff;\n" +
                "            max-width: 600px;\n" +
                "            margin: 40px auto;\n" +
                "            padding: 30px;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 4px 8px rgba(0,0,0,0.05);\n" +
                "        }\n" +
                "        .header {\n" +
                "            text-align: center;\n" +
                "            padding-bottom: 20px;\n" +
                "            border-bottom: 1px solid #dddddd;\n" +
                "        }\n" +
                "        .header h2 {\n" +
                "            color: #333333;\n" +
                "        }\n" +
                "        .otp-box {\n" +
                "            margin: 20px auto;\n" +
                "            text-align: center;\n" +
                "            background-color: #f1f1f1;\n" +
                "            padding: 16px;\n" +
                "            border-radius: 6px;\n" +
                "            font-size: 24px;\n" +
                "            font-weight: bold;\n" +
                "            letter-spacing: 4px;\n" +
                "            color: #333333;\n" +
                "            width: fit-content;\n" +
                "        }\n" +
                "        .message {\n" +
                "            margin: 20px 0;\n" +
                "            font-size: 16px;\n" +
                "            color: #444444;\n" +
                "            line-height: 1.5;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            font-size: 13px;\n" +
                "            color: #888888;\n" +
                "            text-align: center;\n" +
                "            margin-top: 30px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div class=\"container\">\n" +
                "    <div class=\"header\">\n" +
                "        <h2>Your OTP Code</h2>\n" +
                "    </div>\n" +
                "\n" +
                "    <p class=\"message\">\n" +
                "        Dear User,<br><br>\n" +
                "        Please use the following One-Time Password (OTP) to complete your verification. This OTP is valid for the next 10 minutes.\n" +
                "    </p>\n" +
                "\n" +
                "    <div class=\"otp-box\">"+otp+"</div>\n" +
                "\n" +
                "    <p class=\"message\">\n" +
                "        If you did not request this, please ignore this email.<br><br>\n" +
                "        Thank you,<br>\n" +
                "        <strong>ListOnGo Team</strong>\n" +
                "    </p>\n" +
                "\n" +
                "    <div class=\"footer\">\n" +
                "        &copy; 2025 ListOnGo. All rights reserved.\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        helper.setText(htmlMessage,true);
        System.out.println(otp);
        helper.setFrom("ListOnGo App For One-Time Password (OTP) <otp.send.by.sm.team@gmail.com>");

        mailSender.send(mimeMessage);
    }
}
