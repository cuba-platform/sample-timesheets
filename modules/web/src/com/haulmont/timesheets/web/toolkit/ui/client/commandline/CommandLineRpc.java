/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.toolkit.ui.client.commandline;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author degtyarjov
 * @version $Id$
 */
public interface CommandLineRpc extends ServerRpc {
    void apply();
}
