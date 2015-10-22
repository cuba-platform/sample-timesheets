
package com.haulmont.timesheets.web.commandline;

import com.haulmont.timesheets.web.toolkit.ui.client.commandline.CommandLineRpc;
import org.vaadin.aceeditor.Suggester;
import org.vaadin.aceeditor.SuggestionExtension;

/**
 * @author degtyarjov
 */
public class CommandLineSuggestionExtension extends SuggestionExtension {
    protected Runnable applyHandler;
    
    public CommandLineSuggestionExtension(Suggester suggester) {
        super(suggester);

        registerRpc((CommandLineRpc) () -> {
            if (applyHandler != null) {
                applyHandler.run();
            }
        });
    }

    public void setApplyHandler(Runnable applyHandler) {
        this.applyHandler = applyHandler;
    }

    public Runnable getApplyHandler() {
        return applyHandler;
    }
}
