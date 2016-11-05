//@@author A0147335E
package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.task.testutil.TestTask;

public class FavoriteCommandTest extends TaskManagerGuiTest {
    @Test
    public void done() {
        TestTask[] currentList = td.getTypicalTasks();

        commandBox.runCommand("fav 1");
        commandBox.runCommand("unfav 1");
        assertDoneSuccess(currentList);

    }

    private void assertDoneSuccess(TestTask... currentList) {

        TestTask[] expectedList = currentList;
        // confirm the list now contains all previous tasks plus the new task
        expectedList[0].getStatus().setFavoriteStatus(false);
        assertTrue(taskListPanel.isListMatching(expectedList));
    }
}
