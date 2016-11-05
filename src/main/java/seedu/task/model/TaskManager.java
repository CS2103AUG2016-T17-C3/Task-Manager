package seedu.task.model;

import javafx.collections.ObservableList;
import seedu.task.model.tag.Tag;
import seedu.task.model.tag.UniqueTagList;
import seedu.task.model.task.ReadOnlyTask;
import seedu.task.model.task.Task;
import seedu.task.model.task.UniqueTaskList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wraps all data at the task manager.
 * Duplicates are not allowed (by .equals comparison)
 */
public class TaskManager implements ReadOnlyTaskManager {

    private final UniqueTaskList tasks = new UniqueTaskList();
    private final UniqueTagList tags = new UniqueTagList();

    public TaskManager() {
    }

    /**
     * Tasks and Tags are copied into this TaskManager
     */
    public TaskManager(ReadOnlyTaskManager toBeCopied) {
        this(toBeCopied.getUniqueTaskList(), toBeCopied.getUniqueTagList());
    }

    /**
     * Persons and Tags are copied into this TaskManager
     */
    public TaskManager(UniqueTaskList tasks, UniqueTagList tags) {
        resetData(tasks.getInternalList(), tags.getInternalList());
    }

    public static ReadOnlyTaskManager getEmptyTaskManager() {
        return new TaskManager();
    }

    //// list overwrite operations

    public ObservableList<Task> getTasks() {
        return tasks.getInternalList();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks.getInternalList().setAll(tasks);
    }

    public void setTags(Collection<Tag> tags) {
        this.tags.getInternalList().setAll(tags);
    }

    public void resetData(Collection<? extends ReadOnlyTask> newTasks, Collection<Tag> newTags) {
        setTasks(newTasks.stream().map(Task::new).collect(Collectors.toList()));
        setTags(newTags);
    }

    public void resetData(ReadOnlyTaskManager newData) {
        resetData(newData.getTaskList(), newData.getTagList());
    }

    //// task-level operations

    /**
     * Adds a task to the task manager. Also checks the new task tags and
     * updates {@link #tags} with any new tags found, and updates the Tag
     * objects in the task to point to those in {@link #tags}.
     *
     * @throws UniqueTaskList.DuplicateTaskException
     *             if an equivalent task already exists.
     */
    public void addTask(Task p) throws UniqueTaskList.DuplicateTaskException {
        syncTagsWithMasterList(p);
        tasks.add(p);
    }

    // @@author A0147335E-reused
    public void addTask(int index, Task p) throws UniqueTaskList.DuplicateTaskException {
        syncTagsWithMasterList(p);
        tasks.add(index, p);
    }

    // @@author
    /**
     * Ensures that every tag in this task: - exists in the master list
     * {@link #tags} - points to a Tag object in the master list
     */
    private void syncTagsWithMasterList(Task task) {
        final UniqueTagList taskTags = task.getTags();
        tags.mergeFrom(taskTags);

        // Create map with values = tag object references in the master list
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        for (Tag tag : tags) {
            masterTagObjects.put(tag, tag);
        }

        // Rebuild the list of task tags using references from the master list
        final Set<Tag> commonTagReferences = new HashSet<>();
        for (Tag tag : taskTags) {
            commonTagReferences.add(masterTagObjects.get(tag));
        }
        task.setTags(new UniqueTagList(commonTagReferences));
    }

    public boolean removeTask(ReadOnlyTask key) throws UniqueTaskList.TaskNotFoundException {
        if (tasks.remove(key)) {
            return true;
        } else {
            throw new UniqueTaskList.TaskNotFoundException();
        }
    }

    //// tag-level operations

    public void addTag(Tag t) throws UniqueTagList.DuplicateTagException {
        tags.add(t);
    }

    //// util methods

    @Override
    public String toString() {
        return tasks.getInternalList().size() + " tasks, " + tags.getInternalList().size() + " tags";
        // TODO: refine later
    }

