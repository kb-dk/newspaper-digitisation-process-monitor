package dk.statsbiblioteket.newspaper.processmonitor.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility class for converting between datasource structures and json structures
 */
public class Converter {

    /**
     * Convert a list of batches from datasource to backend
     *
     * @param batches the batches from a datasource
     * @return the batches in the backend structure
     */
    static List<Batch> convertBatchList(List<dk.statsbiblioteket.medieplatform.autonomous.Batch> batches) {
        if (batches == null) {
            return null;
        }
        List<Batch> result = new ArrayList<>(batches.size());
        for (dk.statsbiblioteket.medieplatform.autonomous.Batch batch : batches) {
            result.add(convert(batch));
        }
        return result;
    }

    /**
     * Convert a batch
     *
     * @param batch the batch as datasource structure
     * @return the batch as backend
     */
    static Batch convert(dk.statsbiblioteket.medieplatform.autonomous.Batch batch) {
        if (batch == null) {
            return null;
        }
        Batch result = new Batch();
        result.setBatchID(batch.getBatchID());
        result.setRoundTripNumber(batch.getRoundTripNumber());
        result.setEvents(convert(batch.getEventList()));
        result.setDomsID(batch.getDomsID());
        //result = EventCleaner.cleanBatch(result);

        return result;
    }

    /**
     * Convert an event list
     *
     * @param eventList as datasource
     * @return as backend
     */
    private static Map<String, Event> convert(List<dk.statsbiblioteket.medieplatform.autonomous.Event> eventList) {
        if (eventList == null) {
            return null;
        }
        Map<String, Event> result = new HashMap<>(eventList.size());
        for (dk.statsbiblioteket.medieplatform.autonomous.Event event : eventList) {
            result.put(event.getEventID().toString(), convert(event));
        }
        return result;
    }

    /**
     * convert an event
     *
     * @param batchEvent as datasource
     * @return as backend
     */
    static Event convert(dk.statsbiblioteket.medieplatform.autonomous.Event batchEvent) {
        if (batchEvent == null) {
            return null;
        }
        Event result = new Event();
        result.setDetails(batchEvent.getDetails());
        result.setSuccess(batchEvent.isSuccess());
        result.setDate(batchEvent.getDate());
        return result;
    }
}
