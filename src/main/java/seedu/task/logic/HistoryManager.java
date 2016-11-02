package seedu.task.logic;

import java.util.ArrayList;

/**
 * This class keeps track of the successful commands typed by users
 * to allow and support undo command.
 * 
 * @@author A0147335E
 *
 */
public class HistoryManager {

    private ArrayList<RollBackCommand> undoList;
    
    private ArrayList<RollBackCommand> redoList;

    private ArrayList<String> previousCommandList;

    public HistoryManager() {
        undoList = new ArrayList<RollBackCommand>();
        redoList = new ArrayList<RollBackCommand>();

        previousCommandList = new ArrayList<String>();
    }

    public void setUndoList(ArrayList<RollBackCommand> undoList) {
        this.undoList = undoList;
    }

    public void setRedoList(ArrayList<RollBackCommand> redoList) {
        this.redoList = redoList;
    }

    public void setPreviousCommand(ArrayList<String> previousCommand) {
        this.previousCommandList = previousCommand;
    }

    public ArrayList<RollBackCommand> getUndoList() {
        return undoList;
    }

    public ArrayList<RollBackCommand> getRedoList() {
        return redoList;
    }

    public ArrayList<String> getPreviousCommandList() {
        return previousCommandList;
    }
}
