package com.eazybytes.accounts.saga.event;

import com.eazybytes.customer.saga.event.MobileEvent;
import com.eazybytes.customer.saga.service.MobileSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class SagaEventConsumers {

    private final MobileSagaOrchestrator orchestrator;

    @Bean
    public Consumer<MobileEvent> accountsEvents() {
        return orchestrator::onEvent;
    }

    @Bean
    public Consumer<MobileEvent> cardsEvents() {
        return orchestrator::onEvent;
    }

    @Bean
    public Consumer<MobileEvent> loansEvents() {
        return orchestrator::onEvent;
    }
}