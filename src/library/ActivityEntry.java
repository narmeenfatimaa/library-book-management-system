package library;

import java.time.LocalDateTime;

/**
 * One line of the activity log: who did what, and when.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class ActivityEntry {

    private final LocalDateTime time;
    private final String user;
    private final String action;
    private final String details;

    /**
     * Creates an entry.
     *
     * @param time    when it happened
     * @param user    the username of the person who did it
     * @param action  what was done, for example "Added book"
     * @param details more information, for example the book ID and title
     */
    public ActivityEntry(LocalDateTime time, String user, String action, String details) {
        this.time = time;
        this.user = user;
        this.action = action;
        this.details = details;
    }

    /**
     * Returns the time of the action.
     *
     * @return the date and time
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Returns the person who did the action.
     *
     * @return the username
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns what was done.
     *
     * @return the action text
     */
    public String getAction() {
        return action;
    }

    /**
     * Returns more information about the action.
     *
     * @return the details text
     */
    public String getDetails() {
        return details;
    }
}
