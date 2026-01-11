package com.gauro.accounts.bootstrap;

import com.gauro.accounts.dto.AccountsDto;
import com.gauro.accounts.entity.Accounts;
import com.gauro.accounts.services.IAccountsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class BootStrap implements CommandLineRunner {
    private final IAccountsService iAccountsService;

    @Override
    public void run(String... args) throws Exception {
        try {
            AccountsDto accountsdto = iAccountsService.fetchAccount("2546312425");

            log.info("Accountsdto--->{}", accountsdto);
        } catch (Exception e) {

        }


    }
}
