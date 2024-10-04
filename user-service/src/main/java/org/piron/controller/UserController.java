package org.piron.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.piron.common.constants.ApiConstants;
import org.piron.common.dto.request.CreateUserRequest;
import org.piron.common.dto.request.UpdateUserRequest;
import org.piron.common.dto.response.UserResponseDTO;
import org.piron.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_USERS_PATH)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .login(createUserRequest.getLogin())
                .build();
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        userService.updateUser(updateUserRequest);
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .login(updateUserRequest.getLogin())
                .build();
        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{login}")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable String login) {
        userService.deleteUser(login);
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .login(login)
                .build();
        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }
}
