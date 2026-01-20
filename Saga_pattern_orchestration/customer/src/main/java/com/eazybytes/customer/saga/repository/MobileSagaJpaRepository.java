package com.eazybytes.customer.saga.repository;

import com.eazybytes.customer.saga.entity.MobileSagaStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobileSagaJpaRepository extends JpaRepository<MobileSagaStateEntity, String> {}