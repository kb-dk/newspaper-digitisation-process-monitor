package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/17/13
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */

@Component
public class DataSourceCombiner {

    List<DataSource> dataSources;

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    @Resource(name = "dataSourcesList")
    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public DataSource getAsOneDataSource() {
        if (dataSources != null && !dataSources.isEmpty()) {
            return dataSources.get(0);
        }
        return new EmptySource();
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
