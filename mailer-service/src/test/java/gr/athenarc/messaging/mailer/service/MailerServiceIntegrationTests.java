package gr.athenarc.messaging.mailer.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import gr.athenarc.messaging.mailer.config.MailerProperties;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MailerServiceIntegrationTests {

	@RegisterExtension
	static final GreenMailExtension primary = new GreenMailExtension(ServerSetup.SMTP.dynamicPort());

	@RegisterExtension
	static final GreenMailExtension secondary = new GreenMailExtension(ServerSetup.SMTP.dynamicPort());

	@RegisterExtension
	static final GreenMailExtension authenticated = new GreenMailExtension(ServerSetup.SMTP.dynamicPort())
			.withConfiguration(GreenMailConfiguration.aConfig().withUser("sender@example.org", "sender", "secret"));

	@RegisterExtension
	static final GreenMailExtension tls = new GreenMailExtension(ServerSetup.SMTPS.dynamicPort())
			.withConfiguration(GreenMailConfiguration.aConfig().withUser("sender@example.org", "sender", "secret"));

	private static MailerProperties.Config mailer(GreenMailExtension server, String from) {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", "127.0.0.1");
		props.setProperty("mail.smtp.port", String.valueOf(server.getSmtp().getPort()));
		props.setProperty("mail.from", from);
		MailerProperties.Config config = new MailerProperties.Config();
		config.setProps(props);
		return config;
	}

	private static MailerProperties properties(boolean redirect, boolean includeMasked) {
		MailerProperties properties = new MailerProperties();
		properties.getMailer().put("primary", mailer(primary, "primary@example.org"));
		properties.getMailer().put("secondary", mailer(secondary, "secondary@example.org"));
		properties.getRedirect().setEnabled(redirect);
		properties.getRedirect().setTo(List.of("dev@example.org"));
		properties.getRedirect().setIncludeOriginalRecipientsMasked(includeMasked);
		return properties;
	}

	private static EmailMessage message(String from, boolean html) {
		EmailMessage message = new EmailMessage();
		message.setFrom(from);
		message.setTo(List.of("alice@gmail.com"));
		message.setCc(List.of("bob@athenarc.gr"));
		message.setBcc(List.of("carol@mail.example.org"));
		message.setSubject("Hello");
		message.setText(html ? "<p>Body</p>" : "Body");
		message.setHtml(html);
		return message;
	}

	@Test
	void deliversToAllRecipientTypes() throws Exception {
		new MultiMailerServiceService(properties(false, false)).sendMail(message("primary@example.org", false));

		MimeMessage[] received = primary.getReceivedMessages();
		assertEquals(3, received.length, "one copy per envelope recipient");
		MimeMessage first = received[0];
		assertEquals("Hello", first.getSubject());
		assertEquals("Body", GreenMailUtil.getBody(first).trim());
		assertEquals("alice@gmail.com", first.getRecipients(Message.RecipientType.TO)[0].toString());
		assertEquals("bob@athenarc.gr", first.getRecipients(Message.RecipientType.CC)[0].toString());
		assertNull(first.getRecipients(Message.RecipientType.BCC), "BCC must not appear in headers");
	}

	@Test
	void sendsHtmlContentType() throws Exception {
		new MultiMailerServiceService(properties(false, false)).sendMail(message("primary@example.org", true));

		MimeMessage first = primary.getReceivedMessages()[0];
		assertTrue(first.getContentType().startsWith("text/html"), first.getContentType());
	}

	@Test
	void selectsMailerBySender() {
		new MultiMailerServiceService(properties(false, false)).sendMail(message("secondary@example.org", false));

		assertEquals(0, primary.getReceivedMessages().length);
		assertEquals(3, secondary.getReceivedMessages().length);
	}

	@Test
	void redirectDeliversOnlyToConfiguredTargets() throws Exception {
		new MultiMailerServiceService(properties(true, false)).sendMail(message("primary@example.org", false));

		MimeMessage[] received = primary.getReceivedMessages();
		assertEquals(1, received.length);
		MimeMessage only = received[0];
		assertEquals("dev@example.org", only.getAllRecipients()[0].toString());
		assertEquals(1, only.getAllRecipients().length);
		assertTrue(only.getSubject().startsWith("[REDIRECTED] "));
		assertEquals("Body", GreenMailUtil.getBody(only).trim());
		assertNoClearTextOriginals(GreenMailUtil.getWholeMessage(only));
	}

	@Test
	void redirectFooterCarriesOnlyMaskedRecipients() throws Exception {
		new MultiMailerServiceService(properties(true, true)).sendMail(message("primary@example.org", false));

		String whole = GreenMailUtil.getWholeMessage(primary.getReceivedMessages()[0]);
		assertTrue(whole.contains("***"), "masked footer expected");
		assertNoClearTextOriginals(whole);
	}

	private static void assertNoClearTextOriginals(String whole) {
		for (String original : List.of("alice@gmail.com", "bob@athenarc.gr", "carol@mail.example.org")) {
			assertFalse(whole.contains(original), "original recipient leaked in clear text");
		}
	}

	private static MailerProperties singleMailer(MailerProperties.Config config) {
		MailerProperties properties = new MailerProperties();
		properties.getMailer().put("only", config);
		return properties;
	}

	private static MailerProperties.Config authenticatedMailer(int port, String password, boolean ssl) {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", "127.0.0.1");
		props.setProperty("mail.smtp.port", String.valueOf(port));
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.from", "sender@example.org");
		if (ssl) {
			props.setProperty("mail.smtp.ssl.enable", "true");
			props.setProperty("mail.smtp.ssl.trust", "*");
			props.setProperty("mail.smtp.ssl.checkserveridentity", "false");
		}
		MailerProperties.Config config = new MailerProperties.Config();
		config.setUsername("sender");
		config.setPassword(password);
		config.setProps(props);
		return config;
	}

	@Test
	void authenticatesWithConfiguredCredentials() {
		new MultiMailerServiceService(singleMailer(authenticatedMailer(authenticated.getSmtp().getPort(), "secret", false)))
				.sendMail(message("sender@example.org", false));

		assertEquals(3, authenticated.getReceivedMessages().length);
	}

	@Test
	void rejectsWrongPassword() {
		new MultiMailerServiceService(singleMailer(authenticatedMailer(authenticated.getSmtp().getPort(), "wrong", false)))
				.sendMail(message("sender@example.org", false));

		assertEquals(0, authenticated.getReceivedMessages().length);
	}

	@Test
	void deliversOverImplicitTlsWithAuth() throws Exception {
		new MultiMailerServiceService(singleMailer(authenticatedMailer(tls.getSmtps().getPort(), "secret", true)))
				.sendMail(message("sender@example.org", false));

		MimeMessage[] received = tls.getReceivedMessages();
		assertEquals(3, received.length);
		assertEquals("Hello", received[0].getSubject());
	}
}
