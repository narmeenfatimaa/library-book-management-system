package library;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The activity log of the library: a list of what users did (signed in, added a
 * book, issued a book, ...). Only the newest entries are kept.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class ActivityLog {

    /** The largest number of entries that are kept. */
    public static final int MAX_ENTRIES = 500;

    private final ArrayList<ActivityEntry> entries = new ArrayList<>();

    /**
     * Creates an empty log.
     */
    public ActivityLog() {
        // The log starts empty.
    }

    /**
     * Adds an entry with the current time.
     *
     * @param user    the username of the person who did the action
     * @param action  what was done
     * @param details more information about the action
     */
    public void record(String user, String action, String details) {
        entries.add(new ActivityEntry(LocalDateTime.now(), user, action, details));
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(0);
        }
    }

    /**
     * Returns the entries, newest first.
     *
     * @return a new list with the newest entry at the start
     */
    public ArrayList<ActivityEntry> getEntries() {
        ArrayList<ActivityEntry> newestFirst = new ArrayList<>();
        for (int i = entries.size() - 1; i >= 0; i--) {
            newestFirst.add(entries.get(i));
        }
        return newestFirst;
    }

    /**
     * Returns the number of entries.
     *
     * @return the number of entries in the log
     */
    public int size() {
        return entries.size();
    }

    /**
     * Replaces the log with the given entries, oldest first (used when a saved log is loaded).
     *
     * @param loaded the entries to load
     * @return the number of entries now in the log
     */
    public int loadEntries(Collection<ActivityEntry> loaded) {
        entries.clear();
        entries.addAll(loaded);
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(0);
        }
        return entries.size();
    }
}
