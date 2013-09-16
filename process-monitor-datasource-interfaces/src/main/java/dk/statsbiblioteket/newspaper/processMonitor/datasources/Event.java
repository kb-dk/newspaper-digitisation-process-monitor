package dk.statsbiblioteket.newspaper.processMonitor.datasources;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/16/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Event {
    private String eventID;
    private boolean succes;
    private String details;

    public Event() {
    }

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
