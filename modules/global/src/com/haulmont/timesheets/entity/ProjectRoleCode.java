
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author degtyarjov
 */
public enum ProjectRoleCode implements EnumClass<String> {
    MANAGER("manager"),
    APPROVER("approver"),
    WORKER("worker"),
    OBSERVER("observer");

    private String id;

    ProjectRoleCode(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static ProjectRoleCode fromId(String id) {
        for (ProjectRoleCode at : ProjectRoleCode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
