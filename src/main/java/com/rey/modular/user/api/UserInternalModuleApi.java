package com.rey.modular.user.api;

import com.rey.modular.common.exception.ApiErrorException;
import com.rey.modular.common.response.GeneralResponse;
import com.rey.modular.user.UserApi;
import com.rey.modular.user.controller.UserController;
import com.rey.modular.user.controller.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
@Primary
public class UserInternalModuleApi implements UserApi {

    private final UserController userController;

    @SneakyThrows
    @Override
    public GeneralResponse<List<UserResponse>> getUsers(List<Integer> userIds, List<String> responseFields) {
        var responseEntity = userController.getUsers(userIds, responseFields);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            return responseEntity.getBody();
        }
        throw new ApiErrorException(responseEntity.getStatusCode(), responseEntity.getBody());
    }

}
