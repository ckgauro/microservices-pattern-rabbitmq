package com.gauro.accounts.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.gauro.accounts.constants.AccountsConstants;
import com.gauro.accounts.dto.AccountsDto;
import com.gauro.accounts.services.IAccountsService;
import com.gauro.commondto.ResponseDto;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chandra
 */

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class AccountsController {
    private final IAccountsService iAccountsService;

    public AccountsController(IAccountsService iAccountsService) {
        this.iAccountsService = iAccountsService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAmount(@RequestParam("mobileNumber")
                                                    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                        String mobileNumber) {

        iAccountsService.createAccount(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<AccountsDto> fetchAccountDetails(@RequestParam("mobileNumber")
                                                           @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        AccountsDto accountsDto = iAccountsService.fetchAccount(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody AccountsDto accountsDto) {
        boolean isUpdated = iAccountsService.updateAccount(accountsDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(AccountsConstants.STATUS_500, AccountsConstants.MESSAGE_500_UPDATE));
        }

    }

    @PatchMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam("accountNumber") Long accountNUmber){
        boolean isDeleted= iAccountsService.deleteAccount(accountNUmber);
        if(isDeleted){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(AccountsConstants.STATUS_500, AccountsConstants.MESSAGE_500_DELETE));
        }
    }


}