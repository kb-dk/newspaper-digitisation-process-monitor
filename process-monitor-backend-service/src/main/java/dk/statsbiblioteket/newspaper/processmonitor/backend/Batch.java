package dk.statsbiblioteket.newspaper.processmonitor.backend;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;
import java.util.Map;

@XmlRootElement
public class Batch {

    private String batchID;
    private int roundTripNumber;
    private String domsID;
    private String avisID;
    private Date startDate;
    private Date endDate;
    private Map<String, Event> events;
    private int numberOfPages;
    private int numberOfUnmatched;

    @XmlElement(name = "batchID")
    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }
    
    @XmlElement(name = "avisID")
    public String getAvisID() {
        return avisID;
    }

    public void setAvisID(String avisID) {
        this.avisID = avisID;
    }

    @XmlElement(name = "startDate")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @XmlElement(name = "endDate")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    @XmlElement(name = "numberOfPages")
    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    @XmlElement(name = "domsID")
    public String getDomsID() {
        return domsID;
    }

    public void setDomsID(String domsID) {
        this.domsID = domsID;
    }

    @Override
    public String toString() {
        return "Batch{" +
               "batchID='" + batchID + '\'' +
               ", roundTripNumber=" + roundTripNumber +
               ", domsID='" + domsID + '\'' +
               ", avisID='" + avisID + '\'' +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               ", events=" + events +
               ", numberOfPages=" + numberOfPages +
               ", numberOfUnmatched=" + numberOfUnmatched +
               '}';
    }

    public void setNumberOfUnmatched(int numberOfUnmatched) {
        this.numberOfUnmatched = numberOfUnmatched;
    }

    public int getNumberOfUnmatched() {
        return numberOfUnmatched;
    }
}
