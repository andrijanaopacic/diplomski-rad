package ac.rs.bg.fon.backend.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MailService {

	private final RestClient restClient = RestClient.create();

	@Value("${app.mail.from}")
	private String from;

	@Value("${mailtrap.api.token}")
	private String apiToken;

	@Value("${mailtrap.sandbox.id}")
	private String sandboxId;

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
		posaljiPrekoApija(to, subject, null, text);
	}

	public void sendHtml(String to, String subject, String htmlContent) {
		posaljiPrekoApija(to, subject, htmlContent, null);
	}

	private void posaljiPrekoApija(String to, String subject, String html, String text) {
		String url = "https://sandbox.api.mailtrap.io/api/send/" + sandboxId;

		String fromIme = from;
		String fromMejl = from;
		if (from.contains("<") && from.contains(">")) {
			fromIme = from.substring(0, from.indexOf("<")).trim();
			fromMejl = from.substring(from.indexOf("<") + 1, from.indexOf(">")).trim();
		}

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("from", Map.of("email", fromMejl, "name", fromIme));
		body.put("to", List.of(Map.of("email", to)));
		body.put("subject", subject);
		if (html != null) {
			body.put("html", html);
		}
		if (text != null) {
			body.put("text", text);
		}

		try {
			restClient.post()
					.uri(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
					.contentType(MediaType.APPLICATION_JSON)
					.body(body)
					.retrieve()
					.toBodilessEntity();
		} catch (Exception ex) {
			throw new RuntimeException("Slanje mejla preko Mailtrap API-ja nije uspelo.", ex);
		}
	}
}