package org.piron.service;

import lombok.RequiredArgsConstructor;
import org.piron.common.dto.UserDTO;
import org.piron.common.dto.response.AuthResponseDTO;
import org.piron.security.config.JwtService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.piron.common.constants.CustomConstants.AUTHORIZATION_HEADER_BEGINNING;
import static org.piron.common.constants.ExceptionConstants.ERROR_BAD_CREDENTIALS;
import static org.piron.common.constants.ExceptionConstants.ERROR_MISSING_AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO authenticate(String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith(AUTHORIZATION_HEADER_BEGINNING)) {
            throw new AuthenticationServiceException(ERROR_MISSING_AUTHORIZATION);
        }

        String base64Credentials = authorizationHeader.substring(6);
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        String[] values = credentials.split(":", 2);
        String username = values[0];
        String password = values[1];

        UserDTO userDTO = userService.getUserByLogin(username);

        if (!passwordEncoder.matches(password, userDTO.getPassword())) {
            throw new BadCredentialsException(ERROR_BAD_CREDENTIALS);
        }

        String jwtToken = jwtService.generateToken(username);

        return new AuthResponseDTO(username, jwtToken, userDTO.getRoles());
    }
}
