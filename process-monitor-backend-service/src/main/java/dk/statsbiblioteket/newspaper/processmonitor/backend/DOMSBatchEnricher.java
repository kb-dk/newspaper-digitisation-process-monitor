package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.doms.central.connectors.EnhancedFedora;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.medieplatform.autonomous.processmonitor.datasources.SBOIDatasourceConfiguration;
import dk.statsbiblioteket.util.xml.DOM;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Add number of pages to batch and duration to events.
 */

public class DOMSBatchEnricher implements BatchEnricher {

    private static final Logger log = LoggerFactory.getLogger(DOMSBatchEnricher.class);
    private SBOIDatasourceConfiguration config;

    EnhancedFedora fedora;


    @Override
    public List<Batch> enrich(List<Batch> batches) {
        for (Batch batch : batches) {
            enrich(batch);
        }
        return batches;
    }

    @Override
    public Batch enrich(Batch batch) {
        String pid = null;
        try {
            pid = getPid(batch);
        } catch (BackendInvalidResourceException e) {
            //Okay, no pid in doms found, just return
            return batch;
        }

        enrichNumberOfPages(batch, pid);

        enrichDuration(batch, pid);

        return batch;
    }

    protected void enrichDuration(Batch batch, String pid) {
        String eventXML;
        try {
            eventXML = fedora.getXMLDatastreamContents(pid, "EVENTS");
        } catch (BackendInvalidCredsException | BackendMethodFailedException e) {
            log.warn("Failed to retrieve EVENTS from doms for object '" + pid + "'", e);
            return;
        } catch (BackendInvalidResourceException e) {
            //Okay, no events datastream, just return,
            return;
        }
        Document eventDOM = DOM.stringToDOM(eventXML, true);
        XPathSelector xPathSelector
                = DOM.createXPathSelector("premis", "info:lc/xmlns/premis-v2","result","http://schemas.statsbiblioteket.dk/result/");

        for (Map.Entry<String, Event> event : batch.getEvents().entrySet()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            final String xpath
                    = "/premis:premis/premis:event[premis:eventType/text()='" + event.getKey() + "' and premis:eventDateTime/text()='" + sdf.format(event.getValue().getDate()) + "']/premis:eventOutcomeInformation/premis:eventOutcomeDetail/premis:eventOutcomeDetailNote/text()";
            String eventOutcome = xPathSelector.selectString(eventDOM, xpath);
            if (eventOutcome == null || eventOutcome.isEmpty()) {
                continue;
            }
            Document eventOutcomeDOM = dk.statsbiblioteket.newspaper.processmonitor.backend.DOM.stringToDOM(eventOutcome,
                                                                                                                   true);
            String eventDuration = xPathSelector.selectString(eventOutcomeDOM,
                                                              "/result:result/result:duration/text()");
            event.getValue().setDuration(eventDuration);
        }
    }

    protected void enrichNumberOfPages(Batch batch, String pid) {
        String batchStructureXML;
        try {
            batchStructureXML = fedora.getXMLDatastreamContents(pid, "BATCHSTRUCTURE");
        } catch (BackendInvalidCredsException | BackendMethodFailedException e) {
            log.warn("Failed to retrieve BATCHSTRUCTURE from doms for object '" + pid + "'", e);
            return;
        } catch (BackendInvalidResourceException e) {
            //Okay, the batch have not yet been given a structure
            return;
        }

        Document batchStructureDOM = DOM.stringToDOM(batchStructureXML, true);
        Integer pages = DOM.selectInteger(batchStructureDOM,
                                            "count(/node/node[@shortName != 'WORKSHIFT-ISO-TARGET']/node[@shortName != 'UNMATCHED' and @shortName != 'FILM-ISO-target']/node/node[substring(@shortName, string-length(@shortName) - string-length('-brik.jp2') +1) != '-brik.jp2']/attribute[@shortName = 'contents'])");
        batch.setNumberOfPages(pages);
    }

    protected String getPid(Batch batch) throws BackendInvalidResourceException {
        String pid;
        if (batch.getDomsID() != null){
            pid = batch.getDomsID();
        } else {
            try {
                final List<String> identifierList
                        = fedora.findObjectFromDCIdentifier("path:B" + batch.getBatchID() + "-RT" + batch.getRoundTripNumber());
                if (identifierList.isEmpty()){
                    throw new BackendInvalidResourceException("Pid not found for "+batch);
                }
                pid = identifierList
                            .get(0);
            } catch (BackendInvalidCredsException | BackendMethodFailedException e) {
                log.warn("Failed to retrieve get pid from doms for object 'B" + batch.getBatchID() + "-RT" + batch.getRoundTripNumber()+"'", e);
                throw new RuntimeException(e);
            }
        }
        return pid;
    }

    public void setConfig(SBOIDatasourceConfiguration config) {
        this.config = config;
    }

    public SBOIDatasourceConfiguration getConfig() {
        return config;
    }

    public EnhancedFedora getFedora() {
        return fedora;
    }

    public void setFedora(EnhancedFedora fedora) {
        this.fedora = fedora;
    }
}
