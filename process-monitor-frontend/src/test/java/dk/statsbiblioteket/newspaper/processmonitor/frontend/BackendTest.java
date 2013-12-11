package dk.statsbiblioteket.newspaper.processmonitor.frontend;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import dk.statsbiblioteket.newspaper.processmonitor.backend.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.backend.Event;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

public class BackendTest {

    private DefaultClientConfig config;

    private long batchID = 4002l;
    private String eventID = "Shipped_to_supplier";
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
        System.out.println(Client.create(config).resource(integrationTestServer).get(String.class));

    }

    @Test(groups = "integrationTest", enabled = false)
    public void testGetSingleBatch() {
        Batch result = Client.create(config).resource(integrationTestServer).path(batchID + "").get(Batch.class);
        System.out.println(result);
        Assert.assertEquals(result.getBatchID(), batchID, "This is not the batch we expected");
        Assert.assertTrue(result.getEvents().containsKey(eventID), "The batch does not contain the expected key");
        Assert.assertTrue(result.getEvents().get(eventID).isSuccess(), "The event is not marked as succesful");

    }


    @Test(groups = "integrationTest", enabled = false)

    public void testGetSingleEvent() {
        Event result = Client.create(config)
                .resource(integrationTestServer)
                .path(batchID + "")
                .path(eventID)
                .queryParam("details", "true")
                .get(Event.class);
        Assert.assertTrue(result.isSuccess(), "The event is not succesful");
        //TODO the backends should, but does not guarantee, that details is not null when called with details=true
        //Assert.assertNotNull(result.getDetails(), "The event has no details");
    }

    @Test(groups = "integrationTest")
    public void testGetBatchesCSV() {
        String result = Client.create(config).resource(integrationTestServer).accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(result);
        //Assert.assertTrue(result.length > 1, "We expected more than one batch to be returned");
        //System.out.println(Client.create(config).resource(integrationTestServer).get(String.class));

    }

    @Test(groups = "integrationTest", enabled = false)
    public void testGetSingleBatchCSV() {
        String result = Client.create(config).resource(integrationTestServer).path(batchID + "").accept("text/csv").get(String.class);
        System.out.println(result);
        //Assert.assertEquals(result.getBatchID(), batchID, "This is not the batch we expected");
        //Assert.assertTrue(result.getEvents().containsKey(eventID), "The batch does not contain the expected key");
        //Assert.assertTrue(result.getEvents().get(eventID).isSuccess(), "The event is not marked as succesful");

    }


    @Test(groups = "integrationTest", enabled = false)

    public void testGetSingleEventCSV() {
        String result = Client.create(config)
                .resource(integrationTestServer)
                .path(batchID + "")
                .path(eventID)
                .queryParam("details", "true")
                .accept("text/csv")
                .get(String.class);
        System.out.println(result);
        //Assert.assertTrue(result.isSuccess(), "The event is not succesful");
    }
}
