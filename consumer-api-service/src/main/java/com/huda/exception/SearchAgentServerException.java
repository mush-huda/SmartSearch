package com.huda.exception;

import org.springframework.http.HttpStatusCode;

public class SearchAgentServerException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public SearchAgentServerException(HttpStatusCode statusCode) {
        super("search-agent-service error: " + statusCode);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
