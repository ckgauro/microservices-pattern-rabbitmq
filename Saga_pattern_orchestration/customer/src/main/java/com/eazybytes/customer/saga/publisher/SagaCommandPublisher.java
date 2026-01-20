package com.eazybytes.customer.saga.publisher;

import com.eazybytes.customer.saga.command.MobileCommand;
import com.eazybytes.customer.saga.command.UpdateMobileCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaCommandPublisher {

    private final StreamBridge streamBridge;

    public void sendAccounts(MobileCommand cmd) {
        streamBridge.send("accountsCommands-out-0", msg(cmd, "ACCOUNTS"));
    }

    public void sendCards(MobileCommand cmd) {
        streamBridge.send("cardsCommands-out-0", msg(cmd, "CARDS"));
    }

    public void sendLoans(MobileCommand cmd) {
        streamBridge.send("loansCommands-out-0", msg(cmd, "LOANS"));
    }

    private Message<MobileCommand> msg(MobileCommand cmd, String step) {
        String type = (cmd instanceof UpdateMobileCommand) ? "UPDATE" : "ROLLBACK";
        return MessageBuilder.withPayload(cmd)
                .setHeader("sagaId", cmd.sagaId())
                .setHeader("step", step)
                .setHeader("commandType", type)
                .build();
    }
}