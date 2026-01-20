package com.eazybytes.accounts.saga.command;

import com.eazybytes.common.dto.MobileNumberUpdateDto;

public record UpdateMobileCommand(String sagaId, MobileNumberUpdateDto dto) implements MobileCommand {}
