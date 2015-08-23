package com.jaouan.snapandmatch.components.models.exceptions;

/**
 * Service technical exception model.
 *
 * @author Maxence Jaouan
 */
public class ServiceTechnicalException extends Exception {

    /**
     * Constructor.
     *
     * @param message Message.
     * @param cause   Cause.
     */
    public ServiceTechnicalException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
