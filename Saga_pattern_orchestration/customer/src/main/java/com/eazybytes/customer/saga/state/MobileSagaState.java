package com.eazybytes.customer.saga.state;

public enum MobileSagaState {

    STARTED,

    ACCOUNTS_UPDATE_SENT,
    ACCOUNTS_UPDATED,

    CARDS_UPDATE_SENT,
    CARDS_UPDATED,

    LOANS_UPDATE_SENT,

    COMPLETED,

    FAILED_ROLLING_BACK,
    ROLLED_BACK
}

