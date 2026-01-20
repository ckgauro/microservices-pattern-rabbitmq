package com.eazybytes.accounts.saga.event;

public record MobileRollbackFailedEvent(String sagaId, String service, String currentMobile, String newMobile, String reason) implements MobileEvent {}