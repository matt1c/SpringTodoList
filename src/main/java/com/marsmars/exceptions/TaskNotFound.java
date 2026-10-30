package com.marsmars.exceptions;

public class TaskNotFound extends RuntimeException {
    public TaskNotFound(String message) {
        super(message);
    }
}
