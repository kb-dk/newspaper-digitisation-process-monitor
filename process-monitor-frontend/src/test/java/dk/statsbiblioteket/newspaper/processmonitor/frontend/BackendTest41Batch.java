package dk.statsbiblioteket.newspaper.processmonitor.frontend;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import dk.statsbiblioteket.newspaper.processmonitor.backend.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.backend.Event;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class BackendTest41Batch {

    public static final String HEADER
            = "Batch;Roundtrip;Avis id;Start date;End date;Pages;Manually_stopped;Manually_stopped_timestamp;Manually_stopped_duration;Shipped_to_supplier;Shipped_to_supplier_timestamp;Shipped_to_supplier_duration;Data_Received;Data_Received_timestamp;Data_Received_duration;Metadata_Archived;Metadata_Archived_timestamp;Metadata_Archived_duration;Data_Archived;Data_Archived_timestamp;Data_Archived_duration;Structure_Checked;Structure_Checked_timestamp;Structure_Checked_duration;JPylyzed;JPylyzed_timestamp;JPylyzed_duration;Histogrammed;Histogrammed_timestamp;Histogrammed_duration;Metadata_checked;Metadata_checked_timestamp;Metadata_checked_duration;Manual_QA_Flagged;Manual_QA_Flagged_timestamp;Manual_QA_Flagged_duration;Roundtrip_Approved;Roundtrip_Approved_timestamp;Roundtrip_Approved_duration;Dissemination_Copy_Generated;Dissemination_Copy_Generated_timestamp;Dissemination_Copy_Generated_duration;Dissemination_Editions_Generated;Dissemination_Editions_Generated_timestamp;Dissemination_Editions_Generated_duration;Metadata_Enriched;Metadata_Enriched_timestamp;Metadata_Enriched_duration;Cleaned_lesser_roundtrips;Cleaned_lesser_roundtrips_timestamp;Cleaned_lesser_roundtrips_duration;Data_Released;Data_Released_timestamp;Data_Released_duration;Received_from_supplier;Received_from_supplier_timestamp;Received_from_supplier_duration";

    protected static final String JETTY_TEST = "jettyTest";


    public static final boolean enabled = true;

    private DefaultClientConfig config;

    private String batchID = "400022028241";
    private String eventID = "Metadata_Archived";
    private String integrationTestServer;

    @BeforeClass(groups = JETTY_TEST, enabled = enabled)
    public void setup() throws IOException {
        integrationTestServer = "http://localhost:8080/process-monitor-frontend/services/batches";
        config = new DefaultClientConfig();
    }


    @Test(groups = JETTY_TEST, enabled = enabled)
    public void testGetBatches() {
        System.out.println("Running testGetBatches");
        Batch[] result = Client.create(config).resource(integrationTestServer).get(Batch[].class);
        Assert.assertTrue(result.length > 1, "We expected more than one batch to be returned");
    }

    @Test(groups = JETTY_TEST, enabled = enabled)
    public void testGetSingleBatch() {
        System.out.println("Running testGetSingleBatch");
        Batch result = Client.create(config).resource(integrationTestServer).path(batchID + "").get(Batch.class);
        Assert.assertEquals(result.getBatchID(), batchID, "This is not the batch we expected");
        Assert.assertTrue(result.getEvents().containsKey(eventID), "The batch does not contain the expected key: "+result.toString());
        Assert.assertTrue(result.getEvents().get(eventID).isSuccess(), "The event is not marked as succesful");

    }


    @Test(groups = JETTY_TEST, enabled = enabled)
    public void testGetSingleEvent() {
        System.out.println("Running testGetSingleEvent");
        Event result = Client.create(config)
                .resource(integrationTestServer)
                .path(batchID + "")
                .path("roundtrips")
                .path("1")
                .path("events")
                .path(eventID)
                .queryParam("details", "true")
                .get(Event.class);
        Assert.assertTrue(result.isSuccess(), "The event is not succesful");
    }

    @Test(groups = JETTY_TEST, enabled = enabled)
    public void testGetBatchesCSV() {
        String result = Client.create(config)
                .resource(integrationTestServer)
                .accept("text/csv")
                .get(String.class);
        result = cleanDate(result);

        Assert.assertEquals(result.substring(0,result.indexOf('\n')), HEADER,
                          "Expect a column header first");
        Assert.assertTrue(result.contains("\"=\"\"4001\"\"\";0;boersen;;;0;;;;true;1970-01-01 01:00:00;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n"),
                          "The 4001 batch should be contained, but was " + result);
        Assert.assertTrue(result.contains("\"=\"\"400022028241\"\"\";1;"),
                          "The small test batch should be there, but was " + result);
    }

    @Test(groups = JETTY_TEST, enabled = enabled)
    public void testGetSingleBatchCSV() {
        String result = Client.create(config)
                .resource(integrationTestServer)
                .path(batchID + "")
                .accept("text/csv")
                .get(String.class);
        result = cleanDate(result);
        Assert.assertEquals(result.trim(),
                            HEADER+"\n" + "\"=\"\"400022028241\"\"\";1;adresseavisen1759;1795-06-01;1795-06-15;9;;;;true;1970-01-01 01:00:00;;true;1970-01-01 01:00:00;;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;;;;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;true;1970-01-01 01:00:00;P0Y1M0DT0H0M0.101S;;;;;;");
    }

    private String cleanDate(String result) {
        result =  result.replaceAll("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}", "1970-01-01 01:00:00");
        result = result.replaceAll("P\\d+Y\\d+M\\d+DT\\d+H\\d+M\\d+\\.\\d+S", "P0Y1M0DT0H0M0.101S");
        return result;
    }

    @Test(groups = JETTY_TEST, enabled = enabled)
    public void testGetSingleEventCSV() {
        String result = Client.create(config)
                .resource(integrationTestServer)
                .path(batchID + "")
                .path("roundtrips")
                .path("1")
                .path("events")
                .path(eventID)
                .queryParam("details", "true")
                .accept("text/csv")
                .get(String.class);
        result = cleanDate(result);
        Assert.assertEquals(result, ";;;;;;true;1970-01-01 01:00:00;\n");
    }
}
