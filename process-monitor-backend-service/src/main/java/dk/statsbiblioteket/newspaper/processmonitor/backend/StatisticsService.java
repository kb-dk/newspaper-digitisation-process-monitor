package dk.statsbiblioteket.newspaper.processmonitor.backend;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
@Path("/stats/")
public class StatisticsService {

    private String statisticsBaseUrl;
    
    public String getStatisticsBaseUrl() {
        return statisticsBaseUrl;
    }
    
    @Resource(name = "statisticsBaseUrl")
    public void setStatisticsBaseUrl(String statisticsBaseUrl) {
        this.statisticsBaseUrl = statisticsBaseUrl;
    }
    
    @GET
    @Path("baseurl")
    @Produces(MediaType.TEXT_PLAIN)
    public String getBaseUrl() {
        return getStatisticsBaseUrl();
    }
}
