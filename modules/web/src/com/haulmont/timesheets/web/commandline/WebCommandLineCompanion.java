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

package com.haulmont.timesheets.web.commandline;

import com.haulmont.timesheets.gui.commandline.CommandLine;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class WebCommandLineCompanion implements CommandLineFrameController.Companion {
    @Override
    public void setApplyHandler(CommandLine commandLine, Runnable handler) {
        WebCommandLine webCommandLine = (WebCommandLine) commandLine;
        webCommandLine.getSuggestionExtension().setApplyHandler(handler);
    }
}
