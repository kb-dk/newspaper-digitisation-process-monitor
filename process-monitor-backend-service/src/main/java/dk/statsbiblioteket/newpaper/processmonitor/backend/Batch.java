package dk.statsbiblioteket.newpaper.processmonitor.backend;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Batch {

    private String batchID;
    private Map<String, Event> events;
        
    public String getBatchID() {
        return batchID;
    }
    
    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }
    
    public Map<String, Event> getEvents() {
        return events;
    }
    
    public void setEvents(Map<String, Event> events) {
        this.events = events;
    }
}
