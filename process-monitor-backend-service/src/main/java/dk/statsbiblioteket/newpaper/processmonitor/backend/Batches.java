package dk.statsbiblioteket.newpaper.processmonitor.backend;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/")
public class Batches {

    private List<Batch> dummyBatches; 
    
    public Batches() {
        Event e1 = new Event();
        e1.setEventID("foo");
        e1.setSuccess(true);
        
        Event e2 = new Event();
        e2.setEventID("bar");
        e2.setSuccess(false);
        
        Event e3 = new Event();
        e3.setEventID("baz");
        e3.setSuccess(true);
        List<Event> b1Events = new ArrayList<Event>();
        b1Events.add(e1);
        b1Events.add(e2);
        b1Events.add(e3);
        
        Batch b1 = new Batch();
        b1.setBatchID("hans");
        
        Event e4 = new Event();
        e4.setEventID("foo");
        e4.setSuccess(true);
        
        Event e5 = new Event();
        e5.setEventID("bar");
        e5.setSuccess(false);
        
        Event e6 = new Event();
        e6.setEventID("baz");
        e6.setSuccess(false);
        
        List<Event> b2Events = new ArrayList<Event>();
        b2Events.add(e4);
        b2Events.add(e5);
        b2Events.add(e6);
        
        Batch b2 = new Batch();
        b2.setBatchID("bjarne");
        
        dummyBatches = new ArrayList<Batch>();
        dummyBatches.add(b1);
        dummyBatches.add(b2);
    } 
    
    
    @GET
    @Path("/")
    public List<Batch> getBatches() {
        return dummyBatches;
    }
    
    @GET
    @Path("{batchID}")
    public Batch getSpecificBatch(@PathParam("batchID") String batchID) {
        Batch batch = null;
        for(Batch b : dummyBatches) {
            if(b.getBatchID().equals(batchID)) {
                batch = b;
            }
        }
        return batch;
    }
    
    @GET
    @Path("{batchID}/{eventID}")
    public Event getSpecificBatchEvent(@PathParam("batchID") String batchID, @PathParam("eventID") String eventID) {
        Event event = null;
        for(Batch b : dummyBatches) {
            if(b.getBatchID().equals(batchID)) {
                for(Event e : b.getEvents()) {
                    if(e.getEventID().equals(eventID)) {
                        event = e;
                    }
                }
            }
        }       
        return event;
    }
    

}
