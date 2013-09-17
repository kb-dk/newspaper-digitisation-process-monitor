package dk.statsbiblioteket.newspaper.processmonitor.datasources;

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
