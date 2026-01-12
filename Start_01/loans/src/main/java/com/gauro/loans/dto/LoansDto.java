package com.gauro.loans.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LoansDto {


    private Long loanNumber;

    @NotEmpty(message = "Mobile Number can not be a null or empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile Number must be 10 digits")
    private String mobileNumber;

    @NotEmpty(message = "LoanType can not be a null or empty")
    private String loanType;

    @Positive(message = "LoanType can not be a null or empty")
    private int totalLoan;

    @Positive(message = "Total loan amount should be greater than zero")
    private int amountPaid;

    @PositiveOrZero(message = "Total loan amount should be equal to greater than zero")
    private int outstandingAmount;


    private boolean activeSw;

}