package org.piron.exception;

import static org.piron.common.constants.ExceptionConstants.ERROR_USER_NOT_UNIQUE;

public class UserNotUniqueException extends MainException {

    public UserNotUniqueException(String login) {
        super(String.format(ERROR_USER_NOT_UNIQUE, login));
    }
}
