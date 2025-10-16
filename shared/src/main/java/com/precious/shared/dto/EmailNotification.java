package com.precious.shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class EmailNotification implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String to;
    private final String subject;
    private final String body;

//    public EmailNotification(String to, String subject, String body) {
//        this.to = Objects.requireNonNull(to);
//        this.subject = Objects.requireNonNull(subject);
//        this.body = Objects.requireNonNull(body);
//    }

    @JsonCreator
    public EmailNotification(@JsonProperty("to") String to, @JsonProperty("subject") String subject, @JsonProperty("body") String body) {
        this.to = Objects.requireNonNull(to);
        this.subject = Objects.requireNonNull(subject);
        this.body = Objects.requireNonNull(body);
    }

    public String getTo() { return to; }

    public String getSubject() { return subject; }

    public String getBody() { return body; }
}