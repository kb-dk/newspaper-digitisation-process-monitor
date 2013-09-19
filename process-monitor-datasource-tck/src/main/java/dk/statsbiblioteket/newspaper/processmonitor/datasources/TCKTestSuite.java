package dk.statsbiblioteket.newspaper.processmonitor.datasources;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public abstract class TCKTestSuite {

    private final DataSource dataSource;
    private final boolean runNrInBatchID;

    protected TCKTestSuite(DataSource dataSource, boolean isRunNrInBatchID) {
        this.dataSource = dataSource;
        runNrInBatchID = isRunNrInBatchID;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public abstract String getValidBatchID();

    public abstract String getInvalidBatchID();

    public abstract String getValidAndSucessfullEventIDForValidBatch();

    public abstract String getInvalidEventIDForValidBatch();


    private boolean idContainsRunNr(String batchID) {
        return batchID.contains("-");
    }


    @Test(groups = "integrationTest")
    public void testIsRunNrInBatchID() {
        Assert.assertEquals(dataSource.isRunNrInBatchID(), runNrInBatchID, "Run nr should be set correctly");
        boolean containsRunNr = idContainsRunNr(getValidBatchID());
        if (runNrInBatchID) {
            Assert.assertTrue(containsRunNr, "Run nr should be in batch id, but is not in valid id");
        } else {
            Assert.assertFalse(containsRunNr, "Run nr should not be in batch id, but is in valid id");
        }

    }


    @Test(groups = "integrationTest")
    public void testGetBatches() {
        List<Batch> batches = dataSource.getBatches(false, null);
        Assert.assertTrue(batches.size() > 0, "The datasource have no content");
        boolean validHaveBeenFound = false;
        boolean anEventHaveBeenSeen = false;
        for (Batch batch : batches) {
            if (runNrInBatchID != idContainsRunNr(batch.getBatchID())) {
                Assert.fail("The batch ids should not contain run nrs, if this is not specified");
            }
            List<Event> eventList = batch.getEventList();
            Assert.assertNotNull(eventList, "The event list cannot be null");
            if (eventList.size() > 0) {
                anEventHaveBeenSeen = true;
            }
            if (batch.getBatchID().equals(getValidBatchID())) {
                validHaveBeenFound = true;
                boolean goodEventFound = false;
                for (Event event : eventList) {
                    if (event.getEventID().equals(getValidAndSucessfullEventIDForValidBatch())) {
                        Assert.assertTrue(event.isSuccess(), "The successful event must be successful");
                        goodEventFound = true;
                    }
                }
                Assert.assertTrue(goodEventFound, "The good event was not found for the valid batch");
            }
            for (Event event : eventList) {
                // Assert.assertNull(event.getDetails(),"We requested no details, so that must be null");

            }
        }
        if (!validHaveBeenFound) {
            Assert.fail("Failed to find the valid ID among all the batches");
        }
        if (!anEventHaveBeenSeen) {
            Assert.fail("None of the batches have any events. Quite boring, right?");
        }
    }

    @Test(groups = "integrationTest")
    public void testGetInvalidBatch() {
        try {
            Batch batch = dataSource.getBatch(getInvalidBatchID(), false);
            Assert.assertNotNull(batch, "Do not return null");
            Assert.fail("The invalid batch was found");
        } catch (NotFoundException e) {
            //expected
        }
    }

    @Test(groups = "integrationTest")
    public void testGetValidBatch() {
        Batch validBatch = null;
        try {

            validBatch = dataSource.getBatch(getValidBatchID(), true);
            Assert.assertNotNull(validBatch, "Do not return null");
        } catch (NotFoundException e) {
            Assert.fail("The valid batch was not found", e);
        }
        Assert.assertEquals(validBatch.getBatchID(), getValidBatchID(), "The batch have a wrong ID");
        List<Event> eventList = validBatch.getEventList();
        Assert.assertTrue(eventList.size() > 0, "The valid batch must have at least one event");
        for (Event event : eventList) {
            // Assert.assertNotNull(event.getDetails(), "We requested details, so that must be not null");
            if (event.getEventID().equals(getValidAndSucessfullEventIDForValidBatch())) {
                Assert.assertTrue(event.isSuccess(), "The event must be successful");
            }
        }
    }

    @Test(groups = "integrationTest")
    public void testGetEvent() {

        Event event = null;
        try {
            event = dataSource.getBatchEvent(getValidBatchID(), getValidAndSucessfullEventIDForValidBatch(), true);
            Assert.assertNotNull(event, "Do not return null");
        } catch (NotFoundException e) {
            Assert.fail("The valid batch event was not found", e);
        }
        Assert.assertEquals(event.getEventID(), getValidAndSucessfullEventIDForValidBatch(), "The event have a wrong ID");
        //   Assert.assertNotNull(event.getDetails(), "We requested details, so that must be not null");
        Assert.assertTrue(event.isSuccess(), "The event must be successful");


    }


    @Test(groups = "integrationTest")
    public void testGetInvalidEvent() {

        Event event = null;
        try {
            event = dataSource.getBatchEvent(getValidBatchID(), getInvalidEventIDForValidBatch(), true);
            Assert.assertNotNull(event, "Do not return null");
            Assert.fail("The invalid event was found");
        } catch (NotFoundException e) {

        }


    }

}
