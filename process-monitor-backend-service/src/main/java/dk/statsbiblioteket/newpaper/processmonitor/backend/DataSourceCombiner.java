package dk.statsbiblioteket.newpaper.processmonitor.backend;

import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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
        return null;
    }
}
