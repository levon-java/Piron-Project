package org.piron.advice;

import lombok.extern.slf4j.Slf4j;
import org.piron.common.dto.response.UserResponseDTO;
import org.piron.exception.MainException;
import org.piron.exception.UserNotUniqueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(UserNotUniqueException.class)
    public ResponseEntity<UserResponseDTO> handleUserNotUniqueException(UserNotUniqueException exception) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .hasError(true)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(userResponseDTO, status);
    }

    @ExceptionHandler(MainException.class)
    public ResponseEntity<UserResponseDTO> handleMainException(MainException exception) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .hasError(true)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(userResponseDTO, status);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<UserResponseDTO> handleWalletNotFoundException(UsernameNotFoundException exception) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        UserResponseDTO walletResponseDTO = UserResponseDTO.builder()
                .hasError(true)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(walletResponseDTO, status);
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<UserResponseDTO> handleException(Exception exception) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .hasError(true)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(userResponseDTO, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserResponseDTO> globalExceptionHandler(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .hasError(true)
                .message(errors.toString())
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }
}
