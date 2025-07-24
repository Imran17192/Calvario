package de.uniks.stp24.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.exception.ErrorResponseException;
import de.uniks.stp24.model.ErrorResponse;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Objects;

@Singleton
public class ErrorService {
    @Inject
    ObjectMapper objectMapper;
    @Inject
    ErrorService() {

    }

    public int getStatus(Throwable ex){
        return switch (ex) {
            case HttpException httpEx -> httpEx.code();
            case ErrorResponseException errorResponseException -> errorResponseException.getResponse().statusCode();
            default -> -1;
        };
    }

    public String getMessage(Throwable ex) {
        return switch(ex) {
            case HttpException httpEx -> String.join("\n", getErrorResponse(httpEx).message());
            case ErrorResponseException errorResponseException -> errorResponseException.getMessage();
            default -> "not httpEx nor errorResponseEx but \n" + ex.getMessage();
        };
    }

    private ErrorResponse getErrorResponse(HttpException httpEx) {
        try (ResponseBody body = Objects.requireNonNull(httpEx.response()).errorBody()) {
            assert body != null;
            return objectMapper.readValue(body.string(), ErrorResponse.class);
        } catch (IOException e) {
            throw  new RuntimeException(e);
        }
    }
}
