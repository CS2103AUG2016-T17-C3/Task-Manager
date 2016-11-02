package seedu.task.ui;

import java.util.logging.Logger;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import seedu.task.commons.core.LogsCenter;
import seedu.task.model.task.ReadOnlyTask;

//@@author A0147335E-reused
public class TaskCard extends UiPart{
    private final Logger logger = LogsCenter.getLogger(TaskCard.class);
    private static final String FXML = "TaskListCard.fxml";

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Label deadlineLabel;
    @FXML
    private Label tags;

    private static ReadOnlyTask task;
    private int displayedIndex;

    public TaskCard(){

    }

    public static TaskCard load(ReadOnlyTask task, int displayedIndex){
        TaskCard card = new TaskCard();
        TaskCard.task = task;
        card.displayedIndex = displayedIndex;

        return UiPartLoader.loadUiPart(card);
    }

    @FXML
    public void initialize() {
        name.setText(task.getName().fullName);
        id.setText(displayedIndex + ". ");
        if (!task.getStartTime().toString().isEmpty()) {
            startTimeLabel.setText(" from " + task.getStartTime().value);
        }else{
            startTimeLabel.setText("");
        }
        if(!task.getEndTime().toString().isEmpty()){
            endTimeLabel.setText(" to " + task.getEndTime().value);
        }else{
            endTimeLabel.setText("");
        }
        if(!task.getDeadline().toString().isEmpty()){
            deadlineLabel.setText(" ends " + task.getDeadline().value);
        }else{
            deadlineLabel.setText("");
        }
        tags.setText(task.tagsString());
    }

    //@@author A0147335E 
   

    
    @Override
    public void setNode(Node node) {
        cardPane = (HBox)node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}
