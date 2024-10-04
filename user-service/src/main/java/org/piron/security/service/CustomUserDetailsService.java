package org.piron.security.service;

import lombok.RequiredArgsConstructor;
import org.piron.common.dto.UserDTO;
import org.piron.mapper.UserMapper;
import org.piron.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userByLogin = userService.getUserByLogin(username);
        return userMapper.convertUserDTOToUserDTO(userByLogin);
    }
}
