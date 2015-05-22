/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class CommandLineUtils {
    public static final Pattern PROJECT_CODE_PATTERN = Pattern.compile("@([^ ]+?) +");
    public static final Pattern TASK_CODE_PATTERN = Pattern.compile("#([^ ]+?) +");
    public static final Pattern SPENT_TIME_PATTERN = Pattern.compile(" ([0-9]{1,2}[h,ч,:]([0-9]{1,2}[m,м]?)?)");

    protected String commandLine;

    public CommandLineUtils(String commandLine) {
        this.commandLine = commandLine;
    }

    public String getProjectCode() {
        return getMatchedSubstring(PROJECT_CODE_PATTERN);
    }

    public String getTaskCode() {
        return getMatchedSubstring(TASK_CODE_PATTERN);
    }

    public String getSpentTime() {
        return getMatchedSubstring(SPENT_TIME_PATTERN);
    }

    @Nullable
    protected String getMatchedSubstring(Pattern pattern) {
        Matcher matcher = pattern.matcher(commandLine);
        if (matcher.find()) {
            String matched = matcher.group(1);
            return matched;
        }

        return null;
    }

}
