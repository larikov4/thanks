package com.komandda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Yevhen on 12.02.2017.
 */
@ResponseStatus(value=HttpStatus.CONFLICT, reason="Using busy resources detected")
public class UsingBusyResourcesException extends IllegalArgumentException {
}
