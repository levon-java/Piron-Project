package org.piron.security.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAuth implements GrantedAuthority {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }
}

