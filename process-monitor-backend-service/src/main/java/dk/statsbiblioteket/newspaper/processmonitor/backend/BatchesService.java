package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.newspaper.processmonitor.datasources.EventID;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


/**
 * Service class to expose retrieval of Batch and Events  objects for monitoring progress and state
 *
 * @see Batch
 * @see Event
 */

@Component
@Scope(value = "request")
@Path("/")
public class BatchesService {

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
        return Converter.convertBatchList(dataSource.getBatches(details, null));
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
    public Batch getSpecificBatch(@PathParam("batchID") long batchID,
                                  @QueryParam("details") @DefaultValue("false") boolean details) {
        try {
            return Converter.convert(dataSource.getBatch(batchID, null, details));
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Failed to get batch with ID: " + batchID)
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
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
    public Event getSpecificBatchEvent(@PathParam("batchID") long batchID, @PathParam("eventID") String eventID,
                                       @QueryParam("details") @DefaultValue("false") boolean details) {
        try {
            EventID id = EventID.valueOf(eventID);
            return Converter.convert(dataSource.getBatchEvent(batchID,null, id, details));
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Failed to get event with ID: " + eventID + " from batch with ID: " + batchID + ". The EventID is not known by the system")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Failed to get event with ID: " + eventID + " from batch with ID: " + batchID)
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
    }

}
