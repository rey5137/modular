package com.rey.modular.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.modular.common.exception.ApiErrorException;
import com.rey.modular.common.response.GeneralResponse;
import com.rey.modular.user.UserApi;
import com.rey.modular.user.controller.response.UserResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@Slf4j
public class UserExternalModuleApi implements UserApi {

    private final String host;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public UserExternalModuleApi(ObjectMapper objectMapper, @Value("${user.module.host}") String host) {
        this.host = host;
        this.objectMapper = objectMapper;
        restClient =  RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(host)
                .build();
    }

    @SneakyThrows
    @Override
    public GeneralResponse<List<UserResponse>> getUsers(List<Integer> userIds, List<String> responseFields) {
        log.info("Calling to [{}] to get users by [{}] id", host, userIds);
        return restClient.get()
                .uri(uriBuilder -> UriComponentsBuilder.fromUriString(host)
                        .path("/internal/users")
                        .queryParam("ids", userIds)
                        .queryParam("response_fields", responseFields)
                        .build(false)
                        .toUri()
                ).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new ApiErrorException(response.getStatusCode(), objectMapper.readValue(response.getBody(), GeneralResponse.class));
                })
                .body(new ParameterizedTypeReference<>() {});
    }

}
