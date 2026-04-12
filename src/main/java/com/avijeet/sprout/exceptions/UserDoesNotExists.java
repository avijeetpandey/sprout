package com.avijeet.sprout.exceptions;

public class UserDoesNotExists extends RuntimeException {
    public UserDoesNotExists(String message) {
        super(message);
    }
}
