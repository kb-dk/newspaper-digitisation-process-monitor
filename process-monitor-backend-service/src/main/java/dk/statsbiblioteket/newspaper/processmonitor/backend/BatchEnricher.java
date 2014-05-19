package dk.statsbiblioteket.newspaper.processmonitor.backend;

import java.util.List;

/**
 * Interface the the process Monitor for enriching batches. 
 * 
 * Implementations of this class must have a no-args constructor. All properties must be set by getters and setters.
 */
public interface BatchEnricher {

    /**
     * Enriches a list of batches with additional information. 
     */
    public abstract List<Batch> enrich(List<Batch> batches);

}