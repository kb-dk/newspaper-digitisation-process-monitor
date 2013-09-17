package dk.statsbiblioteket.newspaper.processmonitor.datasources;

import java.util.List;

public class Batch {

    private String batchID;
    private List<Event> eventList;

    public Batch() {
    }

    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}
