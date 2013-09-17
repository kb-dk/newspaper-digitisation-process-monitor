package dk.statsbiblioteket.newspaper.processmonitor.datasources;

/**
 * An event that have taken place on a batch
 */
public class Event {
    private String eventID;
    private boolean succes;
    private String details;

    /**
     * No-args constructor
     */
    public Event() {
    }

    /**
     * Get the event id. The event id
     *
     * @return
     */
    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
