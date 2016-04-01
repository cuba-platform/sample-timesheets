
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

package com.haulmont.timesheets.global;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author degtyarjov
 */
public class CommandLineProcessor {
    public static final Pattern PROJECT_CODE_PATTERN = Pattern.compile("@([^ ]+?)( |$)+");
    public static final Pattern TASK_CODE_PATTERN = Pattern.compile("#([^ ]+?)( |$)+");
    public static final Pattern TAG_CODE_PATTERN = Pattern.compile("\\$([^ ]+?)( |$)+");
    public static final Pattern ACTIVITY_TYPE_CODE_PATTERN = Pattern.compile("\\*([^ ]+?)( |$)+");
    public static final Pattern SPENT_TIME_PATTERN = Pattern.compile(" ([0-9]{1,2}[H,h,Ч,ч,:]([0-9]{1,2}[M,m,М,м]?)?)");

    protected String commandLine;

    public CommandLineProcessor(String commandLine) {
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

    @Nullable
    public String getActivityType() {
        List<String> times = getMatchedSubstring(ACTIVITY_TYPE_CODE_PATTERN);
        return !times.isEmpty() ? times.get(0) : null;
    }

    public List<String> getTagCodes() {
        return getMatchedSubstring(TAG_CODE_PATTERN);
    }

    protected List<String> getMatchedSubstring(Pattern pattern) {
        List<String> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(commandLine);
        while (matcher.find()) {
            String matched = matcher.group(1);
            result.add(matched);
        }

        return result;
    }
}
