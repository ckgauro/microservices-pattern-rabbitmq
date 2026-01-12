package com.gauro.accounts.services;

import com.gauro.accounts.dto.AccountsDto;

public interface IAccountsService {

    /**
     *
     * @param mobileNumber - Input Mobile Number
     */
    void createAccount(String mobileNumber);
    /**
     *
     * @param mobileNumber - Input Mobile Number
     * @return Accounts Details based on a given mobileNumber
     */
    AccountsDto fetchAccount(String mobileNumber);
    /**
     *
     * @param accountsDto - AccountsDto Object
     * @return boolean indicating if the update of Account details is successful or not
     */
    boolean updateAccount(AccountsDto accountsDto);
    /**
     *
     * @param accountNumber - Input Account Number
     * @return boolean indicating if the delete of Account details is successful or not
     */
    boolean deleteAccount(Long accountNumber);
}