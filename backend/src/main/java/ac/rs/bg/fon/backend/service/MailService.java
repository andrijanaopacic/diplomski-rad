package ac.rs.bg.fon.backend.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
 
	private final JavaMailSender mailSender;
 
	@Value("${app.mail.from}")
	private String from;
 
	public MailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
 
	public void sendTemplatedHtml(String to, String subject, String templateName, Map<String, String> values) {
		try {
			ClassPathResource resource = new ClassPathResource("templates/" + templateName);
			String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
 
			for (Map.Entry<String, String> entry : values.entrySet()) {
				html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
			}
 
			sendHtml(to, subject, html);
		} catch (IOException e) {
			throw new RuntimeException("Nije moguće učitati mejl template: " + templateName, e);
		}
	}
 
	public void send(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}
 
	public void sendHtml(String to, String subject, String htmlContent) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Slanje HTML mejla nije uspelo.", e);
		}
	}
}
