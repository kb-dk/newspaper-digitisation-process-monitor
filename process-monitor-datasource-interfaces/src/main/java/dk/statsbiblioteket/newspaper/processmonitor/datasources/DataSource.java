package dk.statsbiblioteket.newspaper.processmonitor.datasources;

import java.util.List;
import java.util.Map;

/**
 * Interface the the process Monitor data sources.
 * Implementations of this class must have a no-args constructor. All properties must be set by getters and setters.
 */
public interface DataSource {

    /**
     * Returns true, if the batch id includes the run nr, and false if not.
     *
     * @return as above
     */
    boolean isRunNrInBatchID();

    /**
     * Get all batches matched by the filters.
     *
     * @param includeDetails should the field "details" be set on the events. This can be an expensive operation, if
     *                       many batches are returned
     * @param filters        the map of filters. Name/value pairs.
     * @return a List of Batch objects
     */
    List<Batch> getBatches(boolean includeDetails, Map<String, String> filters);

    /**
     * Get information about a specific batch
     *
     * @param batchID        the id of the specific batch
     * @param includeDetails should the field "details" be set on the events.
     * @return the Batch object
     */
    Batch getBatch(String batchID, boolean includeDetails);

    /**
     * Get information about the specific event on the specific batch
     *
     * @param batchID        the batch id
     * @param eventID        the event id
     * @param includeDetails should the field "details" be set on the event.
     * @return the Specific event
     */
    Event getBatchEvent(String batchID, String eventID, boolean includeDetails);

}
