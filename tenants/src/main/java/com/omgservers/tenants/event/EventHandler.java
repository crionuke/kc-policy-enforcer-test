package com.omgservers.tenants.event;

public interface EventHandler {

    EventQualifier getQualifier();

    void handle(Long resourceId);
}
