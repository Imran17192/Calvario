package de.uniks.stp24.exception;

import de.uniks.stp24.model.ErrorResponse;

public class ErrorResponseException extends Exception {

    private final ErrorResponse response;

    public ErrorResponseException(ErrorResponse response) {
        super(String.join("\n", response.message()));
        this.response = response;
    }

    public ErrorResponse getResponse(){
        return response;
    }
}
