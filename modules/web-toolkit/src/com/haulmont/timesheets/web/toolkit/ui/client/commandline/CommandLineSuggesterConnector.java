/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.toolkit.ui.client.commandline;

import com.google.gwt.core.client.JavaScriptObject;
import com.haulmont.timesheets.web.commandline.CommandLineSuggestionExtension;
import com.vaadin.shared.ui.Connect;
import org.vaadin.aceeditor.client.SuggesterConnector;
import org.vaadin.aceeditor.client.gwt.GwtAceKeyboardEvent;

/**
 * @author degtyarjov
 * @version $Id$
 */
@SuppressWarnings("serial")
@Connect(CommandLineSuggestionExtension.class)
public class CommandLineSuggesterConnector extends SuggesterConnector {
    @Override
    protected void startSuggesting() {
        super.startSuggesting();
        popup.setWidth("300px");
    }

    @Override
    public Command handleKeyboard(JavaScriptObject data, int hashId,
                                  String keyString, int keyCode, GwtAceKeyboardEvent e) {
        if (suggesting) {
            return keyPressWhileSuggesting(keyCode);
        }
        if (e == null) {
            return Command.DEFAULT;
        }

        if (keyCode == 13) {//Enter
            return Command.NULL;//ignore enter
        } else if ((keyCode == 32 && e.isCtrlKey())) {//Ctrl+Space
            startSuggesting();
            return Command.NULL;
        } else if ((keyCode == 50 && e.isShiftKey())//@
                || (keyCode == 51 && e.isShiftKey())//#
                || (keyCode == 52 && e.isShiftKey())) {//$
            startSuggestingOnNextSelectionChange = true;
            widget.addSelectionChangeListener(this);
            return Command.DEFAULT;
//            startSuggesting();
//            return Command.DEFAULT;
        }

        return Command.DEFAULT;
    }
}
