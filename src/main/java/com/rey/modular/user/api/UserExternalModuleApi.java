package com.rey.modular.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.modular.common.exception.ApiErrorException;
import com.rey.modular.common.response.GeneralResponse;
import com.rey.modular.user.UserApi;
import com.rey.modular.user.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserExternalModuleApi implements UserApi {

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final DiscoveryClient discoveryClient;

    public UserExternalModuleApi(ObjectMapper objectMapper, DiscoveryClient discoveryClient) {
        this.objectMapper = objectMapper;
        this.discoveryClient = discoveryClient;
        restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    @Override
    public GeneralResponse<List<UserResponse>> getUsers(List<Integer> userIds) {
        URI hostUri = getHostUri();
        log.info("Calling to [{}] to get users by [{}] id", hostUri, userIds);
        return restClient.get()
                .uri(uriBuilder -> UriComponentsBuilder.fromUri(hostUri)
                        .path("/user/internal/users")
                        .queryParam("ids", userIds)
                        .build(false)
                        .toUri()
                ).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    try {
                        throw new ApiErrorException(response.getStatusCode(), objectMapper.readValue(response.getBody(), GeneralResponse.class));
                    }
                    catch (Exception ex) {
                        throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, GeneralResponse.error("internal_error", ex.getMessage()));
                    }
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private URI getHostUri() {
        URI hostUri = Optional.ofNullable(discoveryClient.getInstances("user"))
                .flatMap(instances -> instances.stream().findFirst())
                .map(ServiceInstance::getUri)
                .orElse(null);
        if(hostUri == null){
            throw new ApiErrorException(HttpStatus.SERVICE_UNAVAILABLE, GeneralResponse.error("unavailable", "Service unavailable"));
        }
        return hostUri;
    }

}
