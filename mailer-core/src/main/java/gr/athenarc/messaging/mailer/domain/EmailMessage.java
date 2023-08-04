package gr.athenarc.messaging.mailer.domain;

import java.util.List;

public class EmailMessage {

    String from;
    List<String> to;
    List<String> cc;
    List<String> bcc;
    String subject;
    String text;

    public EmailMessage() {
    }

    public EmailMessage(EmailBuilder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.cc = builder.cc;
        this.bcc = builder.bcc;
        this.subject = builder.subject;
        this.text = builder.text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "EmailMessage{" +
                "from='" + from + '\'' +
                ", to=" + to +
                ", cc=" + cc +
                ", bcc=" + bcc +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public static class EmailBuilder {
        String from;
        List<String> to;
        List<String> cc;
        List<String> bcc;
        String subject;
        String text;

        public EmailBuilder() {}

        public EmailBuilder(String from, List<String> to) {
            this.from = from;
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public EmailBuilder setFrom(String from) {
            this.from = from;
            return this;
        }

        public List<String> getTo() {
            return to;
        }

        public EmailBuilder setTo(List<String> to) {
            this.to = to;
            return this;
        }

        public List<String> getCc() {
            return cc;
        }

        public EmailBuilder setCc(List<String> cc) {
            this.cc = cc;
            return this;
        }

        public List<String> getBcc() {
            return bcc;
        }

        public EmailBuilder setBcc(List<String> bcc) {
            this.bcc = bcc;
            return this;
        }

        public String getSubject() {
            return subject;
        }

        public EmailBuilder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public String getText() {
            return text;
        }

        public EmailBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public EmailMessage build() {
            return new EmailMessage(this);
        }
    }
}
