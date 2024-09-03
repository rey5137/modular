package com.rey.modular.user.controller;

import com.rey.modular.common.response.DataPageResponse;
import com.rey.modular.common.response.GeneralResponse;
import com.rey.modular.common.response.PageInfo;
import com.rey.modular.user.controller.request.UserRequest;
import com.rey.modular.user.controller.request.UserSearchRequest;
import com.rey.modular.user.controller.response.UserIdResponse;
import com.rey.modular.user.controller.response.UserResponse;
import com.rey.modular.user.service.UserService;
import com.rey.modular.user.service.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("${user.module.context-path}")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/users",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GeneralResponse<UserIdResponse>> createUser(@RequestBody UserRequest request) {
        log.info("--- Start to create user ---");
        var userEntity = userService.createUser(request);
        var userIdResponse = new UserIdResponse(userEntity.getId());
        var generalResponse = GeneralResponse.success(userIdResponse);
        log.info("--- End to create user ---");
        return new ResponseEntity<>(generalResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/internal/users", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GeneralResponse<List<UserResponse>>> getUsers(@RequestParam("ids") List<Integer> userIds) {
        log.info("--- Start to get users ---");
        var userEntities = userService.getUserByIds(userIds);
        if(userEntities.size() != userIds.size()) {
            GeneralResponse<List<UserResponse>> generalResponse = GeneralResponse.error("not_found", "Not found user");
            log.info("--- End to get users ---");
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
        else {
            var userResponses = userEntities.stream()
                    .map(entity -> new UserResponse(entity.getId(), entity.getName(), entity.getEmail(), null))
                    .toList();
            var generalResponse = GeneralResponse.success(userResponses);
            log.info("--- End to get users ---");
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/users/search",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GeneralResponse<DataPageResponse<UserResponse>>> searchUser(@RequestBody UserSearchRequest request) {
        log.info("--- Start to search user ---");
        var userPage = userService.searchUsers(request, List.of(User.ID, User.NAME, User.ROLE_TABLE_ID, User.ROLE_TABLE_NAME, User.ROLE_TABLE_DESCRIPTION, User.ROLE_GROUP_TABLE_NAME, User.ROLE_GROUP_TABLE_ID));
        var userResponse = userPage.stream()
                .map(UserResponse::new)
                .toList();
        var dataPageResponse = new DataPageResponse<>(userResponse, PageInfo.from(userPage));
        var generalResponse = GeneralResponse.success(dataPageResponse);
        log.info("--- End to search user ---");
        return new ResponseEntity<>(generalResponse, HttpStatus.OK);
    }

}
