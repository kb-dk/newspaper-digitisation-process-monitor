package dk.statsbiblioteket.newspaper.processmonitor.backend;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class Batch {

    private String batchID;
    private int roundTripNumber;
    private Map<String, Event> events;

    @XmlElement(name = "batchID")
    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    @XmlElement(name = "events")
    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Event> events) {
        this.events = events;
    }

    @XmlElement(name = "roundTripNumber")
    public int getRoundTripNumber() {
        return roundTripNumber;
    }

    public void setRoundTripNumber(int roundTripNumber) {
        this.roundTripNumber = roundTripNumber;
    }
}
