package org.piron.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.piron.common.dto.RoleDTO;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    @JsonProperty("login")
    private String login;

    @JsonProperty("token")
    private String token;

    @JsonProperty("roles")
    private Set<RoleDTO> roles;
}
