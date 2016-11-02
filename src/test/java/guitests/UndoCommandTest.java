//@@author A0147335E
package guitests;

import guitests.guihandles.TaskCardHandle;
import org.junit.Test;

import seedu.task.commons.core.Messages;
import seedu.task.logic.commands.UndoCommand;
import seedu.task.testutil.TestTask;
import seedu.task.testutil.*;

import static org.junit.Assert.assertTrue;

public class UndoCommandTest extends TaskManagerGuiTest {

    @Test
    public void undo() {
        TestTask[] currentList = td.getTypicalTasks();
        
        commandBox.runCommand("edit 1 name, Accompany dad to the doctor");
        commandBox.runCommand("edit 1 tag, gwsDad");
        commandBox.runCommand("undo");
        commandBox.runCommand("undo");
        assertUndoSuccess(currentList);
        
        commandBox.runCommand("delete 7");
        commandBox.runCommand("undo");
        assertUndoSuccess(currentList);
        
        commandBox.runCommand("undo");
        assertResultMessage(UndoCommand.MESSAGE_FAIL);
        
        commandBox.runCommand("clear");
        commandBox.runCommand("undo");
        assertUndoSuccess(currentList);
        
        commandBox.runCommand("undoed");
        assertResultMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
        
        commandBox.runCommand("done 5");
        commandBox.runCommand("undo");
        assertUndoSuccess(currentList);
        
        commandBox.runCommand("done 6");
        commandBox.runCommand("undone 6");
        commandBox.runCommand("undo");
        commandBox.runCommand("undone 6");
        assertUndoSuccess(currentList);
        
    }

    private void assertUndoSuccess(TestTask... currentList) {
        

        //confirm the list now contains all previous tasks plus the new task
        TestTask[] expectedList = currentList;
        assertTrue(taskListPanel.isListMatching(expectedList));
    }

}