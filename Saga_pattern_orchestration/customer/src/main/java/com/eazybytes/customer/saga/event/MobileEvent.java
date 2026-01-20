package com.eazybytes.customer.saga.event;

public sealed interface MobileEvent permits MobileUpdatedEvent, MobileUpdateFailedEvent, MobileRolledBackEvent, MobileRollbackFailedEvent {
  String sagaId();
  String service();     // "ACCOUNTS", "CARDS", "LOANS"
  String currentMobile();
  String newMobile();
}