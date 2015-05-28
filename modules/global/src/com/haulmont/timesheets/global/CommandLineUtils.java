/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class CommandLineUtils {
    public static final Pattern PROJECT_CODE_PATTERN = Pattern.compile("@([^ ]+?) +");
    public static final Pattern TASK_CODE_PATTERN = Pattern.compile("#([^ ]+?) +");
    public static final Pattern TAG_CODE_PATTERN = Pattern.compile("\\$([^ ]+?) +");
    public static final Pattern SPENT_TIME_PATTERN = Pattern.compile(" ([0-9]{1,2}[h,ч,:]([0-9]{1,2}[m,м]?)?)");

    protected String commandLine;

    public CommandLineUtils(String commandLine) {
        this.commandLine = commandLine;
    }

    @Nullable
    public String getProjectCode() {
        List<String> projects = getMatchedSubstring(PROJECT_CODE_PATTERN);
        return !projects.isEmpty() ? projects.get(0) : null;
    }

    @Nullable
    public String getTaskCode() {
        List<String> tasks = getMatchedSubstring(TASK_CODE_PATTERN);
        return !tasks.isEmpty() ? tasks.get(0) : null;
    }

    @Nullable
    public String getSpentTime() {
        List<String> times = getMatchedSubstring(SPENT_TIME_PATTERN);
        return !times.isEmpty() ? times.get(0) : null;
    }

    public List<String> getTagCodes() {
        return getMatchedSubstring(TAG_CODE_PATTERN);
    }

    protected List<String> getMatchedSubstring(Pattern pattern) {
        List<String> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(commandLine);
        if (matcher.find()) {
            String matched = matcher.group(1);
            result.add(matched);
        }

        return result;
    }
}
