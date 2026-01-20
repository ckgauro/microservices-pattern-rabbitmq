package com.eazybytes.customer.saga.command;

import com.eazybytes.common.dto.MobileNumberUpdateDto;

public record UpdateMobileCommand(String sagaId, MobileNumberUpdateDto dto) implements MobileCommand {}
