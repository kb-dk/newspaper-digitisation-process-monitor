package dk.statsbiblioteket.newspaper.processmonitor.backend;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Test CSV generator methods
 */
public class CSVGeneratorTest {
    private static final Map<String, Event> EVENT_MAP_1;
    static {
        Map<String, Event> eventMap1 = new HashMap<>();
        eventMap1.put("Data_Received", getTestEvent(new Date(0), false, "Test"));
        eventMap1.put("JPylyzed", getTestEvent(new Date(1000), false, "Hello World"));
        eventMap1.put("Unknown", getTestEvent(new Date(2000), false, null));
        EVENT_MAP_1 = eventMap1;
    }

    private static final Map<String, Event> EVENT_MAP_2;
    static {
        Map<String, Event> eventMap2 = new HashMap<>();
        eventMap2.put("Structure_Checked", getTestEvent(new Date(1000), false, "Test"));
        eventMap2.put("Metadata_Archived", getTestEvent(new Date(0), true, null));
        eventMap2.put("Shipped_to_supplier", getTestEvent(new Date(2000), true, "æøå\nabc"));
        EVENT_MAP_2 = eventMap2;
    }

    private static final Batch TEST_BATCH_1 = getTestBatch("4000000000", 2, EVENT_MAP_1);

    private static final Batch TEST_BATCH_2 = getTestBatch("4000000001", 1, EVENT_MAP_2);

    private static final List<Batch> TEST_BATCHES = Arrays.asList(TEST_BATCH_1, TEST_BATCH_2);

    private static final Event TEST_EVENT = getTestEvent(new Date(1000), false, "The\ndetails\næøå");

    @Test
    public void testGenerateCSVForBatchList() throws Exception {
        String expectedOutput
                = "Batch;Roundtrip;Avis id;Pages;Manually_stopped;Manually_stopped_timestamp;Manually_stopped_duration;Shipped_to_supplier;Shipped_to_supplier_timestamp;Shipped_to_supplier_duration;Data_Received;Data_Received_timestamp;Data_Received_duration;Metadata_Archived;Metadata_Archived_timestamp;Metadata_Archived_duration;Data_Archived;Data_Archived_timestamp;Data_Archived_duration;Structure_Checked;Structure_Checked_timestamp;Structure_Checked_duration;JPylyzed;JPylyzed_timestamp;JPylyzed_duration;Histogrammed;Histogrammed_timestamp;Histogrammed_duration;Metadata_checked;Metadata_checked_timestamp;Metadata_checked_duration;Manual_QA_Flagged;Manual_QA_Flagged_timestamp;Manual_QA_Flagged_duration;Roundtrip_Approved;Roundtrip_Approved_timestamp;Roundtrip_Approved_duration;Dissemination_Copy_Generated;Dissemination_Copy_Generated_timestamp;Dissemination_Copy_Generated_duration;Dissemination_Editions_Generated;Dissemination_Editions_Generated_timestamp;Dissemination_Editions_Generated_duration;Metadata_Enriched;Metadata_Enriched_timestamp;Metadata_Enriched_duration;Cleaned_lesser_roundtrips;Cleaned_lesser_roundtrips_timestamp;Cleaned_lesser_roundtrips_duration;Data_Released;Data_Released_timestamp;Data_Released_duration;Received_from_supplier;Received_from_supplier_timestamp;Received_from_supplier_duration\n" +
                  "\"=\"\"4000000000\"\"\";2;;0;;;;;;;false;1970-01-01 01:00:00;;;;;;;;;;;false;1970-01-01 01:00:01;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n" +
                  "\"=\"\"4000000001\"\"\";1;;0;;;;true;1970-01-01 01:00:02;;;;;true;1970-01-01 01:00:00;;;;;false;1970-01-01 01:00:01;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new CSVGenerator().writeTo(TEST_BATCHES, null, null, null, null, null, baos);
        assertEquals(baos.toString(), expectedOutput);
    }

    @Test
    public void testGenerateCSVForBatch() throws Exception {
        String expectedOutput
                = "Batch;Roundtrip;Avis id;Pages;Manually_stopped;Manually_stopped_timestamp;Manually_stopped_duration;Shipped_to_supplier;Shipped_to_supplier_timestamp;Shipped_to_supplier_duration;Data_Received;Data_Received_timestamp;Data_Received_duration;Metadata_Archived;Metadata_Archived_timestamp;Metadata_Archived_duration;Data_Archived;Data_Archived_timestamp;Data_Archived_duration;Structure_Checked;Structure_Checked_timestamp;Structure_Checked_duration;JPylyzed;JPylyzed_timestamp;JPylyzed_duration;Histogrammed;Histogrammed_timestamp;Histogrammed_duration;Metadata_checked;Metadata_checked_timestamp;Metadata_checked_duration;Manual_QA_Flagged;Manual_QA_Flagged_timestamp;Manual_QA_Flagged_duration;Roundtrip_Approved;Roundtrip_Approved_timestamp;Roundtrip_Approved_duration;Dissemination_Copy_Generated;Dissemination_Copy_Generated_timestamp;Dissemination_Copy_Generated_duration;Dissemination_Editions_Generated;Dissemination_Editions_Generated_timestamp;Dissemination_Editions_Generated_duration;Metadata_Enriched;Metadata_Enriched_timestamp;Metadata_Enriched_duration;Cleaned_lesser_roundtrips;Cleaned_lesser_roundtrips_timestamp;Cleaned_lesser_roundtrips_duration;Data_Released;Data_Released_timestamp;Data_Released_duration;Received_from_supplier;Received_from_supplier_timestamp;Received_from_supplier_duration\n" + "\"=\"\"4000000000\"\"\";2;;0;;;;;;;false;1970-01-01 01:00:00;;;;;;;;;;;false;1970-01-01 01:00:01;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new CSVGenerator().writeTo(TEST_BATCH_1, null, null, null, null, null, baos);
        assertEquals(baos.toString(), expectedOutput);
    }

    @Test
    public void testGenerateCSVForEvent() throws Exception {
        String expectedOutput=";;;;false;1970-01-01 01:00:01;\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new CSVGenerator().writeTo(TEST_EVENT, null, null, null, null, null, baos);
        assertEquals(baos.toString(), expectedOutput);
    }

    private static Batch getTestBatch(String batchID, int roundTripNumber, Map<String, Event> events) {
        Batch batch = new Batch();
        batch.setBatchID(batchID);
        batch.setRoundTripNumber(roundTripNumber);
        batch.setEvents(events);
        return batch;
    }

    private static Event getTestEvent(Date date, boolean success, String details) {
        Event event = new Event();
        event.setDate(date);
        event.setSuccess(success);
        event.setDetails(details);
        return event;
    }
}
