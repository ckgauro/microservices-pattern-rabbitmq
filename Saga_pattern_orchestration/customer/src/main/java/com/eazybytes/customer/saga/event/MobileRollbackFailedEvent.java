package com.eazybytes.customer.saga.event;

public record MobileRollbackFailedEvent(String sagaId, String service, String currentMobile, String newMobile, String reason) implements MobileEvent {}