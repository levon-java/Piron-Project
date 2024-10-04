package org.piron.service;

import lombok.RequiredArgsConstructor;
import org.piron.common.dto.RoleDTO;
import org.piron.exception.MainException;
import org.piron.mapper.RoleMapper;
import org.piron.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.piron.common.constants.ExceptionConstants.ERROR_ROLE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public RoleDTO getRoleByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName).map(roleMapper::convertRoleToRoleDTO)
                .orElseThrow(() -> new MainException(String.format(ERROR_ROLE_NOT_FOUND, roleName)));
    }
}
