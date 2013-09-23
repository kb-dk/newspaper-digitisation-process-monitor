package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.EventID;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.NotFoundException;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.NotWorkingProperlyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Scope(value = "request")
public class DataSourceCombiner implements DataSource {

    final static Logger logger = LoggerFactory.getLogger(DataSourceCombiner.class);


    private List<DataSource> dataSources;


    public List<DataSource> getDataSources() {
        return dataSources;
    }

    @Resource(name = "dataSourcesList")
    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    public boolean isRunNrInBatchID() {
        return true;
    }


    @Override
    public List<Batch> getBatches(boolean includeDetails, Map<String, String> filters) {
        logger.info("Call to getBatches with {} and filters {}", includeDetails, filters);
        Map<String, Batch> result = new HashMap<>();
        for (DataSource dataSource : dataSources) {
            try {
                mergeResults(result, dataSource.getBatches(includeDetails, filters));
            } catch (NotWorkingProperlyException e) {
                logger.error("Datasource failed", e);
                continue;
            }
        }
        return new ArrayList<>(result.values());
    }

    /**
     * Merge a list of batches into the given result map. Will do inplace modification of the map.
     *
     * @param result  the map of batches to merge the list into
     * @param batches the batches to merge into the map
     */
    private void mergeResults(Map<String, Batch> result, List<Batch> batches) {
        //For each batch in the lis
        for (Batch batch : batches) {
            //get the id
            String id = batch.getBatchID();
            //get the id already in the map
            //merge the batch from the list and the one from the map
            //put them back into the map
            result.put(id, mergeBatches(result.get(id), batch));
        }
    }

    /**
     * Merge two batches. If both is null, null is returned. If either is null, the other is returned.
     * For each event for the batches. If they do not overlap in eventID, both are included.
     * If they do overlap in eventID, the one from the batch with the highest runNr is used.
     * If the batches have equal runNr, the event from b is used.
     *
     * @param a the first batch
     * @param b the second batch
     * @return a new batch containing the merged information
     */
    private Batch mergeBatches(Batch a, Batch b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        Batch result = new Batch();
        result.setBatchID(a.getBatchID());
        boolean aIsHigher = a.getRunNr() > b.getRunNr();
        if (aIsHigher) {
            result.setRunNr(a.getRunNr());
        } else {
            result.setRunNr(b.getRunNr());
        }

        HashMap<EventID, Event> eventMap = new HashMap<EventID, Event>();
        for (Event event : a.getEventList()) {
            eventMap.put(event.getEventID(), event);
        }

        for (Event event : b.getEventList()) {
            Event existing = eventMap.get(event.getEventID());
            if (existing != null) {
                if (!aIsHigher) {
                    eventMap.put(event.getEventID(), event);
                }
            }

        }
        result.setEventList(new ArrayList<>(eventMap.values()));
        return result;
    }

    /**
     * Get a specific batch, by merging the results of each datasource
     *
     * @param batchID        the id
     * @param includeDetails should details be included
     * @return the specific batch
     * @throws NotFoundException
     */
    @Override
    public Batch getBatch(String batchID, boolean includeDetails) throws NotFoundException {
        //Create a list of batches, at most one from each datasource
        List<Batch> founds = new ArrayList<>();
        for (DataSource dataSource : dataSources) {
            try {
                founds.add(dataSource.getBatch(batchID, includeDetails));
            } catch (NotWorkingProperlyException e) {
                logger.error("Datasource failed", e);
                continue;
            } catch (NotFoundException e) {
                continue;
            }
        }
        //Merge all the found batches into one batch
        Batch result = null;
        for (Batch found : founds) {
            result = mergeBatches(result, found);
        }

        //return or throw
        if (result == null) {
            throw new NotFoundException();
        }
        return result;
    }


    /**
     * Get a specific event by quering the datasources until one of them provides the event
     *
     * @param batchID        the batch id
     * @param eventID        the event id
     * @param includeDetails should details be included
     * @return the specific event
     * @throws NotFoundException
     */
    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) throws NotFoundException {
        for (DataSource dataSource : dataSources) {
            Event result = null;
            try {
                result = dataSource.getBatchEvent(batchID, eventID, includeDetails);
            } catch (NotFoundException e) {
                continue;
            } catch (NotWorkingProperlyException e) {
                logger.error("Datasource failed", e);
                continue;
            }
            return result;
        }
        throw new NotFoundException();
    }


}
