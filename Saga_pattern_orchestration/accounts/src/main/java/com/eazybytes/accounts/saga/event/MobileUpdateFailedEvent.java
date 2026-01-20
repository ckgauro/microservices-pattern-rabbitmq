package com.eazybytes.accounts.saga.event;

public record MobileUpdateFailedEvent(String sagaId, String service, String currentMobile, String newMobile, String reason) implements MobileEvent {}
