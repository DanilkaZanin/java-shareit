package ru.practicum.shareit.error.exception;

public class NotBookerException extends RuntimeException {
    public NotBookerException(String message) {
        super(message);
    }
}