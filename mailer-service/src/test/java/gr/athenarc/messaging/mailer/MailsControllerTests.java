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
package gr.athenarc.messaging.mailer;

import gr.athenarc.messaging.mailer.config.MailerProperties;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import gr.athenarc.messaging.mailer.exception.InvalidEmailException;
import gr.athenarc.messaging.mailer.exception.MailDeliveryException;
import gr.athenarc.messaging.mailer.service.MailerService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MailsControllerTests {

	private static final String BODY = "{\"from\":\"sender@example.org\",\"to\":[\"alice@example.org\"],\"text\":\"Body\"}";

	@Nested
	@WebMvcTest(MailsController.class)
	@EnableConfigurationProperties(MailerProperties.class)
	class WithoutApiKey {

		@Autowired
		MockMvc mvc;

		@MockitoBean
		MailerService mailerService;

		@Test
		void acceptedMessageReturns201() throws Exception {
			mvc.perform(post(RelativePaths.MAILS).contentType(MediaType.APPLICATION_JSON).content(BODY))
					.andExpect(status().isCreated());
		}

		@Test
		void invalidMessageReturns400() throws Exception {
			doThrow(new InvalidEmailException("'from' is required")).when(mailerService).sendMail(any(EmailMessage.class));

			mvc.perform(post(RelativePaths.MAILS).contentType(MediaType.APPLICATION_JSON).content(BODY))
					.andExpect(status().isBadRequest());
		}

		@Test
		void deliveryFailureReturns502() throws Exception {
			doThrow(new MailDeliveryException("rejected", null)).when(mailerService).sendMail(any(EmailMessage.class));

			mvc.perform(post(RelativePaths.MAILS).contentType(MediaType.APPLICATION_JSON).content(BODY))
					.andExpect(status().isBadGateway());
		}

		@Test
		void nonJsonReturns415() throws Exception {
			mvc.perform(post(RelativePaths.MAILS).contentType(MediaType.TEXT_PLAIN).content("hello"))
					.andExpect(status().isUnsupportedMediaType());
		}
	}

	@Nested
	@WebMvcTest(MailsController.class)
	@EnableConfigurationProperties(MailerProperties.class)
	@TestPropertySource(properties = "mail.api-key=test-key")
	class WithApiKey {

		@Autowired
		MockMvc mvc;

		@MockitoBean
		MailerService mailerService;

		@Test
		void missingKeyReturns401() throws Exception {
			mvc.perform(post(RelativePaths.MAILS).contentType(MediaType.APPLICATION_JSON).content(BODY))
					.andExpect(status().isUnauthorized());
		}

		@Test
		void wrongKeyReturns401() throws Exception {
			mvc.perform(post(RelativePaths.MAILS).contentType(MediaType.APPLICATION_JSON).content(BODY)
							.header(MailerHeaders.API_KEY, "wrong"))
					.andExpect(status().isUnauthorized());
		}

		@Test
		void matchingKeyReturns201() throws Exception {
			mvc.perform(post(RelativePaths.MAILS).contentType(MediaType.APPLICATION_JSON).content(BODY)
							.header(MailerHeaders.API_KEY, "test-key"))
					.andExpect(status().isCreated());
		}
	}
}
