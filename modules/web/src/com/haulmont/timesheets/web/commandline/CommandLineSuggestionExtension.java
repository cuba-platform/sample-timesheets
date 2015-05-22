/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.commandline;

import org.vaadin.aceeditor.Suggester;
import org.vaadin.aceeditor.SuggestionExtension;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class CommandLineSuggestionExtension extends SuggestionExtension {
    public CommandLineSuggestionExtension(Suggester suggester) {
        super(suggester);
    }
}
