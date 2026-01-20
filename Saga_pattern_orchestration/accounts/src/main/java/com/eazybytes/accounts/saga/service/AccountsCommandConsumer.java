package com.eazybytes.accounts.saga.service;

import com.eazybytes.accounts.saga.command.MobileCommand;
import com.eazybytes.accounts.saga.command.RollbackMobileCommand;
import com.eazybytes.accounts.saga.command.UpdateMobileCommand;
import com.eazybytes.accounts.saga.event.MobileRollbackFailedEvent;
import com.eazybytes.accounts.saga.event.MobileRolledBackEvent;
import com.eazybytes.accounts.saga.event.MobileUpdateFailedEvent;
import com.eazybytes.accounts.saga.event.MobileUpdatedEvent;
import com.eazybytes.accounts.service.IAccountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AccountsCommandConsumer {

    private final StreamBridge streamBridge;
    private final IAccountsService accountsService;

    @Bean
    public Consumer<MobileCommand> accountsCommands() {
        return cmd -> {
            var dto = cmd.dto();
            try {
                if (cmd instanceof UpdateMobileCommand u) {
                    boolean updated = accountsService.updateMobileNumber(dto);
                    if (!updated) throw new RuntimeException("Mobile not found in ACCOUNTS");
                    streamBridge.send("accountsEvents-out-0",
                            new MobileUpdatedEvent(u.sagaId(), "ACCOUNTS", dto.getCurrentMobileNumber(), dto.getNewMobileNumber()));
                } else if (cmd instanceof RollbackMobileCommand r) {
                    boolean rolled = accountsService.rollbackMobileNumber(dto);
                    if (!rolled) throw new RuntimeException("Rollback failed in ACCOUNTS");
                    streamBridge.send("accountsEvents-out-0",
                            new MobileRolledBackEvent(r.sagaId(), "ACCOUNTS", dto.getCurrentMobileNumber(), dto.getNewMobileNumber()));
                }
            } catch (Exception e) {
                if (cmd instanceof UpdateMobileCommand u) {
                    streamBridge.send("accountsEvents-out-0",
                            new MobileUpdateFailedEvent(u.sagaId(), "ACCOUNTS", dto.getCurrentMobileNumber(), dto.getNewMobileNumber(), e.getMessage()));
                } else if (cmd instanceof RollbackMobileCommand r) {
                    streamBridge.send("accountsEvents-out-0",
                            new MobileRollbackFailedEvent(r.sagaId(), "ACCOUNTS", dto.getCurrentMobileNumber(), dto.getNewMobileNumber(), e.getMessage()));
                }
            }
        };
    }
}