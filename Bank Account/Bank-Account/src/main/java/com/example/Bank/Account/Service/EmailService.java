package com.example.Bank.Account.Service;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendAccountCreationEmail(String to,String customerName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Created Successfully");
        message.setText("Dear " + customerName + ",\n\n" +
                "Your account has been created successfully.\n" +
                "Thank you for choosing our bank.\n\n" +
                "Best regards,\n" +
                "Team Bank");

        mailSender.send(message);
    }
    public void sendAccountDeletedEmail(String to,String customerName){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Deleted Successfull");
        message.setText("Dear "+customerName+ ",\n\n" +
                "Your Account has been deleted from our bank. \n"+
                "Thank you for choosing our bank.\n\n" +
                "Best regards,\n" +
                "Team Bank");
        mailSender.send(message);
    }
    public void sendAddBalanceEmail(String to,String customerName,Double amount){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Amount Credited");
        message.setText("Dear "+customerName+ ",\n\n" +
                "Amount of Rs:"+amount+" has been credited to your account \n"+
                "Thank you for choosing our bank.\n\n" +
                "Best regards,\n" +
                "Team Bank");
        mailSender.send(message);
    }
}