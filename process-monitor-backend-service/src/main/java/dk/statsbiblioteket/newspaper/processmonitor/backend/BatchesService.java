package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.medieplatform.autonomous.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.List;

/**
 * Service class to expose retrieval of Batch and Events  objects for monitoring progress and state
 *
 * @see Batch
 * @see Event
 */

@Component
@Scope(value = "request")
@Path("/batches")
public class BatchesService {
    @Autowired
    private DataSourceCombiner dataSource;

    private List<BatchEnricher> enrichers;

    @Resource(name = "enricherList")
    public void setEnrichers(List<BatchEnricher> enrichers) {
        this.enrichers = enrichers;
    }

    public BatchesService() {
        System.out.println("Starting");
    }

    /**
     * Retrieves a list of all known Batch objects (@see Batch).
     *
     * @param details If true, will also include the available details for each event in the Batch objects. Defaults to false.
     * @return List<Batch> as JSON data or CSV file based on content negotiation.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, "text/csv"})
    public Response getBatches(@Context Request req, @QueryParam("details") @DefaultValue("false") boolean details) {
        MediaType types[] = {MediaType.APPLICATION_JSON_TYPE, new MediaType("text", "csv")};
        List<Variant> vars = Variant.mediaTypes(types).add().build();
        Variant var = req.selectVariant(vars);
        List<Batch> body = Converter.convertBatchList(dataSource.getBatches(details, null));
        enrichers.stream().forEach(enricher -> enricher.enrich(body));
        return Response.ok().entity(body).type(var.getMediaType()).build();
    }

    /**
     * Retrieves a specific batch given it's ID.
     *
     * @param batchID The ID of the specific batch
     * @param details If true, will also include the available details for each event in the Batch. Defaults to false.
     * @return Batch as JSON data or CSV file based on content negotiation.
     */
    @GET
    @Path("{batchID}")
    @Produces({MediaType.APPLICATION_JSON, "text/csv"})
    public Response getSpecificBatch(@Context Request req,
                                  @PathParam("batchID") String batchID,
                                  @QueryParam("details") @DefaultValue("false") boolean details) {
        MediaType types[] = {MediaType.APPLICATION_JSON_TYPE, new MediaType("text", "csv")};
        List<Variant> vars = Variant.mediaTypes(types).add().build();
        Variant var = req.selectVariant(vars);
        try {
            Batch body = Converter.convert(dataSource.getBatch(batchID, null, details));
            return Response.ok().entity(body).type(var.getMediaType()).build();
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
     *
     * @return Event as JSON data or CSV file based on content negotiation.
     */
    @GET
    @Path("{batchID}/roundtrips/{roundtripID}/events/{eventID}")
    @Produces({MediaType.APPLICATION_JSON, "text/csv"})
    public Response getSpecificBatchEvent(@Context Request req,
                                          @PathParam("batchID") String batchID,
                                          @PathParam("roundtripID") Integer roundtripID,
                                          @PathParam("eventID") String eventID,
                                          @QueryParam("details") @DefaultValue("false") boolean details) {
        MediaType types[] = {MediaType.APPLICATION_JSON_TYPE, new MediaType("text", "csv")};
        List<Variant> vars = Variant.mediaTypes(types).add().build();
        Variant var = req.selectVariant(vars);
        try {
            Event body = Converter.convert(dataSource.getBatchEvent(batchID, roundtripID, eventID, details));
            return Response.ok().entity(body).type(var.getMediaType()).build();
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
