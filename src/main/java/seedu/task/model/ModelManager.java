package seedu.task.model;



import javafx.collections.transformation.FilteredList;
import seedu.task.commons.core.ComponentManager;
import seedu.task.commons.core.Config;
import seedu.task.commons.core.LogsCenter;
import seedu.task.commons.core.UnmodifiableObservableList;
import seedu.task.commons.events.model.TaskManagerChangedEvent;
import seedu.task.commons.exceptions.DataConversionException;
import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.commons.util.ConfigUtil;
import seedu.task.commons.util.FileUtil;
import seedu.task.logic.parser.TimeParser;
import seedu.task.logic.parser.TimeParserResult;
import seedu.task.logic.parser.TimeParserResult.DateTimeStatus;
import seedu.task.model.task.Deadline;
import seedu.task.model.task.EndTime;
import seedu.task.model.task.ReadOnlyTask;
import seedu.task.model.task.StartTime;
import seedu.task.model.task.Status;
import seedu.task.model.task.Task;
import seedu.task.model.task.UniqueTaskList;
import seedu.task.model.task.UniqueTaskList.DuplicateTaskException;
import seedu.task.model.task.UniqueTaskList.TaskNotFoundException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents the in-memory model of the task manager data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TaskManager taskManager;
    private final FilteredList<Task> filteredTasks;

    /**
     * Initializes a ModelManager with the given TaskManager
     * TaskManager and its variables should not be null
     */
    public ModelManager(TaskManager src, UserPrefs userPrefs) {
        super();
        assert src != null;
        assert userPrefs != null;

        logger.fine("Initializing with task manager: " + src + " and user prefs " + userPrefs);

        taskManager = new TaskManager(src);
        filteredTasks = new FilteredList<>(taskManager.getTasks());
    }

    public ModelManager() {
        this(new TaskManager(), new UserPrefs());
    }

    public ModelManager(ReadOnlyTaskManager initialData, UserPrefs userPrefs) {
        taskManager = new TaskManager(initialData);
        filteredTasks = new FilteredList<>(taskManager.getTasks());
    }

    @Override
    public void resetData(ReadOnlyTaskManager newData) {
        taskManager.resetData(newData);
        indicateTaskManagerChanged();
    }

    @Override
    public ReadOnlyTaskManager getTaskManager() {
        return taskManager;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateTaskManagerChanged() {
        raise(new TaskManagerChangedEvent(taskManager));
    }

    @Override
    public synchronized void deleteTask(ReadOnlyTask target) throws TaskNotFoundException {
        taskManager.removeTask(target);
        indicateTaskManagerChanged();
    }

    @Override
    public synchronized void addTask(Task task) throws UniqueTaskList.DuplicateTaskException {
        if (!task.getDeadline().toString().isEmpty()) {
            String strDatewithTime = task.getDeadline().toString().replace(" ", "T");
            LocalDateTime aLDT = LocalDateTime.parse(strDatewithTime);

            Date currentDate=new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());

            if (aLDT.isBefore(localDateTime)) {

                task = new Task (task.getName(), task.getStartTime(), task.getEndTime(), task.getDeadline(), task.getTags(), new Status(task.getStatus().getDoneStatus(), true, task.getStatus().getFavoriteStatus()), task.getRecurring());

            }
            else{
                task = new Task (task.getName(), task.getStartTime(), task.getEndTime(), task.getDeadline(), task.getTags(), new Status(task.getStatus().getDoneStatus(), false, task.getStatus().getFavoriteStatus()), task.getRecurring());

            }

        }
        else {
            task = new Task (task.getName(), task.getStartTime(), task.getEndTime(), task.getDeadline(), task.getTags(), new Status(task.getStatus().getDoneStatus(), false, task.getStatus().getFavoriteStatus()), task.getRecurring());

        }
        taskManager.addTask(task);
        updateFilteredListToShowAll();
        indicateTaskManagerChanged();
        
       
    }
    
    // @@author A0147335E-reused
    @Override
    public synchronized void addTask(int index, Task task) throws UniqueTaskList.DuplicateTaskException {
        if (!task.getDeadline().toString().isEmpty()) {
            String strDatewithTime = task.getDeadline().toString().replace(" ", "T");
            LocalDateTime aLDT = LocalDateTime.parse(strDatewithTime);

            Date currentDate=new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());

            if (aLDT.isBefore(localDateTime)) {

                task = new Task (task.getName(), task.getStartTime(), task.getEndTime(), task.getDeadline(), task.getTags(), new Status(task.getStatus().getDoneStatus(), true, task.getStatus().getFavoriteStatus()), task.getRecurring());

            }
            else{
                task = new Task (task.getName(), task.getStartTime(), task.getEndTime(), task.getDeadline(), task.getTags(), new Status(task.getStatus().getDoneStatus(), false, task.getStatus().getFavoriteStatus()), task.getRecurring());

            }

        }
        else {
            task = new Task (task.getName(), task.getStartTime(), task.getEndTime(), task.getDeadline(), task.getTags(), new Status(task.getStatus().getDoneStatus(), false, task.getStatus().getFavoriteStatus()), task.getRecurring());

        }
        taskManager.addTask(index, task);
        updateFilteredListToShowAll();
        indicateTaskManagerChanged();
    }
    //@@author

    // @@author A0147944U
    @Override
    public void repeatRecurringTask(Task recurringTask) {
        if (!recurringTask.getRecurring().toString().equals("false")) {
            String newStartTime = recurringTask.getStartTime().toString();
            String newEndTime = recurringTask.getEndTime().toString();
            String newDeadline = recurringTask.getDeadline().toString();
            
            if (!newStartTime.equals("")) {
                newStartTime = addPeriodicTimeToTask(recurringTask.getStartTime().toString(), recurringTask.getRecurring().toString());
            }
            if (!newEndTime.equals("")) {
                newEndTime = addPeriodicTimeToTask(recurringTask.getEndTime().toString(), recurringTask.getRecurring().toString());
            }
            if (!newDeadline.equals("")) {
                newDeadline = addPeriodicTimeToTask(recurringTask.getDeadline().toString(), recurringTask.getRecurring().toString());
            }
            
            try {
                Task newTask = new Task(recurringTask.getName(), new StartTime(newStartTime), new EndTime(newEndTime), new Deadline(newDeadline), recurringTask.getTags(), new Status(false, false, recurringTask.getStatus().getFavoriteStatus()), recurringTask.getRecurring());
                taskManager.addTask(newTask);
            } catch (DuplicateTaskException e) {
                e.printStackTrace();
            } catch (IllegalValueException e) {
                e.printStackTrace();
            }
        }
    }

    private String addPeriodicTimeToTask(String originalTime, String interval) {
        String newTime = "one week after " + originalTime;
        if (interval.equals("hourly")) {
            newTime = "one hour after " + originalTime;
        } else if (interval.equals("daily")) {
            newTime = "one day after " + originalTime;
        } else if (interval.equals("weekly")) {
            newTime = "one week after " + originalTime;
        } else if (interval.equals("fortnightly")) {
            newTime = "two weeks after " + originalTime;
        } else if (interval.equals("monthly")) {
            newTime = "one month after " + originalTime;
        }  else if (interval.equals("yearly")) {
            newTime = "one year after " + originalTime;
        }
        TimeParser parserTime = new TimeParser();
        TimeParserResult time = parserTime.parseTime(newTime);
        StringBuilder newTimeString = new StringBuilder();
        if (time.getRawDateTimeStatus() == DateTimeStatus.START_DATE_START_TIME) {
            newTimeString.append(time.getFirstDate().toString());
            newTimeString.append(" ");
            newTimeString.append(time.getFirstTime().toString().substring(0, 5));
        }
        return newTimeString.toString();
    }
    // @@author
    
    //=========== Filtered Task List Accessors ===============================================================

    @Override
    public UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList() {
        return new UnmodifiableObservableList<>(filteredTasks);
    }

    @Override
    public void updateFilteredListToShowAll() {
        filteredTasks.setPredicate(null);
    }

    @Override
    public void updateFilteredTaskList(Set<String> keywords){
        updateFilteredTaskList(new PredicateExpression(new NameQualifier(keywords)));
    }

    private void updateFilteredTaskList(Expression expression) {
        filteredTasks.setPredicate(expression::satisfies);
    }
    
    // @@author A0147944U
    /**
     * Select sorting method based on keyword
     * 
     * @param keyword keyword to sort tasks by
     */
    public void sortFilteredTaskList(String keyword) {
        if (keyword.equals("Deadline")) {
            taskManager.sortByDeadline();
        } else if (keyword.equals("Start Time")) {
            taskManager.sortByStartTime();
        } else if (keyword.equals("End Time")) {
            taskManager.sortByEndTime();
        } else if (keyword.equals("Completed")) {
            taskManager.sortByDoneStatus();
        } else if (keyword.equals("Name")) {
            taskManager.sortByName();
        } else {
            taskManager.sortByDefaultRules();
        }
        // Save data in that order
        indicateTaskManagerChanged();
    }
    
    /**
     * Updates sorting method in config based on keyword
     * 
     * @param keyword keyword to sort tasks by
     */
    @Override
    public void saveCurrentSortPreference(String keyword) {
        Config config = new Config();
        File configFile = new File("config.json");
        try {
            config = FileUtil.deserializeObjectFromJsonFile(configFile, Config.class);
        } catch (IOException e) {
            logger.warning("Error reading from config file " + "config.json" + ": " + e);
            try {
                throw new DataConversionException(e);
            } catch (DataConversionException e1) {
                e1.printStackTrace();
            }
        }
        config.setsortPreference(keyword);
        try {
            ConfigUtil.saveConfig(config, "config.json");
        } catch (IOException e) {
            logger.warning("Error saving to config file : " + e);
            e.printStackTrace();
        }
    }
    
    /**
     * Automatically sorts tasks based on current sort preferences in config
     */
    public void autoSortBasedOnCurrentSortPreference() {
        Config config = new Config();
        File configFile = new File("config.json");
        try {
            config = FileUtil.deserializeObjectFromJsonFile(configFile, Config.class);
        } catch (IOException e) {
            logger.warning("Error reading from config file : " + "config.json " + e);
            try {
                throw new DataConversionException(e);
            } catch (DataConversionException e1) {
                e1.printStackTrace();
            }
        }
        String CurrentSortPreference = config.getsortPreference();
        if (!CurrentSortPreference.equals("None")) {
            sortFilteredTaskList(CurrentSortPreference);
        }
    }
    // @@author
    
    //========== Inner classes/interfaces used for filtering ==================================================

    interface Expression {
        boolean satisfies(ReadOnlyTask task);
        String toString();
    }

    private class PredicateExpression implements Expression {

        private final Qualifier qualifier;

        PredicateExpression(Qualifier qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public boolean satisfies(ReadOnlyTask task) {
            return qualifier.run(task);
        }

        @Override
        public String toString() {
            return qualifier.toString();
        }
    }

    interface Qualifier {
        boolean run(ReadOnlyTask task);
        String toString();
    }

    private class NameQualifier implements Qualifier {
        private Set<String> nameKeyWords;

        NameQualifier(Set<String> nameKeyWords) {
            this.nameKeyWords = nameKeyWords;
        }

        @Override
        public boolean run(ReadOnlyTask task) {
            
            String name = task.getAsText().toLowerCase();
            
            return nameKeyWords.stream()
                    .filter(keyword -> name.indexOf(keyword.toLowerCase())>=0)
                    .findAny()
                    .isPresent();
        }

        @Override
        public String toString() {
            return "name=" + String.join(", ", nameKeyWords);
        }
    }

}
