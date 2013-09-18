package dk.statsbiblioteket.newspaper.processmonitor.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.statsbiblioteket.newspaper.processmonitor.datasources.NotFoundException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service class to expose retrieval of Batch (@see Batch) and Events (@see Event) objects for monitoring progress and state
 */

@Component
@Path("/")
public class Batches {

    @Autowired
    private DataSourceCombiner dataSource;

    /**
     * Retrieves a list of all known Batch objects (@see Batch).
     *
     * @param details If true, will also include the available details for each event in the Batch objects. Defaults to false.
     * @return List<Batch> as JSON data.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Batch> getBatches(@QueryParam("details") @DefaultValue("false") boolean details) {
        return convertBatchList(dataSource.getAsOneDataSource().getBatches(details, null));
    }

    private List<Batch> convertBatchList(List<dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch> batches) {
        ArrayList<Batch> result = new ArrayList<Batch>(batches.size());
        for (dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch batch : batches) {
            result.add(convert(batch));
        }
        return result;
    }

    /**
     * Retrieves a specific batch given it's ID.
     *
     * @param batchID The ID of the specific batch
     * @param details If true, will also include the available details for each event in the Batch. Defaults to false.
     * @return Batch as JSON Object
     */
    @GET
    @Path("{batchID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Batch getSpecificBatch(@PathParam("batchID") String batchID,
                                  @QueryParam("details") @DefaultValue("false") boolean details) {
        try {
            return convert(dataSource.getAsOneDataSource().getBatch(batchID, details));
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Failed to get batch with ID: " + batchID)
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
    }

    private Batch convert(dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch batch) {
        Batch result = new Batch();
        result.setBatchID(batch.getBatchID());
        result.setEvents(convert(batch.getEventList()));
        return result;
    }

    private Map<String, Event> convert(List<dk.statsbiblioteket.newspaper.processmonitor.datasources.Event> eventList) {
        Map<String, Event> result = new HashMap<String, Event>(eventList.size());
        for (dk.statsbiblioteket.newspaper.processmonitor.datasources.Event event : eventList) {
            result.put(event.getEventID(), convert(event));
        }
        return result;
    }

    /**
     * Retrieves a specific Event for a specific Batch.
     *
     * @param batchID The ID of the specific batch
     * @param eventID The ID of the specific event
     * @param details If true, will also include the available details. Defaults to false.
     */
    @GET
    @Path("{batchID}/{eventID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Event getSpecificBatchEvent(@PathParam("batchID") String batchID, @PathParam("eventID") String eventID,
                                       @QueryParam("details") @DefaultValue("false") boolean details) {
        try {
            return convert(dataSource.getAsOneDataSource().getBatchEvent(batchID, eventID, details));
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Failed to get event with ID: " + eventID + " from batch with ID: " + batchID)
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
    }

    private Event convert(dk.statsbiblioteket.newspaper.processmonitor.datasources.Event batchEvent) {
        Event result = new Event();
        result.setDetails(batchEvent.getDetails());
        result.setSuccess(batchEvent.isSuccess());
        return result;
    }

}
