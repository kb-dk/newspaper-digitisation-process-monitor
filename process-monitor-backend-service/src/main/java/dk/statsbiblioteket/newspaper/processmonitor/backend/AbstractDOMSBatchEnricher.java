package dk.statsbiblioteket.newspaper.processmonitor.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.EnhancedFedora;
import dk.statsbiblioteket.medieplatform.autonomous.processmonitor.datasources.SBOIDatasourceConfiguration;

import java.util.List;

/**
 * Common superclass for enrichers using DOMS data
 */
public abstract class AbstractDOMSBatchEnricher implements BatchEnricher {
    EnhancedFedora fedora;
    private SBOIDatasourceConfiguration config;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<Batch> enrich(List<Batch> batches) {
        for (Batch batch : batches) {
            enrich(batch);
        }
        return batches;
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
                log.warn("Failed to retrieve pid from doms for object 'B" + batch.getBatchID() + "-RT" + batch
                        .getRoundTripNumber() + "'", e);
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
