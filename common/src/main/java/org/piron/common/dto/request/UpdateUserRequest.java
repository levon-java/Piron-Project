package org.piron.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.piron.common.dto.RoleDTO;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @JsonProperty("login")
    @NotBlank(message = "Login is required!")
    private String login;

    @JsonProperty("password")
    private String password;

    @JsonProperty("roles")
    private Set<RoleDTO> roles;
}

