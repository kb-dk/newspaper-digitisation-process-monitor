package dk.statsbiblioteket.newspaper.processmonitor.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.util.xml.DOM;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Add number of pages to batch and duration to events.
 */

public class PagesDOMSBatchEnricher extends AbstractDOMSBatchEnricher {
    private static final Logger log = LoggerFactory.getLogger(PagesDOMSBatchEnricher.class);
    private static final Map<Batch, Integer> cache = Collections.synchronizedMap(new HashMap<Batch, Integer>());

    @Override
    public Batch enrich(Batch batch) {
        String pid;
        try {
            pid = getPid(batch);
        } catch (BackendInvalidResourceException e) {
            //Okay, no pid in doms found, just return
            return batch;
        }

        if (cache.containsKey(batch)) {
            batch.setNumberOfPages(cache.get(batch));
        } else {
            enrichNumberOfPages(batch, pid);
        }
        return batch;
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
        cache.put(batch, pages);
        batch.setNumberOfPages(pages);
    }

}
