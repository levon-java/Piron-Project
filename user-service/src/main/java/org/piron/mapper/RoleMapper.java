package org.piron.mapper;

import org.piron.common.dto.RoleDTO;
import org.piron.entity.Role;
import org.piron.security.model.RoleAuth;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public RoleDTO convertRoleToRoleDTO(Role role) {
        return RoleDTO.builder()
                .roleName(role.getRoleName())
                .build();
    }

    public Role convertRoleDTOToRole(RoleDTO roleDTO) {
        return Role.builder()
                .roleName(roleDTO.getRoleName())
                .build();
    }

    public RoleAuth convertRoleDTOToRoleAuth(RoleDTO roleDTO) {
        return RoleAuth.builder()
                .roleName(roleDTO.getRoleName())
                .build();
    }
}
