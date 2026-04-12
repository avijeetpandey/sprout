package com.avijeet.sprout.exceptions;

public class UnableToCreateUserException extends RuntimeException {
    public UnableToCreateUserException(String message) {
        super(message);
    }
}
