package dk.statsbiblioteket.newspaper.processmonitor.backend;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.w3c.dom.Document;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.EnhancedFedoraImpl;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import dk.statsbiblioteket.medieplatform.autonomous.processmonitor.datasources.SBOIDatasourceConfiguration;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.DefaultNamespaceContext;
import dk.statsbiblioteket.util.xml.XPathSelectorImpl;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;

/**
 * Add number of pages to batch and duration to events.
 */

public class DOMSBatchEnricher implements BatchEnricher {
    private SBOIDatasourceConfiguration config;

    @Override
    public List<Batch> enrich(List<Batch> batches) {
        for (Batch batch : batches) {
            enrichBatch(batch);
        }
        return batches;
    }

    private Batch enrichBatch(Batch batch) {
        String batchStructureXML = null;
        String eventXML = null;
        try {
            EnhancedFedora fedora = new EnhancedFedoraImpl(
                    new Credentials(config.getDomsUser(), config.getDomsPassword()), config.getDomsLocation(), config.getDomsPidGenLocation(), null, Integer.parseInt(config.getDomsRetries()), Integer.parseInt(config.getDomsDelayBetweenRetries()));
            String pid = fedora.findObjectFromDCIdentifier("B" + batch.getBatchID() + "RT" + batch.getRoundTripNumber()).get(0);
            batchStructureXML = fedora.getXMLDatastreamContents(pid, "BATCHSTRUCTURE");
            eventXML = fedora.getXMLDatastreamContents(pid, "EVENTS");
        } catch (BackendInvalidCredsException | BackendMethodFailedException | BackendInvalidResourceException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        Document batchStructureDOM = DOM.stringToDOM(batchStructureXML, true);
        Integer pages = DOM.selectInteger(batchStructureDOM,
                                            "count(/node/node[@shortName != 'WORKSHIFT-ISO-TARGET']/node[@shortName != 'UNMATCHED' and @shortName != 'FILM-ISO-target']/node/node[substring(@shortName, string-length(@shortName) - string-length('-brik.jp2') +1) != '-brik.jp2']/attribute[@shortName = 'contents'])");
        batch.setNumberOfPages(pages);


        Document eventDOM = DOM.stringToDOM(eventXML, true);
        XPathSelectorImpl xPathSelector
                = new XPathSelectorImpl(new DefaultNamespaceContext("info:lc/xmlns/premis-v2", "premis", "info:lc/xmlns/premis-v2"), 10);
        for (Map.Entry<String, Event> event : batch.getEvents().entrySet()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            String eventOutcome = xPathSelector.selectString(eventDOM, "/premis/event[eventType/text()='" + event.getKey()
                    + "' and eventDateTime/text()='" + sdf.format(event.getValue().getDate())
                    + "']/eventOutcomeInformation/eventOutcomeDetail/eventOutcomeDetailNote/text()");
            if (eventOutcome == null || eventOutcome.isEmpty()) {
                continue;
            }
            Document eventOutcomeDOM = DOM.stringToDOM(eventOutcome,true);
            String eventDuration = xPathSelector.selectString(eventOutcomeDOM,
                                                              "result/duration/text");
            event.getValue().setDuration(eventDuration);
        }

        return batch;
    }

    public void setConfig(SBOIDatasourceConfiguration config) {
        this.config = config;
    }

    public SBOIDatasourceConfiguration getConfig() {
        return config;
    }
}
