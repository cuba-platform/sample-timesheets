/*
 * Copyright (c) 2015 com.haulmont.ts.gui.client
 */
package com.haulmont.timesheets.gui.client;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.timesheets.entity.Client;
import com.haulmont.timesheets.gui.util.ComponentsHelper;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author gorelov
 */
public class ClientEdit extends AbstractEditor<Client> {
    @Inject
    private Datasource<Client> clientDs;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidthAuto();

        clientDs.addItemPropertyChangeListener(new ComponentsHelper.EntityCodeGenerationListener<>());
    }
}