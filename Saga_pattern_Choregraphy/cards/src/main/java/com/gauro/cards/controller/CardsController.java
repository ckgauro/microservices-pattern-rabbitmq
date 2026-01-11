package com.gauro.cards.controller;

import com.gauro.cards.constants.CardsConstants;
import com.gauro.cards.dto.CardsDto;
import com.gauro.cards.service.ICardsService;
import com.gauro.commondto.ResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chadnra
 */
@RestController
@Validated
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CardsController {

    private final ICardsService iCardsService;

    public CardsController(ICardsService iCardsService) {
        this.iCardsService = iCardsService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCard(@Valid @RequestParam("mobileNumber")
                                                  @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                  String mobileNumber) {
        iCardsService.createCard(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CardsConstants.STATUS_201, CardsConstants.MESSAGE_201));

    }

    @GetMapping("/fetch")
    public ResponseEntity<CardsDto> fetchCardDetails(@RequestParam("mobileNumber")
                                                     @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                     String mobileNUmber) {
        CardsDto cardsDto = iCardsService.fetchCard(mobileNUmber);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardsDto);

    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCardDetails(@Valid @RequestBody CardsDto cardsDto) {
        boolean isUpdated = iCardsService.updateCard(cardsDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(CardsConstants.STATUS_500, CardsConstants.MESSAGE_500_UPDATE));
        }
    }

    @PatchMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCardDetails(@RequestParam("cardNumber") Long cardNumber) {
        boolean isDeleted = iCardsService.deleteCard(cardNumber);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(CardsConstants.STATUS_500, CardsConstants.MESSAGE_500_DELETE));
        }
    }


}