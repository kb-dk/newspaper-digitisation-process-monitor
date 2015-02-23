package dk.statsbiblioteket.newspaper.processmonitor.backend;

import org.testng.annotations.Test;

import dk.statsbiblioteket.doms.central.connectors.EnhancedFedora;
import dk.statsbiblioteket.util.Strings;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class DurationDOMSBatchEnricherTest {
    @Test
    public void testDurationEnrich() throws Exception {
        //TODO also test with event without duration
        EnhancedFedora fedora = mock(EnhancedFedora.class);

        String pid = "uuid:test";
        when(fedora.getXMLDatastreamContents(eq(pid), eq("EVENTS"))).thenReturn(Strings.flush(Thread.currentThread()
                                                                                                            .getContextClassLoader()
                                                                                                            .getResourceAsStream("PREMIS.xml")));
        DurationDOMSBatchEnricher enricher = new DurationDOMSBatchEnricher();
        enricher.setFedora(fedora);
        Batch myBatch = new Batch();
        myBatch.setDomsID(pid);
        myBatch.setEvents(new HashMap<String, Event>());
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");



        final Event created = new Event();
        created.setDate(dateformat.parse("2014-10-29T18:15:46.215+01:00"));
        myBatch.getEvents().put("Data_Received",created);

        final Event created2 = new Event();
        created2.setDate(dateformat.parse("2014-11-19T10:41:05.342+01:00"));
        myBatch.getEvents().put("Data_Received", created2);

        final Event metadata = new Event();
        metadata.setDate(dateformat.parse("2014-10-29T19:26:41.565+01:00"));
        myBatch.getEvents().put("Metadata_Archived", metadata);

        final Event data = new Event();
        data.setDate(dateformat.parse("2014-10-30T01:27:30.791+01:00"));
        myBatch.getEvents().put("Data_Archived", data);

        enricher.enrich(myBatch);

        verify(fedora).getXMLDatastreamContents(pid, "EVENTS");
        verifyNoMoreInteractions(fedora);

        assertEquals(created.getDuration(), null);
        assertEquals(created2.getDuration(), null);
        assertEquals(metadata.getDuration(),"P0Y0M0DT0H52M23.609S");
        assertEquals(data.getDuration(),"P0Y0M0DT3H1M57.970S");

        // Make sure caching happens - call again, no more interactions expected.
        enricher.enrich(myBatch);

        verifyNoMoreInteractions(fedora);

    }
}