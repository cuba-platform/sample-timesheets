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

    protected String commandLine;

    public CommandLineUtils(String commandLine) {
        this.commandLine = commandLine;
    }

    public String getProjectCode(){
        return getMatchedSubstring(PROJECT_CODE_PATTERN);
    }

    public String getTaskCode(){
        return getMatchedSubstring(TASK_CODE_PATTERN);
    }

    @Nullable
    protected String getMatchedSubstring(Pattern pattern) {
        Matcher matcher = pattern.matcher(commandLine);
        if (matcher.find()) {
            String projectCode = matcher.group(1);
            return projectCode;
        }

        return null;
    }

}
