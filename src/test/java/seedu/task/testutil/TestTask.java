package seedu.task.testutil;

import seedu.task.model.tag.UniqueTagList;
import seedu.task.model.task.*;

/**
 * A mutable task object. For testing only.
 */
public class TestTask implements ReadOnlyTask {

    private Name name;
    private Deadline location;
    private EndTime endTime;
    private StartTime startTime;
    private UniqueTagList tags;
    private Status status;

    public TestTask() {
        tags = new UniqueTagList();
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setTask(Deadline location) {
        this.location = location;
    }

    public void setEndTime(EndTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(StartTime startTime) {
        this.startTime = startTime;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public StartTime getStartTime() {
        return startTime;
    }

    @Override
    public EndTime getEndTime() {
        return endTime;
    }

    @Override
    public Deadline getDeadline() {
        return location;
    }

    @Override
    public UniqueTagList getTags() {
        return tags;
    }
    
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return getAsText();
    }

    public String getAddCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append("add " + this.getName().fullName + "");
        if(!this.getStartTime().value.isEmpty()) {
        	sb.append(", from " + this.getStartTime().value);
        }
        if(!this.getEndTime().value.isEmpty()) {
        sb.append(" to " + this.getEndTime().value + "");
        }
        if(!this.getDeadline().value.isEmpty()) {
        sb.append(" by " + this.getDeadline().value + " ");
        }
        this.getTags().getInternalList().stream().forEach(s -> sb.append("#" + s.tagName + " "));
        return sb.toString();
    }

   
}
