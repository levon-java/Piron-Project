package org.piron.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static final String ERROR_USER_NOT_FOUND = "User with login: '%s' was not found!";

    public static final String ERROR_BAD_CREDENTIALS = "Invalid credentials";

    public static final String ERROR_AUTHORIZATION = "Authorization error";

    public static final String ERROR_MISSING_AUTHORIZATION = "Missing header Authorization";

    public static final String ERROR_USER_NOT_UNIQUE = "User with login: '%s' already exist";

    public static final String ERROR_ROLE_NOT_FOUND = "Role with name: '%s' was not found!";

    public static final String ERROR_DELETING_ACCOUNT = "You cannot delete your account";
}
