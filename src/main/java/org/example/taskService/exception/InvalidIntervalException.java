package org.example.taskService.exception;

public class InvalidIntervalException extends IllegalArgumentException {
    public InvalidIntervalException(String message) {
        super(message);
    }
}
