
/*
 * Copyright (c) 2016 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.timesheets.web.toolkit.ui.client.commandline;

import com.google.gwt.core.client.JavaScriptObject;
import com.haulmont.timesheets.web.commandline.CommandLineSuggestionExtension;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.shared.ui.Connect;
import org.vaadin.aceeditor.client.SuggesterConnector;
import org.vaadin.aceeditor.client.gwt.GwtAceKeyboardEvent;

/**
 * @author degtyarjov
 */
@SuppressWarnings("serial")
@Connect(CommandLineSuggestionExtension.class)
public class CommandLineSuggesterConnector extends SuggesterConnector {
    protected CommandLineRpc commandLineRpc = RpcProxy.create(
            CommandLineRpc.class, this);

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
            commandLineRpc.apply();
            return Command.NULL;//ignore enter
        } else if ((keyCode == 32 && e.isCtrlKey())) {//Ctrl+Space
            startSuggesting();
            return Command.NULL;
        } else if ((keyCode == 50 && e.isShiftKey())//@
                || (keyCode == 51 && e.isShiftKey())//#
                || (keyCode == 52 && e.isShiftKey())//$
                || (keyCode == 56 && e.isShiftKey())) {//*
            startSuggestingOnNextSelectionChange = true;
            widget.addSelectionChangeListener(this);
            return Command.DEFAULT;
//            startSuggesting();
//            return Command.DEFAULT;
        }

        return Command.DEFAULT;
    }
}
