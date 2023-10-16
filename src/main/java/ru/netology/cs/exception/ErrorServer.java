package ru.netology.cs.exception;

public class ErrorServer extends RuntimeException {

    public ErrorServer(String message) {
        super(message);
    }
}
