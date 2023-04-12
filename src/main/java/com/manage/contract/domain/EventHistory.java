package com.manage.contract.domain;

import java.time.Instant;

public class EventHistory {

    private String createdBy;

    private Instant createdOn;

    private String eventMessage;

    public EventHistory(String eventMessage, String createdBy, Instant createdOn){
        this.eventMessage = eventMessage;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        createdBy = createdBy;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        createdOn = createdOn;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }
}
