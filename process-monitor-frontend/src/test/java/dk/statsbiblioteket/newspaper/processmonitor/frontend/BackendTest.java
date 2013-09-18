package dk.statsbiblioteket.newspaper.processmonitor.frontend;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import dk.statsbiblioteket.newspaper.processmonitor.backend.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.backend.Event;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class BackendTest {

    private DefaultClientConfig config;

    private String batchID = "hans";
    private String integrationTestServer;


    @BeforeClass(groups = "integrationTest")
    public void setup() {
        config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        integrationTestServer = "http://localhost:8081/process-monitor-frontend/batches/";
    }


    @Test(groups = "integrationTest")
    public void testGetBatches() {
        Batch[] result = Client.create(config).resource(integrationTestServer).get(Batch[].class);
        Assert.assertTrue(result.length > 1, "We expected more than one batch to be returned");

    }

    @Test(groups = "integrationTest")
    public void testGetSingleBatch() {
        Batch result = Client.create(config).resource(integrationTestServer).path(batchID).get(Batch.class);
        System.out.println(result);
        Assert.assertEquals(result.getBatchID(), batchID, "This is not the batch we expected");
        Assert.assertTrue(result.getEvents().containsKey("reels-sent"), "The batch does not contain the expected key");
        Assert.assertTrue(result.getEvents().get("reels-sent").isSuccess(), "The event is not marked as succesful");

    }


    @Test(groups = "integrationTest")
    public void testGetSingleEvent() {
        Event result = Client.create(config)
                .resource(integrationTestServer)
                .path(batchID)
                .path("reels-sent")
                .queryParam("details", "true")
                .get(Event.class);
        Assert.assertTrue(result.isSuccess(), "The event is not succesful");
        Assert.assertNotNull(result.getDetails(), "The event has no details");
    }

}