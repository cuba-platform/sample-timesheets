/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

/**
 * @author gorelov
 * @version $Id$
 */
public class EntityDeletionException extends RuntimeException {

    public EntityDeletionException(String message) {
        super(message);
    }
}
