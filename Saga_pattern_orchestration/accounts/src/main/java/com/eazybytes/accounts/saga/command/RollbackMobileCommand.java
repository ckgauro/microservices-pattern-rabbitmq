package com.eazybytes.accounts.saga.command;

import com.eazybytes.common.dto.MobileNumberUpdateDto;

public record RollbackMobileCommand(String sagaId, MobileNumberUpdateDto dto) implements MobileCommand {}