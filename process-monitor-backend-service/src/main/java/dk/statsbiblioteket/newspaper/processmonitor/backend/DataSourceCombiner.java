package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.NotFoundException;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.NotWorkingProperlyException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


@Component
@Scope(value = "request")
public class DataSourceCombiner implements DataSource {

    List<DataSource> dataSources_withRunNr;
    List<DataSource> dataSources_withoutRunNr;

    public List<DataSource> getDataSources() {
        ArrayList<DataSource> result = new ArrayList<>(dataSources_withoutRunNr);
        result.addAll(dataSources_withRunNr);
        return result;
    }

    @Resource(name = "dataSourcesList")
    public void setDataSources(List<DataSource> dataSources) {
        dataSources_withoutRunNr = new ArrayList<>();
        dataSources_withRunNr = new ArrayList<>();
        for (DataSource dataSource : dataSources) {
            try {
                if (dataSource.isRunNrInBatchID()) {
                    dataSources_withRunNr.add(dataSource);
                } else {
                    dataSources_withoutRunNr.add(dataSource);
                }
            } catch (NotWorkingProperlyException e) {
                continue;
            }
        }
    }

    @Override
    public boolean isRunNrInBatchID() {
        return true;
    }

    @Override
    public List<Batch> getBatches(boolean includeDetails, Map<String, String> filters) {
        Map<String, Batch> resultsWithRunNr = new HashMap<>();
        Map<String, Batch> resultsWithoutRunNr = new HashMap<>();

        for (DataSource dataSource : dataSources_withoutRunNr) {
            try {
                resultsWithoutRunNr.putAll(toBatchMap(dataSource.getBatches(includeDetails, filters)));
            } catch (NotWorkingProperlyException e) {
                continue;
            }
        }
        for (DataSource dataSource : dataSources_withRunNr) {
            try {
                resultsWithRunNr.putAll(toBatchMap(dataSource.getBatches(includeDetails, filters)));
            } catch (NotWorkingProperlyException e) {
                continue;
            }
        }

        HashMap<String, Batch> combinedResult = new HashMap<>();
        HashSet<String> removals = new HashSet<>();
        for (Map.Entry<String, Batch> stringBatchEntry : resultsWithRunNr.entrySet()) {
            if (resultsWithoutRunNr.containsKey(stripRunNr(stringBatchEntry.getKey()))) {
                combinedResult.put(stringBatchEntry.getKey(),
                        mergeBatches(
                                resultsWithoutRunNr.get(stripRunNr(stringBatchEntry.getKey())),
                                stringBatchEntry.getValue()));
                removals.add(stripRunNr(stringBatchEntry.getKey()));
            }
        }
        for (Map.Entry<String, Batch> stringBatchEntry : resultsWithoutRunNr.entrySet()) {
            if (!removals.contains(stringBatchEntry.getKey())) {
                combinedResult.put(stringBatchEntry.getKey(), stringBatchEntry.getValue());
            }
        }
        return new ArrayList<>(combinedResult.values());
    }

    private Map<? extends String, ? extends Batch> toBatchMap(List<Batch> batches) {
        Map<String, Batch> results = new HashMap<>();
        for (Batch batch : batches) {
            results.put(batch.getBatchID(), batch);
        }
        return results;
    }

    public boolean batchIdEquals(String batchID_1, String batchID_2) {
        if (batchID_1.equals(batchID_2)) {
            return true;
        }
        if (batchID_1.equals(stripRunNr(batchID_2))) {
            return true;
        }
        if (stripRunNr(batchID_1).equals(batchID_2)) {
            return true;
        }
        if (stripRunNr(batchID_1).equals(stripRunNr(batchID_2))) {
            return true;
        }
        return false;
    }

    @Override
    public Batch getBatch(String batchID, boolean includeDetails) throws NotFoundException {
        if (batchID.contains("-")) {
            //we have run nr
            Batch result_with_run_nr = null;
            Batch result_without_run_nr = null;
            String batchID_without_runNr = stripRunNr(batchID);
            result_without_run_nr = getMergedBatch(batchID_without_runNr, includeDetails, dataSources_withoutRunNr);

            result_with_run_nr = getMergedBatch(batchID, includeDetails, dataSources_withRunNr);
            if (result_with_run_nr == null && result_without_run_nr == null) {
                throw new NotFoundException("Failed to find batch " + batchID);
            } else {
                if (result_with_run_nr == null) {
                    return result_without_run_nr;
                }
                if (result_without_run_nr == null) {
                    return result_with_run_nr;
                }
                return mergeBatches(result_without_run_nr, result_with_run_nr);
            }

        } else {
            return getMergedBatch(batchID, includeDetails, dataSources_withoutRunNr);
        }
    }

    private Batch getMergedBatch(String batchID, boolean includeDetails, List<DataSource> dataSources) {
        Batch result = null;
        for (DataSource dataSource : dataSources) {
            try {
                Batch temp = null;
                try {
                    temp = dataSource.getBatch(batchID, includeDetails);
                } catch (NotWorkingProperlyException e) {
                    continue;
                }
                if (result != null) {
                    result = mergeBatches(result, temp);
                } else {
                    result = temp;
                }
            } catch (NotFoundException e) {
                //continue
            }
        }
        return result;
    }

    private String stripRunNr(String batchID) {
        batchID = batchID.replaceFirst("^B", "");
        batchID = batchID.replaceFirst("-.*$", "");
        return batchID;
    }

    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) throws NotFoundException {
        for (DataSource dataSource : dataSources_withoutRunNr) {
            try {
                String strippedID = stripRunNr(batchID);
                return dataSource.getBatchEvent(strippedID, eventID, includeDetails);
            } catch (NotFoundException e) {
                //continiue
            } catch (NotWorkingProperlyException e) {
                continue;
            }
        }
        for (DataSource dataSource : dataSources_withRunNr) {
            try {
                return dataSource.getBatchEvent(batchID, eventID, includeDetails);
            } catch (NotFoundException e) {
                //continiue
            } catch (NotWorkingProperlyException e) {
                continue;
            }
        }

        throw new NotFoundException("Event not found for batch id " + batchID + " and event id " + eventID);
    }

    private Batch mergeBatches(Batch a, Batch b) {
        String id = (a.getBatchID().length() > b.getBatchID().length() ? a.getBatchID() : b.getBatchID());
        HashMap<String, Event> eventMap = new HashMap<String, Event>();
        for (Event event : a.getEventList()) {
            eventMap.put(event.getEventID(), event);
        }
        for (Event event : b.getEventList()) {
            eventMap.put(event.getEventID(), event);
        }
        Batch result = new Batch();
        result.setBatchID(id);
        result.setEventList(new ArrayList<>(eventMap.values()));
        return result;
    }


    private static class EmptySource implements DataSource {

        @Override
        public boolean isRunNrInBatchID() {
            return false;
        }

        @Override
        public List<Batch> getBatches(boolean includeDetails, Map<String, String> filters) {
            return new ArrayList<Batch>();
        }

        @Override
        public Batch getBatch(String batchID, boolean includeDetails) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
