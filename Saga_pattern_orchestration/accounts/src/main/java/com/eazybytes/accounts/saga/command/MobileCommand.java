package com.eazybytes.accounts.saga.command;

import com.eazybytes.common.dto.MobileNumberUpdateDto;

public sealed interface MobileCommand permits UpdateMobileCommand, RollbackMobileCommand {
  String sagaId();
  MobileNumberUpdateDto dto();
}