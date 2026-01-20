package com.eazybytes.customer.saga.repository;

import com.eazybytes.common.dto.MobileNumberUpdateDto;
import com.eazybytes.customer.saga.entity.MobileSagaStateEntity;
import com.eazybytes.customer.saga.state.MobileSagaState;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SagaRepository {

    private final MobileSagaJpaRepository jpa;

    @Transactional
    public MobileSagaStateEntity create(String sagaId, MobileNumberUpdateDto dto, MobileSagaState initialState) {
        MobileSagaStateEntity entity = new MobileSagaStateEntity();
        entity.setSagaId(sagaId);
        entity.setState(initialState);
        entity.setCurrentMobileNumber(dto.getCurrentMobileNumber());
        entity.setNewMobileNumber(dto.getNewMobileNumber());
        return jpa.save(entity);
    }

    @Transactional(readOnly = true)
    public MobileSagaStateEntity get(String sagaId) {
        return jpa.findById(sagaId)
                .orElseThrow(() -> new EntityNotFoundException("Saga not found: " + sagaId));
    }

    @Transactional
    public void updateState(String sagaId, MobileSagaState state) {
        MobileSagaStateEntity entity = get(sagaId);
        entity.setState(state);
        jpa.save(entity);
    }

    @Transactional
    public void setError(String sagaId, String error) {
        MobileSagaStateEntity entity = get(sagaId);
        entity.setError(error);
        jpa.save(entity);
    }

    @Transactional
    public void addUpdatedStep(String sagaId, String serviceName) {
        MobileSagaStateEntity entity = get(sagaId);
        entity.getUpdatedSteps().add(serviceName);
        jpa.save(entity);
    }
}