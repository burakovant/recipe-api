package com.crediteuropebank.recipeapi.util;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
class RecipeAPIExceptionAdvice {

    private static final String STATUS_STR = "status";

    @ResponseBody
    @ExceptionHandler(RecipeAPIException.class)
    ResponseEntity<Object> recipeAPIExceptionHandler(RecipeAPIException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        String errMsg = ex.getMessage();
        // Get all errors
        List<String> errors = Collections.singletonList(errMsg);

        body.put("errors", errors);

        switch (errMsg) {
            case Constants.NOTFOUND_ERROR_MESSAGE:
                body.put(STATUS_STR, HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
            case Constants.USER_FORBIDDEN_MESSAGE:
                body.put(STATUS_STR, HttpStatus.FORBIDDEN.value());
                return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
            default:
                body.put(STATUS_STR, HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handle(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS_STR, HttpStatus.BAD_REQUEST.value());

        // Get all errors
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
