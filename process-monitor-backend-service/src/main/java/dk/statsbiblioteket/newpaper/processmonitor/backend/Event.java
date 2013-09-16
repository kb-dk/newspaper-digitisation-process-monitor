package dk.statsbiblioteket.newpaper.processmonitor.backend;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Event {

    String eventID;
    boolean passed;
    String details;
    
    public String getEventID() {
        return eventID;
    }
    
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
    
    public boolean isPassed() {
        return passed;
    }
    
    public void setPassed(boolean passed) {
        this.passed = passed;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
}
