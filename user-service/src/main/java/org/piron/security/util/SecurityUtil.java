package org.piron.security.util;

import org.piron.security.model.UserAuth;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static org.piron.common.constants.ExceptionConstants.ERROR_AUTHORIZATION;

@Component
public class SecurityUtil {

    public UserAuth getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return (UserAuth) userDetails;
            } else {
                throw new SecurityException(ERROR_AUTHORIZATION);
            }
        } else {
            throw new SecurityException(ERROR_AUTHORIZATION);
        }
    }
}
