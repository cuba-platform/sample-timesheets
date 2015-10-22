
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
import java.util.Collections;
import java.util.List;

/**
 * @author degtyarjov
 */
@ManagedBean
public class SystemDataManager {
    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    public <T extends Entity> T getEntityByCode(Class<T> clazz, String code, @Nullable String viewName) {
        LoadContext<T> loadContext = new LoadContext<>(clazz);
        MetaClass metaClass = metadata.getSession().getClassNN(clazz);
        loadContext.setQueryString("select e from " + metaClass.getName() + " e where e.code = :code")
                .setParameter("code", code);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        return dataManager.load(loadContext);
    }

    public <T extends Entity> List<T> getEntitiesByCodes(Class<T> clazz, List<String> codes, @Nullable String viewName) {
        if (codes.isEmpty()) {
            return Collections.emptyList();
        }
        LoadContext<T> loadContext = new LoadContext<>(clazz);
        MetaClass metaClass = metadata.getSession().getClassNN(clazz);
        loadContext.setQueryString("select e from " + metaClass.getName() + " e where e.code in :codes")
                .setParameter("codes", codes);
        if (viewName != null) {
            loadContext.setView(viewName);
        }
        return dataManager.loadList(loadContext);
    }

    public <T extends Entity> MetaPropertyPath getEntityMetaPropertyPath(Class<T> clazz, String property) {
        MetaClass metaClass = metadata.getSession().getClassNN(clazz);
        return metaClass.getPropertyPath(property);
    }
}
