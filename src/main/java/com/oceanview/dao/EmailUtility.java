package com.oceanview.dao;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailUtility {

    private static final String SENDER_EMAIL = "";
    private static final String SENDER_PASSWORD = "";

    public static void sendBookingConfirmation(String recipientEmail, String reservationNumber, String guestName, double totalCost) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Booking Confirmation - Ocean View Resort");

            String emailBody = "Dear " + guestName + ",\n\n"
                    + "Your booking (Reservation #: " + reservationNumber + ") has been successfully confirmed.\n"
                    + "Total Cost: LKR " + totalCost + "\n\n"
                    + "Thank you for choosing Ocean View Resort!";

            message.setText(emailBody);
            Transport.send(message);
            System.out.println("Confirmation email queued successfully.");

        } catch (MessagingException e) {
            System.err.println("Email failed to send. Proceeding with booking. Error: " + e.getMessage());
        }
    }
}
