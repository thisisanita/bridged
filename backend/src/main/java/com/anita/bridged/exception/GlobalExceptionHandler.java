package com.anita.bridged.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleCustomerNotFound (CustomerNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Customer not found");
        problemDetail.setDetail(exception.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(ReferenceDataNotFoundException.class)
    public ProblemDetail handleReferenceDataNotFound (ReferenceDataNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Application reference data missing");
        problemDetail.setDetail(exception.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ProblemDetail handleChatNotFound (ChatNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Chat not found");
        problemDetail.setDetail(exception.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(InvalidTriageSelectionException.class)
    public ProblemDetail handleInvalidTriageSelection (InvalidTriageSelectionException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid triage selection");
        problemDetail.setDetail(exception.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(InvalidChatStateException.class)
    public ProblemDetail handleInvalidChatState (InvalidChatStateException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Invalid chat state");
        problemDetail.setDetail(exception.getMessage());

        return problemDetail;
    }

}
