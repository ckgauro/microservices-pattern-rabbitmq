package com.gauro.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerSummaryDto {

    private CustomerDto customer;
    private AccountsDto accounts;
    private LoansDto loans;
    private CardsDto cards;
}