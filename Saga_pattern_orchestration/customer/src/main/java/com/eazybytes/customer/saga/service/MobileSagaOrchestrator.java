package com.eazybytes.customer.saga.service;

import com.eazybytes.common.dto.MobileNumberUpdateDto;
import com.eazybytes.customer.saga.command.RollbackMobileCommand;
import com.eazybytes.customer.saga.command.UpdateMobileCommand;
import com.eazybytes.customer.saga.entity.MobileSagaStateEntity;
import com.eazybytes.customer.saga.event.*;
import com.eazybytes.customer.saga.repository.SagaRepository;
import com.eazybytes.customer.saga.state.MobileSagaState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MobileSagaOrchestrator {

  private final SagaRepository sagaRepo;
  private final SagaCommandPublisher publisher;

  public String start(MobileNumberUpdateDto dto) {
    String sagaId = UUID.randomUUID().toString();

    sagaRepo.create(sagaId, dto, MobileSagaState.STARTED);

    // Step 1: Accounts
    publisher.sendAccounts(new UpdateMobileCommand(sagaId, dto));
    sagaRepo.updateState(sagaId, MobileSagaState.ACCOUNTS_UPDATE_SENT);

    return sagaId;
  }

  public void onEvent(MobileEvent event) {
    var saga = sagaRepo.get(event.sagaId());

    if (event instanceof MobileUpdatedEvent ok) {
      onUpdateSuccess(saga, ok);
      return;
    }

    if (event instanceof MobileUpdateFailedEvent fail) {
      sagaRepo.setError(saga.getSagaId(), fail.reason());
      onUpdateFailure(saga, fail);
      return;
    }

    if (event instanceof MobileRolledBackEvent) {
      // Optional: track rollback completion per service
      return;
    }

    if (event instanceof MobileRollbackFailedEvent rbFail) {
      sagaRepo.setError(saga.getSagaId(), rbFail.reason());
      sagaRepo.updateState(saga.getSagaId(), MobileSagaState.FAILED_ROLLING_BACK);
    }
  }

  private void onUpdateSuccess(MobileSagaStateEntity saga, MobileUpdatedEvent ok) {
    sagaRepo.addUpdatedStep(saga.getSagaId(), ok.service());

    switch (ok.service()) {
      case "ACCOUNTS" -> {
        sagaRepo.updateState(saga.getSagaId(), MobileSagaState.ACCOUNTS_UPDATED);
        publisher.sendCards(new UpdateMobileCommand(saga.getSagaId(), dtoFromSaga(saga)));
        sagaRepo.updateState(saga.getSagaId(), MobileSagaState.CARDS_UPDATE_SENT);
      }
      case "CARDS" -> {
        sagaRepo.updateState(saga.getSagaId(), MobileSagaState.CARDS_UPDATED);
        publisher.sendLoans(new UpdateMobileCommand(saga.getSagaId(), dtoFromSaga(saga)));
        sagaRepo.updateState(saga.getSagaId(), MobileSagaState.LOANS_UPDATE_SENT);
      }
      case "LOANS" -> {
        sagaRepo.updateState(saga.getSagaId(), MobileSagaState.COMPLETED);
      }
    }
  }

  private void onUpdateFailure(MobileSagaStateEntity saga, MobileUpdateFailedEvent fail) {
    // Rollback only services that already succeeded
    Set<String> updated = saga.getUpdatedSteps();
    var dto = dtoFromSaga(saga);

    // Reverse order: LOANS -> CARDS -> ACCOUNTS
    if (updated.contains("LOANS")) {
      publisher.sendLoans(new RollbackMobileCommand(saga.getSagaId(), dto));
    }
    if (updated.contains("CARDS")) {
      publisher.sendCards(new RollbackMobileCommand(saga.getSagaId(), dto));
    }
    if (updated.contains("ACCOUNTS")) {
      publisher.sendAccounts(new RollbackMobileCommand(saga.getSagaId(), dto));
    }

    sagaRepo.updateState(saga.getSagaId(), MobileSagaState.ROLLED_BACK);
  }

  private MobileNumberUpdateDto dtoFromSaga(MobileSagaStateEntity saga) {
    return new MobileNumberUpdateDto(saga.getCurrentMobileNumber(), saga.getNewMobileNumber());
  }
}