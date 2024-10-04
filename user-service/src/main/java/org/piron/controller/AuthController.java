package org.piron.controller;

import lombok.RequiredArgsConstructor;
import org.piron.common.dto.response.AuthResponseDTO;
import org.piron.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.piron.common.constants.ApiConstants.API_AUTH_PATH;

@RestController
@RequestMapping(API_AUTH_PATH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestHeader("Authorization") String authorizationHeader) {
        AuthResponseDTO responseDTO = authService.authenticate(authorizationHeader);
        return ResponseEntity.ok(responseDTO);
    }
}
