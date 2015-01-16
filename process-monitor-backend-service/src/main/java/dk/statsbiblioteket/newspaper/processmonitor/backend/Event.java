package dk.statsbiblioteket.newspaper.processmonitor.backend;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Event {

    private boolean success;
    private String details;
    private Date date;
    private String duration;

    @XmlElement(name = "success")
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean passed) {
        this.success = passed;
    }

    @XmlElement(name = "details")
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @XmlElement(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @XmlElement(name = "duration")
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Event{" +
               "success=" + success +
               ", details='" + details + '\'' +
               ", date=" + date +
               ", duration='" + duration + '\'' +
               '}';
    }
}
