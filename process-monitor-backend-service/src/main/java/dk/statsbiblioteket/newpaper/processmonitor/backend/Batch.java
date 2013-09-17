package dk.statsbiblioteket.newpaper.processmonitor.backend;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Batch {

    private String batchID;
    private List<Event> events;
        
    public String getBatchID() {
        return batchID;
    }
    
    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }
    
    public List<Event> getEvents() {
        return events;
    }
    
    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
