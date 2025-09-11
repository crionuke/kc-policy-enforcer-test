package com.omgservers.omgservice.event;

public interface EventHandler {

    EventQualifier getQualifier();

    void handle(Long resourceId);
}
