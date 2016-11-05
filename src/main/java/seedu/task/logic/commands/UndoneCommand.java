// @@author A0147335E
package seedu.task.logic.commands;

import java.util.ArrayList;

import seedu.task.commons.core.Messages;

import seedu.task.commons.core.UnmodifiableObservableList;
import seedu.task.logic.RollBackCommand;
import seedu.task.model.task.ReadOnlyTask;
import seedu.task.model.task.Task;
import seedu.task.model.task.UniqueTaskList.DuplicateTaskException;
import seedu.task.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * Undone a task from the task manager.
 */
public class UndoneCommand extends Command {
    public static final String COMMAND_WORD = "undone";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Undone the task identified by the index number used in the last task listing.\n"
            + "Parameters: INDEX TASKNAME\n"
            + "Example: " + COMMAND_WORD
            + " 4";

    public static final String MESSAGE_DONE_TASK_SUCCESS = "Undone Task: %1$s";

    public static final String MESSAGE_ALREADY_UNDONE = "Task has already been undone!";

    public final int targetIndex;

    public UndoneCommand(int targetIndex)
    {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(boolean isUndo) {
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();
        assert model != null;
        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        ReadOnlyTask currentTask = lastShownList.get(targetIndex - 1);
        boolean previousDoneStatus = currentTask.getStatus().getDoneStatus();

        try {
            model.deleteTask(currentTask);
        } catch (TaskNotFoundException e) {}

        Task taskToUndone = new Task(currentTask);
        taskToUndone.getStatus().setDoneStatus(false);
        try {
            model.addTask(targetIndex - 1, taskToUndone);
        } catch (DuplicateTaskException e) {}

        if (isEqual(previousDoneStatus, taskToUndone.getStatus().getDoneStatus())) {
            return new CommandResult(MESSAGE_ALREADY_UNDONE);
        }
        if (!isUndo) {
            getUndoList().add(new RollBackCommand(COMMAND_WORD, taskToUndone, null));
        }
        // @@author A0147944U
        // Sorts updated list of tasks
        model.autoSortBasedOnCurrentSortPreference();
        // @@author A0147335E
        return new CommandResult(String.format(MESSAGE_DONE_TASK_SUCCESS, taskToUndone.getName()));
    }

    private boolean isEqual(boolean previousDoneStatus, boolean currentDoneStatus) {
        return previousDoneStatus == currentDoneStatus;
    }

    private ArrayList<RollBackCommand> getUndoList() {
        return history.getUndoList();
    }

    @Override
    public CommandResult execute(int index) {
        return null;

    }
}