
package com.haulmont.timesheets.global;

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author degtyarjov
 */
public class CommandLineProcessorTest {
    @Test
    public void testParseSpentTime() throws Exception {
        String commandLine = "@PLATFORM #DEVELOPMENT 2h30m";
        CommandLineProcessor commandLineProcessor = new CommandLineProcessor(commandLine);
        String spentTime = commandLineProcessor.getSpentTime();
        String projectCode = commandLineProcessor.getProjectCode();
        String taskCode = commandLineProcessor.getTaskCode();
        Assert.assertEquals("PLATFORM", projectCode);
        Assert.assertEquals("DEVELOPMENT", taskCode);
        Assert.assertEquals("2h30m", spentTime);

        commandLine = "@PLATFORM #DEVELOPMENT 2:30";
        commandLineProcessor = new CommandLineProcessor(commandLine);
        spentTime = commandLineProcessor.getSpentTime();
        projectCode = commandLineProcessor.getProjectCode();
        taskCode = commandLineProcessor.getTaskCode();
        Assert.assertEquals("PLATFORM", projectCode);
        Assert.assertEquals("DEVELOPMENT", taskCode);
        Assert.assertEquals("2:30", spentTime);

    }

    @Test
    public void testParseTags() throws Exception {
        String commandLine = "@PLATFORM #DEVELOPMENT 2h30m $Extend_#1";
        CommandLineProcessor commandLineProcessor = new CommandLineProcessor(commandLine);
        String spentTime = commandLineProcessor.getSpentTime();
        String projectCode = commandLineProcessor.getProjectCode();
        String taskCode = commandLineProcessor.getTaskCode();
        List<String> tagCodes = commandLineProcessor.getTagCodes();
        Assert.assertEquals("PLATFORM", projectCode);
        Assert.assertEquals("DEVELOPMENT", taskCode);
        Assert.assertEquals("2h30m", spentTime);
        Assert.assertEquals("Extend_#1", tagCodes.get(0));
    }
}
