package com.komandda.web.controller.rest.permission.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Yevhen_Larikov on 1/8/2017.
 */
@ResponseStatus(value= HttpStatus.FORBIDDEN, reason="Missing permission to execute this action")
public class MissingPermissionException extends RuntimeException{

}
