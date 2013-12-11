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

    private String batchID = "4001";
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
    }

    @Test(groups = "integrationTest")
    public void testGetSingleBatch() {
        Batch result = Client.create(config).resource(integrationTestServer).path(batchID + "").get(Batch.class);
        Assert.assertEquals(result.getBatchID(), batchID, "This is not the batch we expected");
        Assert.assertTrue(result.getEvents().containsKey(eventID), "The batch does not contain the expected key");
        Assert.assertTrue(result.getEvents().get(eventID).isSuccess(), "The event is not marked as succesful");

    }


    @Test(groups = "integrationTest")
    public void testGetSingleEvent() {
        Event result = Client.create(config)
                .resource(integrationTestServer)
                .path(batchID + "")
                .path(eventID)
                .queryParam("details", "true")
                .get(Event.class);
        Assert.assertTrue(result.isSuccess(), "The event is not succesful");
    }

    @Test(groups = "integrationTest")
    public void testGetBatchesCSV() {
        String result = Client.create(config)
                .resource(integrationTestServer)
                .accept("text/csv")
                .get(String.class);
        result = cleanDate(result);
        Assert.assertTrue(result.startsWith("Batch;Roundtrip;Shipped_to_supplier;;;Data_Received;;;Metadata_Archived;;;Data_Archived;;;Structure_Checked;;;JPylyzed;;;Metadata_checked;;;auto-qa;;;manuel-qa;;;Approved;;;Received_from_supplier;;"),
                          "Expect a column header first");
        Assert.assertTrue(result.contains("4001;1;true;1970-01-01 01:00:00;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;"),
                          "The 4001 batch should be contained");
        Assert.assertTrue(result.contains("400022028241;1;"),
                          "The small test batch should be there");
    }

    @Test(groups = "integrationTest")
    public void testGetSingleBatchCSV() {
        String result = Client.create(config)
                .resource(integrationTestServer)
                .path(batchID + "")
                .accept("text/csv")
                .get(String.class);
        result = cleanDate(result);
        Assert.assertEquals(result,
                            "Batch;Roundtrip;Shipped_to_supplier;;;Data_Received;;;Metadata_Archived;;;Data_Archived;;;Structure_Checked;;;JPylyzed;;;Metadata_checked;;;auto-qa;;;manuel-qa;;;Approved;;;Received_from_supplier;;\n"
                                    + "4001;1;true;1970-01-01 01:00:00;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n");
    }

    private String cleanDate(String result) {
        return result.replaceAll("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}", "1970-01-01 01:00:00");
    }

    @Test(groups = "integrationTest")
    public void testGetSingleEventCSV() {
        String result = Client.create(config)
                .resource(integrationTestServer)
                .path(batchID + "")
                .path(eventID)
                .queryParam("details", "true")
                .accept("text/csv")
                .get(String.class);
        result = cleanDate(result);
        Assert.assertEquals(result, ";;true;1970-01-01 01:00:00;\n");
    }
}
