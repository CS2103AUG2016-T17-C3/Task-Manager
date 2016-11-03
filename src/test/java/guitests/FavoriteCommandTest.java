//@@author A0147335E
package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import guitests.guihandles.TaskCardHandle;
import seedu.task.commons.core.Messages;
import seedu.task.logic.commands.UndoCommand;
import seedu.task.testutil.TestTask;
import seedu.task.testutil.TestUtil;

public class FavoriteCommandTest extends TaskManagerGuiTest {
    @Test
    public void done() {
        TestTask[] currentList = td.getTypicalTasks();
        
        commandBox.runCommand("fav 1");
        commandBox.runCommand("unfav 1");
        assertDoneSuccess(1, currentList);
        
    }

    private void assertDoneSuccess(int index, TestTask... currentList) {
        
         TestTask[] expectedList = currentList;
        //confirm the list now contains all previous tasks plus the new task
        expectedList[0].getStatus().setFavoriteStatus(false);
        assertTrue(taskListPanel.isListMatching(expectedList));
    }
}
