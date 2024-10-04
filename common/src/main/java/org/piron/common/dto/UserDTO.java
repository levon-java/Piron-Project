package org.piron.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @JsonProperty("login")
    @NotBlank(message = "Login is required!")
    private String login;

    @JsonProperty("password")
    @NotBlank(message = "Password is required!")
    private String password;

    @JsonProperty("roles")
    @NotEmpty(message = "At least one role is required!")
    private Set<RoleDTO> roles;
}
