/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * @author degtyarjov
 * @version $Id$
 */
@ManagedBean
public class SystemDataManager {
    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    public <T extends Entity> T getEntityByCode(Class<T> clazz, String code, @Nullable String viewName) {
        LoadContext loadContext = new LoadContext(clazz);
        MetaClass metaClass = metadata.getSession().getClassNN(clazz);
        loadContext.setQueryString("select e from " + metaClass.getName() + " e where e.code = :code")
                .setParameter("code", code);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        return dataManager.load(loadContext);
    }

    public <T extends Entity> MetaPropertyPath getEntityMetaPropertyPath(Class<T> clazz, String property) {
        MetaClass metaClass = metadata.getSession().getClassNN(clazz);
        return metaClass.getPropertyPath(property);
    }
}
