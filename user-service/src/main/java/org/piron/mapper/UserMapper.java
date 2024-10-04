package org.piron.mapper;

import lombok.RequiredArgsConstructor;
import org.piron.common.dto.UserDTO;
import org.piron.common.dto.request.CreateUserRequest;
import org.piron.common.dto.request.UpdateUserRequest;
import org.piron.entity.Role;
import org.piron.entity.User;
import org.piron.exception.MainException;
import org.piron.repository.RoleRepository;
import org.piron.security.model.UserAuth;
import org.piron.service.RoleService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.piron.common.constants.ExceptionConstants.ERROR_ROLE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleMapper roleMapper;

    private final RoleRepository roleRepository;

    private final RoleService roleService;

    public User createUserByUpdateUserRequest(UpdateUserRequest updateUserRequest, User user) {
        if (Objects.nonNull(updateUserRequest.getPassword())) {
            user.setPassword(updateUserRequest.getPassword());
        }

        if (updateUserRequest.getRoles() != null && !updateUserRequest.getRoles().isEmpty()) {
            user.setRoles(updateUserRequest.getRoles().stream()
                    .map(roleMapper::convertRoleDTOToRole).collect(Collectors.toSet()));
        }
        return user;
    }

    public User convertCreateUserRequestToUser(CreateUserRequest createUserRequest) {
        return User.builder()
                .login(createUserRequest.getLogin())
                .password(createUserRequest.getPassword())
                .roles(createUserRequest.getRoles().stream()
                        .map(roleMapper::convertRoleDTOToRole)
                        .map(this::getRoleIfExist)
                        .collect(Collectors.toSet()))
                .build();
    }

    public UserDTO convertUserToUserDTO(User user) {
        return UserDTO.builder()
                .login(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles().stream()
                        .map(roleMapper::convertRoleToRoleDTO).collect(Collectors.toSet()))
                .build();
    }

    public UserAuth convertUserDTOToUserDTO(UserDTO userDTO) {
        return UserAuth.builder()
                .login(userDTO.getLogin())
                .password(userDTO.getPassword())
                .roles(userDTO.getRoles().stream()
                        .map(roleMapper::convertRoleDTOToRoleAuth).collect(Collectors.toSet()))
                .build();
    }

    private Role getRoleIfExist(Role role) {
        return roleRepository.findByRoleName(role.getRoleName())
                .orElseThrow(() -> new MainException(String.format(ERROR_ROLE_NOT_FOUND, role.getRoleName())));
    }
}
