package com.gauro.accounts.services.impl;

import com.gauro.accounts.constants.AccountsConstants;
import com.gauro.accounts.dto.AccountsDto;
import com.gauro.accounts.entity.Accounts;
import com.gauro.accounts.exception.AccountAlreadyExistsException;
import com.gauro.accounts.exception.ResourceNotFoundException;
import com.gauro.accounts.mapper.AccountsMapper;
import com.gauro.accounts.repository.AccountsRepository;
import com.gauro.accounts.services.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {
    private AccountsRepository accountsRepository;

    /**
     *
     * @param mobileNumber - Input Mobile Number
     */
    @Override
    public void createAccount(String mobileNumber) {
        Optional<Accounts> optionalAccounts=accountsRepository.findByMobileNumberAndActiveSw(mobileNumber,
                AccountsConstants.ACTIVE_SW);
        if(optionalAccounts.isPresent()){
            throw new AccountAlreadyExistsException("Account already registered with given mobile number"+mobileNumber);
        }
        accountsRepository.save(createNewAccount(mobileNumber));
    }

    /**
     * @param mobileNumber - String
     * @return the new account details
     */
    private Accounts createNewAccount(String mobileNumber) {
        Accounts newAccount = new Accounts();
        newAccount.setMobileNumber(mobileNumber);
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        newAccount.setActiveSw(AccountsConstants.ACTIVE_SW);
        return newAccount;
    }

    /**
     *
     * @param mobileNumber - Input Mobile Number
     * @return Accounts Details based on a given mobileNumber
     */

    @Override
    public AccountsDto fetchAccount(String mobileNumber) {
        Accounts accounts=accountsRepository.findByMobileNumberAndActiveSw(mobileNumber,AccountsConstants.ACTIVE_SW)
                .orElseThrow(()->new ResourceNotFoundException("Account","mobileNumber",mobileNumber)
                );
        AccountsDto accountsDto= AccountsMapper.mapToAccountsDto(accounts,new AccountsDto());
        return accountsDto;
    }

    /**
     * @param accountsDto - AccountsDto Object
     * @return boolean indicating if the update of Account details is successful or not
     */

    @Override
    public boolean updateAccount(AccountsDto accountsDto) {
        Accounts accounts=accountsRepository.findByMobileNumberAndActiveSw(accountsDto.getMobileNumber(), AccountsConstants.ACTIVE_SW)
                .orElseThrow(()->new ResourceNotFoundException("Account","mobileNumber",accountsDto.getMobileNumber()));
        AccountsMapper.maptToAccounts(accountsDto,accounts);
        accountsRepository.save(accounts);
        return true;
    }

    /**
     * @param accountNumber - Input Account Number
     * @return boolean indicating if the delete of Account details is successful or not
     */
    @Override
    public boolean deleteAccount(Long accountNumber) {
        Accounts accounts=accountsRepository.findById(accountNumber)
                .orElseThrow(()->new ResourceNotFoundException("Account", "accountNumber", accountNumber.toString()));
        accounts.setActiveSw(AccountsConstants.IN_ACTIVE_SW);
        accountsRepository.save(accounts);
        return true;
    }
}