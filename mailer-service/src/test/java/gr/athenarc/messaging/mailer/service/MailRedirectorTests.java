/*
 * Copyright 2023-2026 OpenAIRE AMKE & Athena Research and Innovation Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.athenarc.messaging.mailer.service;

import gr.athenarc.messaging.mailer.config.MailerProperties;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MailRedirectorTests {

	private static MailerProperties.Redirect redirect(boolean include, String... to) {
		MailerProperties.Redirect redirect = new MailerProperties.Redirect();
		redirect.setEnabled(true);
		redirect.setTo(List.of(to));
		redirect.setIncludeOriginalRecipientsMasked(include);
		return redirect;
	}

	private static EmailMessage original(boolean html) {
		EmailMessage message = new EmailMessage();
		message.setFrom("noreply@example.org");
		message.setTo(List.of("alice@gmail.com"));
		message.setCc(List.of("bob@athenarc.gr"));
		message.setBcc(List.of("carol@mail.example.org"));
		message.setSubject("Hello");
		message.setText("Body");
		message.setHtml(html);
		return message;
	}

	@Test
	void replacesRecipientsAndLeavesOriginalUntouched() {
		EmailMessage original = original(false);
		EmailMessage copy = new MailRedirector(redirect(false, "dev@example.org")).redirect(original);

		assertEquals(List.of("dev@example.org"), copy.getTo());
		assertTrue(copy.getCc().isEmpty());
		assertTrue(copy.getBcc().isEmpty());
		assertEquals("[REDIRECTED] Hello", copy.getSubject());
		assertEquals("Body", copy.getText());
		assertEquals("noreply@example.org", copy.getFrom());
		assertEquals(List.of("alice@gmail.com"), original.getTo());
		assertEquals("Hello", original.getSubject());
	}

	@Test
	void footerIsMaskedAndOptIn() {
		EmailMessage copy = new MailRedirector(redirect(true, "dev@example.org")).redirect(original(false));

		assertTrue(copy.getText().contains("To (1): a***@g***.com"));
		assertTrue(copy.getText().contains("Cc (1): b***@a***.gr"));
		assertTrue(copy.getText().contains("Bcc (1): c***@m***.org"));
		assertFalse(copy.getText().contains("alice"));
		assertFalse(copy.getText().contains("athenarc"));
	}

	@Test
	void htmlFooterUsesMarkup() {
		EmailMessage copy = new MailRedirector(redirect(true, "dev@example.org")).redirect(original(true));

		assertTrue(copy.isHtml());
		assertTrue(copy.getText().contains("<br/>"));
		assertFalse(copy.getText().contains("alice"));
	}

	@Test
	void masksShortParts() {
		assertEquals("***@***.org", MailRedirector.mask("a@b.org"));
		assertEquals("***", MailRedirector.mask("invalid"));
	}

	@Test
	void failsWithoutValidTargets() {
		assertThrows(IllegalStateException.class, () -> new MailRedirector(redirect(false)));
		assertThrows(IllegalStateException.class, () -> new MailRedirector(redirect(false, "not-an-address")));
	}
}
