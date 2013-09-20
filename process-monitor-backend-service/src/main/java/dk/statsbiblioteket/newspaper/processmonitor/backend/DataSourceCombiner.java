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
import java.util.List;
import java.util.Map;


@Component
@Scope(value = "request")
public class DataSourceCombiner implements DataSource {

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
        Map<String, Batch> result = new HashMap<>();
        for (DataSource dataSource : dataSources) {
            try {
                mergeResults(result, dataSource.getBatches(includeDetails, filters));
            } catch (NotWorkingProperlyException e) {
                continue;
            }
        }
        return new ArrayList<>(result.values());
    }

    private void mergeResults(Map<String, Batch> result, List<Batch> batches) {
        for (Batch batch : batches) {
            String id = batch.getBatchID();
            result.put(id, mergeBatches(result.get(id), batch));
        }
    }

    private Batch mergeBatches(Batch a, Batch b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        Batch result = new Batch();
        result.setBatchID(a.getBatchID());


        HashMap<String, Event> eventMap = new HashMap<String, Event>();
        for (Event event : a.getEventList()) {
            eventMap.put(event.getEventID(), event);
        }
        boolean aIsHigher = a.getRunNr() > b.getRunNr();
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


    @Override
    public Batch getBatch(String batchID, boolean includeDetails) throws NotFoundException {
        List<Batch> founds = new ArrayList<>();
        for (DataSource dataSource : dataSources) {
            try {
                founds.add(dataSource.getBatch(batchID, includeDetails));
            } catch (NotWorkingProperlyException e) {
                continue;
            }
        }
        Batch result = null;
        for (Batch found : founds) {
            result = mergeBatches(result, found);
        }
        if (result == null) {
            throw new NotFoundException();
        }
        return result;
    }


    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) throws NotFoundException {
        for (DataSource dataSource : dataSources) {
            Event result = null;
            try {
                result = dataSource.getBatchEvent(batchID, eventID, includeDetails);
            } catch (NotWorkingProperlyException e) {
                continue;
            }
            return result;
        }
        throw new NotFoundException();
    }


}
