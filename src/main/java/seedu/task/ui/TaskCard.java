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
            startTimeLabel.setText(" Starts: " + task.getStartTime().value);
        }else{
            startTimeLabel.setText("");
        }
        if(!task.getEndTime().toString().isEmpty()){
            endTimeLabel.setText(" Ends: " + task.getEndTime().value);
        }else{
            endTimeLabel.setText("");
        }
        if(!task.getDeadline().toString().isEmpty()){
            deadlineLabel.setText(" Due: " + task.getDeadline().value);
        }else{
            deadlineLabel.setText("");
        }
        tags.setText(task.tagsString());
    }

    //@@author A0147335E 
    public HBox getLayout() {
        /*
        if (task.getStatus().getNewlyAddedStatus() == true) {
            cardPane.setStyle("-fx-background-color: #FFFE00");
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    cardPane.setStyle("-fx-background-color: #FFFFFF");
                    task.getStatus().setNewlyAdded(false);
                }
            });
            delay.play();
        }
        */
        
        if (task.getStatus().getDoneStatus() && task.getStatus().getFavoriteStatus()) {
            cardPane.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #ADDBAC, #FFFE00)");   
        }
        else if (task.getStatus().getDoneStatus()) {
            cardPane.setStyle("-fx-background-color: #ADDBAC");
        }
        else if (task.getStatus().getFavoriteStatus() && task.getStatus().getOverdueStatus()) {
            cardPane.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #FF0000, #FFFE00)");
            
        }
        else if (task.getStatus().getOverdueStatus()) {
            cardPane.setStyle("-fx-background-color: #FF0000");
        }
        else if (task.getStatus().getFavoriteStatus()) {
            cardPane.setStyle("-fx-background-color: #FFFE00");
        }
        
        return cardPane;
    }

    
    @Override
    public void setNode(Node node) {
        cardPane = (HBox)node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}