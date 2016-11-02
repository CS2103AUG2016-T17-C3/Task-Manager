package seedu.task.model.task;

/**
 * Represents a Task status in the task manager.
 * @@author A0147335E
 */
public class Status {

    private boolean isDone;

    private boolean isOverdue;

    private boolean isFavorite;

    public Status() {
        this.isDone = false;
        this.isOverdue = false;
        this.isFavorite = false;
    }

    public Status(boolean isDone, boolean isOverdue, boolean isFavorite) {
        this.isDone = isDone;
        this.isOverdue = isOverdue;
        this.isFavorite = isFavorite;
    }

    public void setDoneStatus(boolean doneStatus) {
        this.isDone = doneStatus;
    }

    public void setOverdueStatus(boolean overdueStatus) {
        this.isDone = overdueStatus;
    }

    public void setFavoriteStatus(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean getDoneStatus() {
        return isDone;
    }

    public boolean getOverdueStatus() {
        return isOverdue;
    }

    public boolean getFavoriteStatus() {
        return isFavorite;
    }
}
