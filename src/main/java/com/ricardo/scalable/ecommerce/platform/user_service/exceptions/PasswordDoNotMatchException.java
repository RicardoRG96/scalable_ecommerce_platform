package com.ricardo.scalable.ecommerce.platform.user_service.exceptions;

public class PasswordDoNotMatchException  extends Exception{

    public PasswordDoNotMatchException(String message) {
        super(message);
    }

    public PasswordDoNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordDoNotMatchException(Throwable cause) {
        super(cause);
    }

}
