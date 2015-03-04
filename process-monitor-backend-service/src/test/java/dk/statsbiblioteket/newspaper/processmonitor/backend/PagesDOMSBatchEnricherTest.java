package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.doms.central.connectors.EnhancedFedora;
import dk.statsbiblioteket.util.Strings;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class PagesDOMSBatchEnricherTest {

    @Test
    public void testNumberOfPagesEnrich() throws Exception {
        EnhancedFedora fedora = mock(EnhancedFedora.class);

        String pid = "uuid:test";
        when(fedora.getXMLDatastreamContents(eq(pid),eq("BATCHSTRUCTURE"))).thenReturn(Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream("BATCHSTRUCTURE.xml")));

        PagesDOMSBatchEnricher enricher = new PagesDOMSBatchEnricher();
        enricher.setFedora(fedora);
        Batch myBatch = new Batch();
        myBatch.setDomsID(pid);
        HashMap<String, Event> events = new HashMap<>();
        events.put("Structure_Checked", new Event());
        myBatch.setEvents(events);
        enricher.enrich(myBatch);

        //Verify expected DOMS calls
        verify(fedora).getXMLDatastreamContents(pid, "BATCHSTRUCTURE");
        verifyNoMoreInteractions(fedora);

        //Verify expected number of pages
        assertEquals(10269, myBatch.getNumberOfPages());

        //Ensure caching happens, call again
        PagesDOMSBatchEnricher enricher2 = new PagesDOMSBatchEnricher();
        enricher2.setFedora(fedora);
        Batch myBatch2 = new Batch();
        myBatch2.setDomsID(pid);
        HashMap<String, Event> events2 = new HashMap<>();
        events2.put("Structure_Checked", new Event());
        myBatch2.setEvents(events2);
        enricher2.enrich(myBatch2);
        verifyNoMoreInteractions(fedora);
    }
}