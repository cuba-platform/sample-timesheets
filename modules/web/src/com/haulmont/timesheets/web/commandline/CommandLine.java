/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.commandline;

import com.haulmont.cuba.gui.components.autocomplete.Suggester;
import com.haulmont.cuba.web.gui.components.WebSourceCodeEditor;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class CommandLine extends WebSourceCodeEditor {
    public static final String NAME = "commandLine";

    @Override
    public void setSuggester(Suggester suggester) {
        this.suggester = suggester;

        if (suggester != null && suggestionExtension == null) {
            suggestionExtension = new CommandLineSuggestionExtension(new CommandLineSourceCodeEditorSuggester());
            suggestionExtension.extend(component);
            suggestionExtension.setShowDescriptions(false);
        }
    }

    protected class CommandLineSourceCodeEditorSuggester extends SourceCodeEditorSuggester {
    }
}
