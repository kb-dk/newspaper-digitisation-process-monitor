package dk.statsbiblioteket.newspaper.processmonitor.backend;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import dk.statsbiblioteket.medieplatform.autonomous.ConfigConstants;
import dk.statsbiblioteket.medieplatform.autonomous.processmonitor.datasources.SBOIDatasourceConfiguration;
import dk.statsbiblioteket.util.xml.DOM;
import org.w3c.dom.Document;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Add number of pages to batch and duration to events.
 */

public class DOMSBatchEnricher implements BatchEnricher {
    private final WebResource domsResource;
    private SBOIDatasourceConfiguration config;


    public DOMSBatchEnricher(SBOIDatasourceConfiguration config) {
        this.config = config;
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(config.getDomsUser(), config.getDomsPassword()));

        domsResource = client.resource(config.getDomsLocation()).path("/objects/");
    }

    @Override
    public List<Batch> enrich(List<Batch> batches) {
        return batches.stream().map(this::enrichBatch).collect(Collectors.toList());
    }

    private Batch enrichBatch(Batch batch) {

        String pid = "";
        String batchStructureXML = domsResource.path(pid).path("/datastreams/BATCHSTRUCTURE/contents").get(String.class);
        Document batchStructureDOM = DOM.stringToDOM(batchStructureXML, true);
        Integer numberOfPages = DOM.selectInteger(batchStructureDOM,
                                                         "count(/node/node[@shortName != 'WORKSHIFT-ISO-TARGET']/node[@shortName != 'UNMATCHED' and @shortName != 'FILM-ISO-target']/node/node[substring(@shortName, string-length(@shortName) - string-length('-brik.jp2') +1) != '-brik.jp2']/attribute[@shortName = 'contents'])");

        batch.setNumberOfPages(numberOfPages);
        return batch;
    }

    public void setConfig(SBOIDatasourceConfiguration config) {
        this.config = config;
    }
}
