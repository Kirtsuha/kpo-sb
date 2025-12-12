package org.gateway.dto;

public class SubmissionFullResponse {
    public Object storage;
    public Object analysis;

    public SubmissionFullResponse(Object storage, Object analysis) {
        this.storage = storage;
        this.analysis = analysis;
    }
}
