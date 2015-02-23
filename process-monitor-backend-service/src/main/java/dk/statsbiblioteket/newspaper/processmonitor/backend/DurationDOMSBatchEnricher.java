package dk.statsbiblioteket.newspaper.processmonitor.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.util.caching.TimeSensitiveCache;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Add duration to events in batch.
 */

public class DurationDOMSBatchEnricher extends AbstractDOMSBatchEnricher {
    private static final Logger log = LoggerFactory.getLogger(DurationDOMSBatchEnricher.class);
    private static final int ONE_DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
    private static final TimeSensitiveCache<String, Map<String, String>> cache = new TimeSensitiveCache<>(ONE_DAY_IN_MILLISECONDS, true);

    @Override
    public Batch enrich(Batch batch) {
        String pid;
        try {
            pid = getPid(batch);
        } catch (BackendInvalidResourceException e) {
            //Okay, no pid in doms found, just return
            return batch;
        }

        Map<String, String> cachedDurations = cache.get(key(batch));
        if (cachedDurations != null) {
            fillOutCachedDurations(batch, cachedDurations);
        } else {
            enrichDuration(batch, pid);
        }

        return batch;
    }

    protected void enrichDuration(Batch batch, String pid) {
        Map <String, String> durations = new HashMap<>();
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
            durations.put(key(event.getKey(), event.getValue()), eventDuration);
        }
        cache.put(key(batch), durations);
    }

    protected void fillOutCachedDurations(Batch batch, Map<String, String> cachedDurations) {
        for (Map.Entry<String, Event> event : batch.getEvents().entrySet()) {
            event.getValue().setDuration(cachedDurations.get(key(event.getKey(), event.getValue())));
        }
    }

    private String key(Batch batch) {
        StringBuilder sb = new StringBuilder();
        sb.append(batch.getBatchID()).append(':');
        for (Iterator<Map.Entry<String, Event>> iterator = batch.getEvents().entrySet().iterator();
             iterator.hasNext(); ) {
            Map.Entry<String, Event> event = iterator.next();
            sb.append(key(event.getKey(), event.getValue()));
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String key(String name, Event event) {
        return name + ":" + event.getDate() + "," + event.isSuccess();
    }
}
