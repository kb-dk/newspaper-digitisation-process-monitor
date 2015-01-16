package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.doms.central.connectors.EnhancedFedora;
import dk.statsbiblioteket.doms.central.connectors.EnhancedFedoraImpl;
import dk.statsbiblioteket.doms.central.connectors.fedora.pidGenerator.PIDGeneratorException;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import dk.statsbiblioteket.util.Strings;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class DOMSBatchEnricherTest {

    @Test
    public void testNumberOfPagesEnrich() throws Exception {
        EnhancedFedora fedora = mock(EnhancedFedora.class);

        String pid = "uuid:test";
        when(fedora.getXMLDatastreamContents(eq(pid),eq("BATCHSTRUCTURE"))).thenReturn(Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream("BATCHSTRUCTURE.xml")));

        DOMSBatchEnricher enricher = new DOMSBatchEnricher();
        enricher.setFedora(fedora);
        Batch myBatch = new Batch();
        enricher.enrichNumberOfPages(myBatch, pid);
        assertEquals(10269, myBatch.getNumberOfPages());
    }

    @Test
    public void testDurationEnrich() throws Exception {
        //TODO also test with event without duration
        EnhancedFedora fedora = mock(EnhancedFedora.class);

        String pid = "uuid:test";
        when(fedora.getXMLDatastreamContents(eq(pid), eq("EVENTS"))).thenReturn(Strings.flush(Thread.currentThread()
                                                                                                            .getContextClassLoader()
                                                                                                            .getResourceAsStream("PREMIS.xml")));
        DOMSBatchEnricher enricher = new DOMSBatchEnricher();
        enricher.setFedora(fedora);
        Batch myBatch = new Batch();
        myBatch.setEvents(new HashMap<String, Event>());

        final Event metadata = new Event();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        metadata.setDate(dateformat.parse("2014-10-29T19:26:41.565+01:00"));
        myBatch.getEvents().put("Metadata_Archived", metadata);
        final Event data = new Event();
        data.setDate(dateformat.parse("2014-10-30T01:27:30.791+01:00"));
        myBatch.getEvents().put("Data_Archived", data);
        enricher.enrichDuration(myBatch, pid);
        assertEquals(metadata.getDuration(),"P0Y0M0DT0H52M23.609S");
        assertEquals(data.getDuration(),"P0Y0M0DT3H1M57.970S");
    }
}