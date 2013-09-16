package dk.statsbiblioteket.newspaper.processMonitor.datasources;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/16/13
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
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
