/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author degtyarjov
 * @version $Id$
 */
public class CommandLineUtilsTest {
    @Test
    public void testParseSpentTime() throws Exception {
        String commandLine = "@PLATFORM #DEVELOPMENT 2h30m";
        CommandLineUtils commandLineUtils = new CommandLineUtils(commandLine);
        String spentTime = commandLineUtils.getSpentTime();
        String projectCode = commandLineUtils.getProjectCode();
        String taskCode = commandLineUtils.getTaskCode();
        Assert.assertEquals("PLATFORM", projectCode);
        Assert.assertEquals("DEVELOPMENT", taskCode);
        Assert.assertEquals("2h30m", spentTime);

        commandLine = "@PLATFORM #DEVELOPMENT 2:30";
        commandLineUtils = new CommandLineUtils(commandLine);
        spentTime = commandLineUtils.getSpentTime();
        projectCode = commandLineUtils.getProjectCode();
        taskCode = commandLineUtils.getTaskCode();
        Assert.assertEquals("PLATFORM", projectCode);
        Assert.assertEquals("DEVELOPMENT", taskCode);
        Assert.assertEquals("2:30", spentTime);

    }
}
