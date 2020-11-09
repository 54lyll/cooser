package com.wonderzh.cooser.exception;

import java.io.IOException;

/**
 *
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class TimeoutException extends IOException {
    private static final long serialVersionUID = 1L;

    public TimeoutException(String message) {
        super(message);
    }
}
