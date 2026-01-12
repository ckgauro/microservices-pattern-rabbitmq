package com.gauro.cards.mapper;

import com.gauro.cards.dto.CardsDto;
import com.gauro.cards.entity.Cards;

public class CardsMapper {
    public static CardsDto mapToCardsDto(Cards cards, CardsDto cardsDto){
        cardsDto.setCardNumber(cards.getCardNumber());
        cardsDto.setCardType(cards.getCardType());
        cardsDto.setMobileNumber(cards.getMobileNumber());
        cardsDto.setTotalLimit(cards.getTotalLimit());
        cardsDto.setAvailableAmount(cards.getAvailableAmount());
        cardsDto.setActiveSw(cards.isActiveSw());
        return cardsDto;
    }

    public static Cards mapToCards(CardsDto cardsDto, Cards cards){
        cardsDto.setCardType(cards.getCardType());
        cards.setTotalLimit(cardsDto.getTotalLimit());
        cardsDto.setAvailableAmount(cardsDto.getAvailableAmount());
        cardsDto.setAmountUsed(cards.getAmountUsed());
        return cards;
    }
}