package com.rey.modular.common.exception;

import com.rey.modular.common.response.GeneralResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
public class ApiErrorException extends RuntimeException {

    @Getter
    private final HttpStatusCode httpStatusCode;
    private final GeneralResponse generalResponse;

    public <T> GeneralResponse<T> getGeneralResponse() {
        return generalResponse;
    }
}
