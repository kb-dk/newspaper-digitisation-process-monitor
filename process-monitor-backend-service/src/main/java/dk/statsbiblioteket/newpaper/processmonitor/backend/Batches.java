package dk.statsbiblioteket.newpaper.processmonitor.backend;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 * Service class to expose retrieval of Batch (@see Batch) and Events (@see Event) objects for monitoring progress and state  
 */

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
    
    /**
     * Retrieves a list of all known Batch objects (@see Batch).
     * @param details If true, will also include the available details for each event in the Batch objects. Defaults to false.
     * @return List<Batch> as JSON data.   
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Batch> getBatches(@QueryParam("details") @DefaultValue("false") boolean details) {
        return dummyBatches;
    }
    
    /**
     * Retrieves a specific batch given it's ID. 
     * @param batchID The ID of the specific batch
     * @param details If true, will also include the available details for each event in the Batch. Defaults to false.
     * @return Batch as JSON Object
     */
    @GET
    @Path("{batchID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Batch getSpecificBatch(@PathParam("batchID") String batchID, 
            @QueryParam("details") @DefaultValue("false") boolean details) {
        Batch batch = null;
        for(Batch b : dummyBatches) {
            if(b.getBatchID().equals(batchID)) {
                batch = b;
            }
        }
        return batch;
    }
    
    /**
     * Retrieves a specific Event for a specific Batch.
     * @param batchID The ID of the specific batch
     * @param eventID The ID of the specific event
     * @param details If true, will also include the available details. Defaults to false.
     */
    @GET
    @Path("{batchID}/{eventID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Event getSpecificBatchEvent(@PathParam("batchID") String batchID, @PathParam("eventID") String eventID, 
            @QueryParam("details") @DefaultValue("false") boolean details) {
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
