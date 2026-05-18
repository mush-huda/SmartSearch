package com.huda.exception;

import org.springframework.http.HttpStatusCode;

public class SearchAgentClientException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public SearchAgentClientException(HttpStatusCode statusCode) {
        super("search-agent-service rejected request: " + statusCode);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
