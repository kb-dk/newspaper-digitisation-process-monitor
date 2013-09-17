package dk.statsbiblioteket.newpaper.processmonitor.backend;

import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * Service class to expose retrieval of Batch (@see Batch) and Events (@see Event) objects for monitoring progress and state
 */
@Component
@Path("/")
public class Batches {

    @Resource(name = "dataSourceCombiner")
    private DataSourceCombiner combiner;


    public Batches() {
    }

    /**
     * Retrieves a list of all known Batch objects (@see Batch).
     *
     * @param details If true, will also include the available details for each event in the Batch objects. Defaults to false.
     * @return List<Batch> as JSON data.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Batch> getBatches(@QueryParam("details") @DefaultValue("false") boolean details) {
        return combiner.getAsOneDataSource().getBatches(details, null);
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
        return combiner.getAsOneDataSource().getBatch(batchID, details);
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
        return combiner.getAsOneDataSource().getBatchEvent(batchID, eventID, details);
    }


}
