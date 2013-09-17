package dk.statsbiblioteket.newspaper.processmonitor.backend;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Event {

    private boolean success;
    private String details;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean passed) {
        this.success = passed;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
