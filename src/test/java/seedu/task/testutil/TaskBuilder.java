package seedu.task.testutil;

import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.model.tag.Tag;
import seedu.task.model.task.*;

/**
 *
 */
public class TaskBuilder {

    private TestTask task;

    public TaskBuilder() {
        this.task = new TestTask();
    }

    public TaskBuilder withName(String name) throws IllegalValueException {
        this.task.setName(new Name(name));
        return this;
    }

    public TaskBuilder withTags(String ... tags) throws IllegalValueException {
        for (String tag: tags) {
            task.getTags().add(new Tag(tag));
        }
        return this;
    }

    public TaskBuilder withDeadline(String deadline) throws IllegalValueException {
        this.task.setTask(new Deadline(deadline));
        return this;
    }

    public TaskBuilder withStartTime(String startTime) throws IllegalValueException {
        this.task.setStartTime(new StartTime(startTime));
        return this;
    }

    public TaskBuilder withEndTime(String endTime) throws IllegalValueException {
        this.task.setEndTime(new EndTime(endTime));
        return this;
    }
    
    public TaskBuilder withStatus(boolean isDone, boolean isOverdue, boolean isNewlyAdded) throws IllegalValueException {
        this.task.setStatus(new Status(isDone, isOverdue, isNewlyAdded));
        return this;
    }
    
    public TaskBuilder withRecurring(String recurring) throws IllegalValueException {
        this.task.setRecurring(new Recurring(recurring));
        return this;
    }

    public TestTask build() {
        return this.task;
    }

}
