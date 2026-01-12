package com.gauro.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException  extends RuntimeException{
    public ResourceNotFoundException(String resouceName, String fieldName, String fieldValue ){
        super(String.format("%s not found the given input data %s : '%s' ", resouceName,fieldName,fieldValue));
    }
}