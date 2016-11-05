package guitests.guihandles;

import guitests.GuiRobot;
import javafx.scene.Node;
import javafx.stage.Stage;
import seedu.task.model.task.ReadOnlyTask;

// @@author A0147944U
/**
 * Provides a handle to a task card in the task list panel.
 */
public class TaskCardHandle extends GuiHandle {
    private static final String COLOR_FIELD_ID = "#id";
    private static final String NAME_FIELD_ID = "#name";
    private static final String DEADLINE_FIELD_ID = "#deadlineLabel";
    private static final String STARTTIME_FIELD_ID = "#startTimeLabel";
    private static final String ENDTIME_FIELD_ID = "#endTimeLabel";
   

    private Node node;

    public TaskCardHandle(GuiRobot guiRobot, Stage primaryStage, Node node){
        super(guiRobot, primaryStage, null);
        this.node = node;
    }

    protected String getTextFromLabel(String fieldId) {
        return getTextFromLabel(fieldId, node);
    }

    public String getTaskName() {
        return getTextFromLabel(NAME_FIELD_ID);
    }

    public String getStartTime() {
        return getTextFromLabel(STARTTIME_FIELD_ID).replace(" Starts: ", "");
    }

    public String getEndTime() {
        return getTextFromLabel(ENDTIME_FIELD_ID).replace(" Ends: ", "");
    }

    public String getDeadline() {
        return getTextFromLabel(DEADLINE_FIELD_ID).replace(" Due: ", "");
    }
    
    public boolean getDoneStatus() {
        if(getColorFromLabel(COLOR_FIELD_ID, node).equals("-fx-background-color: #ADDBAC")) {
           
            return true;
        }
                
        else {
            return false;
        }
    }

    public boolean isSameTask(ReadOnlyTask task){
        return getTaskName().equals(task.getName().fullName) && getStartTime().equals(task.getStartTime().value)
                && getEndTime().equals(task.getEndTime().value) && getDeadline().equals(task.getDeadline().value)
                && getDoneStatus() == task.getStatus().getDoneStatus();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TaskCardHandle) {
            TaskCardHandle handle = (TaskCardHandle) obj;
            return getTaskName().equals(handle.getTaskName())
                    && getDeadline().equals(handle.getDeadline()); //TODO: compare the rest
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return getTaskName() + " " + getDeadline();
    }
}