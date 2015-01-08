package dk.statsbiblioteket.newspaper.processmonitor.backend;

import dk.statsbiblioteket.medieplatform.autonomous.processmonitor.datasources.SBOIDatasourceConfiguration;

import java.util.List;

/**
 * Add number of pages to batch and duration to events.
 */

public class DOMSBatchEnricher implements BatchEnricher {
    private SBOIDatasourceConfiguration config;

    @Override
    public List<Batch> enrich(List<Batch> batches) {
        //return batches.stream().map(this::enrichBatch).collect(Collectors.toList());
        return batches;
    }

    private Batch enrichBatch(Batch batch) {
/*
        Client client = Client.create();
        client.addFilter(
                new HTTPBasicAuthFilter(
                        config.getDomsUser(),
                        config.getDomsPassword()));

        WebResource domsResource = client.resource(config.getDomsLocation()).path("/objects/");
        String batchStructureXML = domsResource.path(pid).path("/datastreams/BATCHSTRUCTURE/contents").get(String.class);
        Document batchStructureDOM = DOM.stringToDOM(batchStructureXML, true);
        // count(node/node[@shortName != 'WORKSHIFT-ISO-TARGET']/node[@shortName != 'UNMATCHED' and @shortName != 'FILM-ISO-target']/node/node[substring(@shortName, string-length(@shortName) - string-length('-brik.jp2') +1) != '-brik.jp2']/attribute[@shortName = 'contents'])
        DOM.selectInteger(batchStructureDOM, "count(/node/node[@shortName != 'WORKSHIFT-ISO-TARGET']/node[@shortName != 'UNMATCHED' and @shortName != 'FILM-ISO-target']/node/node[substring(@shortName, string-length(@shortName) - string-length('-brik.jp2') +1) != '-brik.jp2']/attribute[@shortName = 'contents'])");
*/


        return batch;
    }

    public void setConfig(SBOIDatasourceConfiguration config) {
        this.config = config;
    }

/*
    public SBOIDatasourceConfiguration getConfig() {
        return config;
    }
*/
}
