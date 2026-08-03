package pl.volleylove.antenka.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${antenka.mail.from}")
    private String emailFrom;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrganizerOptOutNotification(String organizerEmail, String matchName) throws MessagingException {
        String htmlContent = EmailTemplateFactory.getOrganizerOptOutTemplate(matchName);
        sendHtmlEmail(organizerEmail, EmailTemplateFactory.SUBJECT_ORGANIZER_PLAYER_OPT_OUT + matchName, htmlContent);
    }

    public void sendPlayerOptOutNotification(String playerEmail, String matchName) throws MessagingException {
        String htmlContent = EmailTemplateFactory.getPlayerOptOutTemplate(matchName);
        sendHtmlEmail(playerEmail, EmailTemplateFactory.SUBJECT_PLAYER_OPT_OUT + matchName, htmlContent);
    }

    private void sendHtmlEmail(String recipient, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(emailFrom);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }
}