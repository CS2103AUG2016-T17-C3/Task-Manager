package seedu.task.logic.commands;

import java.util.HashSet;
import seedu.task.commons.core.UnmodifiableObservableList;
import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.model.tag.Tag;
import seedu.task.model.task.ReadOnlyTask;

/**
 * Undo previous commands that was input by the user.
 * 
 * @@author A0147335E
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undo a command which was previously input by user. ";
    public static final String MESSAGE_SUCCESS = "Undo: ";
    public static final String MESSAGE_FAIL = "Cannot undo anymore!";

    public static final String UNDO_EDIT_NAME = "name";
    public static final String UNDO_EDIT_START_TIME = "start";
    public static final String UNDO_EDIT_END_TIME = "end";
    public static final String UNDO_EDIT_DEADLINE = "deadline";
    public static final String UNDO_EDIT_TAG = "tag";

    public static final String EMPTY_STRING = "";
    public static final String DELIMITER = " ";
    public static final String NEW_LINE = "\n";

    public final int numOfTimes;

    public final boolean isMultiUndo;

    /**
     * Constructor for undo one command only
     *
     * @throws IllegalValueException
     *             if any of the raw values are invalid
     */
    public UndoCommand() {
        numOfTimes = 1;
        isMultiUndo = false;
    }

    /**
     * Constructor for undo multiple commands
     *
     * @throws IllegalValueException
     *             if any of the raw values are invalid
     */
    public UndoCommand(int numOfTimes) {
        this.numOfTimes = numOfTimes;
        isMultiUndo = true;
    }

    @Override
    public CommandResult execute(boolean isUndo) {
        if (history.getUndoList().size() == 0) {
            return new CommandResult(MESSAGE_FAIL);
        }
        String outputUndoList = EMPTY_STRING;
        for (int i = 0; i < numOfTimes; i++) {
            if (history.getUndoList().size() != 0) {
                outputUndoList += DELIMITER + MESSAGE_SUCCESS
                        + history.getPreviousCommandList().get(history.getPreviousCommandList().size() - 1) + NEW_LINE;
                String[] getIndex = history.getPreviousCommandList().get(history.getPreviousCommandList().size() - 1)
                        .split(DELIMITER, 4);
                String previousCommand = getIndex[0];
                String[] previousCommandDetails = getIndex;

                switch (previousCommand) {

                case AddCommand.COMMAND_WORD:
                    prepareUndoAdd();
                    break;

                case DeleteCommand.COMMAND_WORD:
                    prepareUndoDelete(previousCommandDetails);
                    break;

                case EditCommand.COMMAND_WORD:
                    prepareUndoEdit(previousCommandDetails);
                    break;

                case ClearCommand.COMMAND_WORD:
                    prepareUndoClear(previousCommandDetails);
                    break;

                case DoneCommand.COMMAND_WORD:
                    prepareUndoDone(previousCommandDetails);
                    break;

                case UndoneCommand.COMMAND_WORD:
                    prepareUndoUndone(previousCommandDetails);
                    break;

                case FavoriteCommand.COMMAND_WORD:
                    prepareUndoFavorite(previousCommandDetails);
                    break;

                case UnfavoriteCommand.COMMAND_WORD:
                    prepareUndoUnfavorite(previousCommandDetails);
                    break;

                case RefreshCommand.COMMAND_WORD:
                    prepareUndoRefreshCommand();
                    break;
                    
                default:
                    break;
                }
                checkCommandListSize();
            } else {
                if (!isMultiUndo) {
                    outputUndoList = MESSAGE_FAIL;
                }
            }
        }
        return new CommandResult(outputUndoList);
    }

    private void prepareUndoRefreshCommand() {
        Command command = new RefreshCommand();
        setData(command);
        executeCommand(command);
        removePreviousCommand();
    }

    private void prepareUndoDone(String[] previousCommandDetails) {
        int index = Integer.parseInt(previousCommandDetails[1]);
        Command command = new UndoneCommand(index);
        setData(command);
        executeCommand(command);
        removePreviousCommand();
    }

    private void prepareUndoUndone(String[] previousCommandDetails) {
        int index = Integer.parseInt(previousCommandDetails[1]);
        Command command = new DoneCommand(index);
        setData(command);
        executeCommand(command);
        removePreviousCommand();
    }

    private void prepareUndoFavorite(String[] previousCommandDetails) {
        int index = Integer.parseInt(previousCommandDetails[1]);
        Command command = new UnfavoriteCommand(index);
        setData(command);
        executeCommand(command);
        removePreviousCommand();
    }

    private void prepareUndoUnfavorite(String[] previousCommandDetails) {
        int index = Integer.parseInt(previousCommandDetails[1]);
        Command command = new FavoriteCommand(index);
        setData(command);
        executeCommand(command);
        removePreviousCommand();
    }

    private void prepareUndoClear(String[] previousCommandDetails) {
        int undoIndex = history.getUndoList().size() - 1;

        while (history.getUndoList().get(undoIndex).getCommandWord().equals("clear")) {
            HashSet<String> getTags = getTags(undoIndex);

            try {
                Command command = addCommand(undoIndex, getTags);
                setData(command);
                command.execute(0);

            } catch (IllegalValueException e) {

            }
            removePreviousCommand(undoIndex);
            undoIndex = decrement(undoIndex);
            if (undoIndex < 0) {
                break;
            }
            if (isNotClearCommand(undoIndex)) {
                break;
            }
        }

    }

    private void prepareUndoEdit(String[] previousCommandDetails) {
        int index = Integer.parseInt(previousCommandDetails[1]);
        String toEditItem = previousCommandDetails[2].replace(",", "");
        String toEdit = "";

        HashSet<String> tagStringSet = null;

        int undoIndex = history.getUndoList().size() - 1;

        switch (toEditItem) {

        case UNDO_EDIT_NAME:
            toEdit = history.getUndoList().get(undoIndex).getOldTask().getName().toString();
            break;
        case UNDO_EDIT_START_TIME:
            toEdit = history.getUndoList().get(undoIndex).getOldTask().getStartTime().toString();
            break;
        case UNDO_EDIT_END_TIME:
            toEdit = history.getUndoList().get(undoIndex).getOldTask().getEndTime().toString();
            break;
        case UNDO_EDIT_DEADLINE:
            toEdit = history.getUndoList().get(undoIndex).getOldTask().getDeadline().toString();
            break;
        case UNDO_EDIT_TAG:
            HashSet<Tag> tagSet = new HashSet<>(history.getUndoList().get(undoIndex).getNewTask().getTags().toSet());
            tagStringSet = new HashSet<>(tagSet.size());
            break;
        }

        try {
            Command command = new EditCommand(index, toEditItem, toEdit, tagStringSet);
            setData(command);
            executeCommand(command);
        } catch (IllegalValueException e) {
        }
        removePreviousCommand(undoIndex);

    }

    private void prepareUndoAdd() {

        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();
        Command command = new DeleteCommand(lastShownList.size());
        setData(command);
        executeCommand(command);

        removePreviousCommand();

    }

    private void prepareUndoDelete(String[] previousCommandDetails) {

        int index = Integer.parseInt(previousCommandDetails[1]) - 1;
        int size = history.getUndoList().size() - 1;

        HashSet<String> tagStringSet = getTags(size);

        try {
            Command command = addCommand(size, tagStringSet);
            setData(command);
            command.execute(index);

        } catch (IllegalValueException e) {
        }
        removePreviousCommand(size);
    }

    private void executeCommand(Command command) {
        command.execute(true);
    }

    private void setData(Command command) {
        command.setData(model);
    }
    
    private void removePreviousCommand() {
        history.getUndoList().remove(history.getUndoList().size() - 1);
    }
    
    private boolean isNotClearCommand(int index) {
        return !(history.getUndoList().get(index).getCommandWord().equals("clear"));
    }

    private int decrement(int index) {
        index--;
        return index;
    }

    private void removePreviousCommand(int index) {
        history.getUndoList().remove(index);
    }

    private HashSet<String> getTags(int index) {
        HashSet<Tag> tagSet = new HashSet<>(history.getUndoList().get(index).getNewTask().getTags().toSet());
        HashSet<String> getTags = new HashSet<>(tagSet.size());
        for (Tag tags : tagSet) {
            getTags.add(tags.tagName);
        }
        return getTags;
    }

    private Command addCommand(int index, HashSet<String> tagStringSet) throws IllegalValueException {
        Command command = new AddCommand("" + history.getUndoList().get(index).getNewTask().getName(),
                "" + history.getUndoList().get(index).getNewTask().getStartTime(),
                "" + history.getUndoList().get(index).getNewTask().getEndTime(),
                "" + history.getUndoList().get(index).getNewTask().getDeadline(), tagStringSet);
        return command;
    }

    private void checkCommandListSize() {
        if (history.getPreviousCommandList().size() != 0) {
            history.getPreviousCommandList().remove(history.getPreviousCommandList().size() - 1);
        }
    }
    
    @Override
    public CommandResult execute(int index) {
        return null;
    }
    

}