package com.eazybytes.accounts.saga.event;

import com.eazybytes.customer.saga.event.MobileRollbackFailedEvent;
import com.eazybytes.customer.saga.event.MobileRolledBackEvent;
import com.eazybytes.customer.saga.event.MobileUpdateFailedEvent;
import com.eazybytes.customer.saga.event.MobileUpdatedEvent;

public sealed interface MobileEvent permits MobileUpdatedEvent, MobileUpdateFailedEvent, MobileRolledBackEvent, MobileRollbackFailedEvent {
  String sagaId();
  String service();     // "ACCOUNTS", "CARDS", "LOANS"
  String currentMobile();
  String newMobile();
}