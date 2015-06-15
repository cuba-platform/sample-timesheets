
package com.haulmont.timesheets.web.approve;

import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.timesheets.gui.approve.ApproveScreen;

/**
 * @author gorelov
 */
public class WebApproveScreenCompanion implements ApproveScreen.Companion {
    @Override
    public void initTable(Table table) {
        com.vaadin.ui.Table webTable = WebComponentsHelper.unwrap(table);
        webTable.setSelectable(false);
    }
}
