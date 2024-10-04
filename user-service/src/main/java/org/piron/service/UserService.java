package org.piron.service;

import lombok.RequiredArgsConstructor;
import org.piron.common.dto.UserDTO;
import org.piron.common.dto.request.CreateUserRequest;
import org.piron.common.dto.request.UpdateUserRequest;
import org.piron.entity.User;
import org.piron.exception.MainException;
import org.piron.exception.UserNotUniqueException;
import org.piron.mapper.UserMapper;
import org.piron.repository.UserRepository;
import org.piron.security.model.UserAuth;
import org.piron.security.util.SecurityUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static org.piron.common.constants.CustomConstants.BCRYPT_PATTERN;
import static org.piron.common.constants.ExceptionConstants.ERROR_DELETING_ACCOUNT;
import static org.piron.common.constants.ExceptionConstants.ERROR_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final SecurityUtil securityUtil;

    @Transactional
    public UserDTO getUserByLogin(String login) {
        return userRepository.findByLogin(login).map(userMapper::convertUserToUserDTO).orElseThrow(
                () -> new UsernameNotFoundException(String.format(ERROR_USER_NOT_FOUND, login)));
    }

    @Transactional
    public void createUser(CreateUserRequest createUserRequest) {
        boolean userExistsInDB = userRepository.existsUserByLogin(createUserRequest.getLogin());
        if (userExistsInDB) {
            throw new UserNotUniqueException(createUserRequest.getLogin());
        } else {
            User user = userMapper.convertCreateUserRequestToUser(createUserRequest);

            if (!BCRYPT_PATTERN.matcher(user.getPassword()).matches()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUser(UpdateUserRequest updateUserRequest) {
        Optional<User> userFromDB = userRepository.findByLogin(updateUserRequest.getLogin());

        if (userFromDB.isPresent()) {
            User userByUpdateUserRequest = userMapper.createUserByUpdateUserRequest(updateUserRequest, userFromDB.get());
            userRepository.save(userByUpdateUserRequest);
        } else {
            throw new UsernameNotFoundException(String.format(ERROR_USER_NOT_FOUND, updateUserRequest.getLogin()));
        }
    }

    @Transactional
    public void deleteUser(String login) {
        UserAuth currentUserLogin = securityUtil.getCurrentUserLogin();
        if (Objects.equals(login, currentUserLogin.getLogin())) {
            throw new MainException(ERROR_DELETING_ACCOUNT);
        } else {
            User user = userRepository.findByLogin(login).orElseThrow(
                    () -> new UsernameNotFoundException(String.format(ERROR_USER_NOT_FOUND, login)));

            userRepository.delete(user);
        }
    }
}
