package seedu.task.model.task;

import seedu.task.model.tag.UniqueTagList;

/**
 * A read-only immutable interface for a Task in the task manager.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyTask {

    Name getName();
    StartTime getStartTime();
    EndTime getEndTime();
    Deadline getDeadline();
    Status getStatus();
    Recurring getRecurring();

    /**
     * The returned TagList is a deep copy of the internal TagList,
     * changes on the returned list will not affect the task internal tags.
     */
    UniqueTagList getTags();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyTask other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName()) // state checks here onwards
                && other.getStartTime().equals(this.getStartTime())
                && other.getEndTime().equals(this.getEndTime())
                && other.getDeadline().equals(this.getDeadline())
                && other.getRecurring().equals(this.getRecurring())
                && other.getTags().equals(this.getTags()));
    }

    /**
     * Formats the task as text, showing all task details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName());
        
        if (!getStartTime().toString().isEmpty()) {
            builder.append(" \nStart time: ").append(getStartTime());
        }
        if (!getEndTime().toString().isEmpty()) {       
            builder.append(" \nEnd time: ").append(getEndTime());
        }
        if (!getDeadline().toString().isEmpty()) {       
            builder.append(" \nDeadline: ").append(getDeadline());
        }
        if (!getRecurring().toString().equals("false")) {       
            builder.append(" \nRecurring: ").append(getDeadline());
        }
        if (!getTags().toSet().isEmpty()) {       
            builder.append(" \nTags: ");
            getTags().forEach(builder::append);
        }
        
        return builder.toString();
    }

    /**
     * Returns a string representation of this Task tags
     */
    default String tagsString() {
        final StringBuffer buffer = new StringBuffer();
        final String separator = ", ";
        getTags().forEach(tag -> buffer.append(tag).append(separator));
        if (buffer.length() == 0) {
            return "";
        } else {
            return buffer.substring(0, buffer.length() - separator.length());
        }
    }

}