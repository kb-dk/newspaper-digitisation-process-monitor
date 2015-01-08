package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.newspaper.processmonitor.stats.StatisticsService;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

public class ProcessMonitor extends ResourceConfig {
    public ProcessMonitor() {
        register(RequestContextFilter.class);
        register(BatchesService.class);
        register(JacksonFeature.class);
        register(CSVGenerator.class);
        register(StatisticsService.class);

    }
}
