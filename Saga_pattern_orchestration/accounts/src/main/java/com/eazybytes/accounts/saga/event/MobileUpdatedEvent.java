package com.eazybytes.accounts.saga.event;

public record MobileUpdatedEvent(String sagaId, String service, String currentMobile, String newMobile) implements MobileEvent {}
