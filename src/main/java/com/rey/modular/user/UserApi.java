package com.rey.modular.user;

import com.rey.modular.common.response.GeneralResponse;
import com.rey.modular.user.controller.response.UserResponse;

import java.util.List;

public interface UserApi {

    GeneralResponse<List<UserResponse>> getUsers(List<Integer> userIds);

}
