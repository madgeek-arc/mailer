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
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Rewrites outgoing messages so that they are delivered to a fixed list of addresses
 * instead of their original recipients. Original addresses are never logged and appear
 * in the message only, masked, when explicitly enabled.
 */
public class MailRedirector {

    static final String SUBJECT_PREFIX = "[REDIRECTED] ";

    private final boolean includeOriginalRecipientsMasked;
    private final List<String> targets;

    public MailRedirector(MailerProperties.Redirect redirect) {
        this.includeOriginalRecipientsMasked = redirect.isIncludeOriginalRecipientsMasked();
        this.targets = redirect.getTo() == null ? List.of() : redirect.getTo().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(MailRedirector::isValid)
                .toList();
        if (targets.isEmpty()) {
            throw new IllegalStateException("mail.redirect.enabled is true but mail.redirect.to has no valid addresses");
        }
    }

    public int targetCount() {
        return targets.size();
    }

    public EmailMessage redirect(EmailMessage original) {
        EmailMessage copy = new EmailMessage();
        copy.setFrom(original.getFrom());
        copy.setTo(new ArrayList<>(targets));
        copy.setCc(new ArrayList<>());
        copy.setBcc(new ArrayList<>());
        copy.setSubject(SUBJECT_PREFIX + (original.getSubject() != null ? original.getSubject() : "[No Subject]"));
        copy.setHtml(original.isHtml());

        String text = original.getText() != null ? original.getText() : "";
        copy.setText(includeOriginalRecipientsMasked ? text + footer(original) : text);
        return copy;
    }

    private String footer(EmailMessage original) {
        String summary = describe("To", original.getTo())
                + describe("Cc", original.getCc())
                + describe("Bcc", original.getBcc());
        if (original.isHtml()) {
            return "<hr/><p><small>Original recipients (masked):<br/>"
                    + summary.replace("\n", "<br/>") + "</small></p>";
        }
        return "\n\n--\nOriginal recipients (masked):\n" + summary;
    }

    private String describe(String label, List<String> addresses) {
        List<String> masked = addresses == null ? List.of() : addresses.stream()
                .filter(Objects::nonNull)
                .map(MailRedirector::mask)
                .toList();
        return label + " (" + masked.size() + "): " + String.join(", ", masked) + "\n";
    }

    /**
     * Masks an address as {@code s***@g***.com}: the first character of the local part and of
     * the domain are kept together with the top-level domain; the mask length is fixed.
     */
    static String mask(String address) {
        int at = address.lastIndexOf('@');
        if (at < 0) {
            return "***";
        }
        String local = address.substring(0, at);
        String domain = address.substring(at + 1);
        int dot = domain.lastIndexOf('.');
        String host = dot < 0 ? domain : domain.substring(0, dot);
        String tld = dot < 0 ? "" : domain.substring(dot);
        return maskPart(local) + "@" + maskPart(host) + tld;
    }

    private static String maskPart(String part) {
        return part.length() < 2 ? "***" : part.charAt(0) + "***";
    }

    private static boolean isValid(String address) {
        try {
            new InternetAddress(address).validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
}
