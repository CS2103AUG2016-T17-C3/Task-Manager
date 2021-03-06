package seedu.task.commons.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.task.commons.core.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// @@author A0147944U
public class ConfigTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void toStringDefaultObjectStringReturned() {
        String defaultConfigAsString = "App title : Task App\n" +
                                       "Current log level : INFO\n" +
                                       "Preference file Location : preferences.json\n" +
                                       "Local data file location : data/taskmanager.xml\n" +
                                       "TaskManager name : MyTaskManager\n" +
                                       "Current Sorting Preference : Default";

        assertEquals(defaultConfigAsString, new Config().toString());
    }

    @Test
    public void equalsMethod() {
        Config defaultConfig = new Config();
        assertFalse(defaultConfig == null);
        assertTrue(defaultConfig.equals(defaultConfig));
    }

}
