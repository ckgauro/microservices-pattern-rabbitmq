package com.eazybytes.accounts.saga.event;

public record MobileRolledBackEvent(String sagaId, String service, String currentMobile, String newMobile) implements MobileEvent {}