    @Override
    public List<ReadOnlyTask> getTaskList() {
        return Collections.unmodifiableList(tasks.getInternalList());
    }

    @Override
    public List<Tag> getTagList() {
        return Collections.unmodifiableList(tags.getInternalList());
    }

    @Override
    public UniqueTaskList getUniqueTaskList() {
        return this.tasks;
    }

    @Override
    public UniqueTagList getUniqueTagList() {
        return this.tags;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TaskManager // instanceof handles nulls
                        && this.tasks.equals(((TaskManager) other).tasks)
                        && this.tags.equals(((TaskManager) other).tags));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing
        // your own
        return Objects.hash(tasks, tags);
    }

    // @@author A0147944U
    //// sort methods

    /**
     * Tasks are sorted according to Deadline in ascending order
     */
    public void sortByDeadline() {
        this.tasks.getInternalList().sort(new Comparator<Task>() {
            @Override
            public int compare(Task one, Task other) {
                return other.getDeadline().compareTo(one.getDeadline());
            }
        });
    }

    /**
     * Tasks are sorted according to StartTime in ascending order
     */
    public void sortByStartTime() {
        this.tasks.getInternalList().sort(new Comparator<Task>() {
            @Override
            public int compare(Task one, Task other) {
                return one.getStartTime().compareTo(other.getStartTime());
            }
        });
    }

    /**
     * Tasks are sorted according to EndTime in ascending order
     */
    public void sortByEndTime() {
        this.tasks.getInternalList().sort(new Comparator<Task>() {
            @Override
            public int compare(Task one, Task other) {
                return one.getEndTime().compareTo(other.getEndTime());
            }
        });
    }

    /**
     * Tasks are sorted according to DoneStatus, starting with completed tasks
     */
    public void sortByDoneStatus() {
        this.tasks.getInternalList().sort(new Comparator<Task>() {
            @Override
            public int compare(Task one, Task other) {
                return one.getStatus().compareDoneStatusTo(other.getStatus());
            }
        });
    }

    /**
     * Tasks are sorted according to Name in ascending order
     */
    public void sortByName() {
        this.tasks.getInternalList().sort(new Comparator<Task>() {
            @Override
            public int compare(Task one, Task other) {
                return one.getName().compareTo(other.getName());
            }
        });
    }

    /**
     * Tasks are sorted according to these criteria in the order: Incomplete
     * tasks, Floating tasks, Older tasks, lastly Name in ascending order.
     */
    public void sortByDefaultRules() {
        this.tasks.getInternalList().sort(new Comparator<Task>() {
            @Override
            public int compare(Task one, Task other) {
                // Incomplete tasks first
                int statusResult = one.getStatus().compareDoneStatusTo(other.getStatus());
                // Older tasks first
                int deadlineResult = one.getDeadline().compareTo(other.getDeadline());
                int startTimeResult = one.getStartTime().compareTo(other.getStartTime());
                int timeResult;
                if (deadlineResult == 0 && startTimeResult == 0) {
                    timeResult = 0;
                } else if (!one.getDeadline().toString().equals("") && !other.getStartTime().toString().equals("")) {
                    timeResult = one.getDeadline().compareTo(other.getStartTime());
                } else if (!one.getStartTime().toString().equals("") && !other.getDeadline().toString().equals("")) {
                    timeResult = one.getStartTime().compareTo(other.getDeadline());
                } else if (!(deadlineResult == 0)) {
                    timeResult = deadlineResult;
                } else {
                    timeResult = startTimeResult;
                }
                // Name in ascending order
                int nameResult = one.getName().compareTo(other.getName());

                if (statusResult == 0) {
                    if (timeResult == 0) {
                        return nameResult;
                    } else {
                        return timeResult;
                    }
                } else {
                    return statusResult;
                }
            }
        });
    }
    // @@author

}
