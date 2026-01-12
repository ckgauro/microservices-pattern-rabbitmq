package com.gauro.gateway.handler;

import com.gauro.gateway.dto.*;
import com.gauro.gateway.service.client.CustomerSummaryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerCompositeHandler {

    private final CustomerSummaryClient customerSummaryClient;
//
//    public Mono<ServerResponse> fetchCustomerSummary(ServerRequest serverRequest) {
//        String mobileNumber = serverRequest.queryParam("mobileNumber").get();
//
//        Mono<ResponseEntity<CustomerDto>> customerDetails = customerSummaryClient.fetchCustomerDetails(mobileNumber);
//        Mono<ResponseEntity<AccountsDto>> accountsDetails = customerSummaryClient.fetchAccountDetails(mobileNumber);
//        Mono<ResponseEntity<LoansDto>> loanDetails = customerSummaryClient.fetchLoanDetails(mobileNumber);
//        Mono<ResponseEntity<CardsDto>> cardDetails = customerSummaryClient.fetchCardDetails(mobileNumber);
//
//        return Mono.zip(customerDetails, accountsDetails, loanDetails, cardDetails)
//                .flatMap(tuple -> {
//                    CustomerDto customerDto = tuple.getT1().getBody();
//                    AccountsDto accountsDto = tuple.getT2().getBody();
//                    LoansDto loansDto = tuple.getT3().getBody();
//                    CardsDto cardsDto = tuple.getT4().getBody();
//                    CustomerSummaryDto customerSummaryDto = new CustomerSummaryDto(customerDto, accountsDto, loansDto, cardsDto);
//                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                            .body(BodyInserters.fromValue(customerSummaryDto));
//
//                });
//
//
//    }
//

    public Mono<ServerResponse> fetchCustomerSummary(ServerRequest serverRequest) {

        String mobileNumber = serverRequest.queryParam("mobileNumber")
                .orElseThrow(() -> new IllegalArgumentException("mobileNumber is required"));

        Mono<CustomerDto> customerMono =
                customerSummaryClient.fetchCustomerDetails(mobileNumber)
                        .mapNotNull(ResponseEntity::getBody)
                        .onErrorResume(WebClientResponseException.NotFound.class, ex ->
                                Mono.error(new RuntimeException("Customer not found for mobileNumber=" + mobileNumber)));

        Mono<AccountsDto> accountsMono =
                customerSummaryClient.fetchAccountDetails(mobileNumber)
                        .mapNotNull(ResponseEntity::getBody)
                        .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.just(new AccountsDto()));

        Mono<LoansDto> loansMono =
                customerSummaryClient.fetchLoanDetails(mobileNumber)
                        .mapNotNull(ResponseEntity::getBody)
                        .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.just(new LoansDto()));

        Mono<CardsDto> cardsMono =
                customerSummaryClient.fetchCardDetails(mobileNumber)
                        .mapNotNull(ResponseEntity::getBody)
                        .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.just(new CardsDto()));

        return Mono.zip(customerMono, accountsMono, loansMono, cardsMono)
                .flatMap(t -> {
                    CustomerSummaryDto dto = new CustomerSummaryDto(t.getT1(), t.getT2(), t.getT3(), t.getT4());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(dto);
                })
                .onErrorResume(RuntimeException.class, ex -> {
                    // if we threw "Customer not found..."
                    if (ex.getMessage() != null && ex.getMessage().startsWith("Customer not found")) {
                        return ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorDto("NOT_FOUND", ex.getMessage()));
                    }
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorDto("INTERNAL_SERVER_ERROR", ex.getMessage()));
                });
    }



}