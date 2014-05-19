package dk.statsbiblioteket.newspaper.processmonitor.backend;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import dk.statsbiblioteket.newspaper.mfpakintegration.database.InconsistentDatabaseException;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.MfPakDAO;

@Component
public class MfPakBatchEnricher implements BatchEnricher {

    private MfPakDAO mfpak;
    private Logger log = LoggerFactory.getLogger(getClass());
    public MfPakDAO getMfpak() {
        return mfpak;
    }

    public void setMfpak(MfPakDAO mfpak) {
        this.mfpak = mfpak;
    }
    
    /**
     * Enriches the batch objects with 'avisid' and date intervals from the mfpak database. 
     * The returned reference to list of batches is the same as the one taken as input. 
     */
    @Override
    public List<Batch> enrich(List<Batch> batches) {
        
        for(Batch b : batches) {
            String batchID = b.getBatchID();
            try {
                b.setAvisID(mfpak.getNewspaperID(batchID));    
            } catch (InconsistentDatabaseException | SQLException e) {
                log.debug("Failed to enrich batch {}", batchID, e);
            }
                
        }
        
        return batches;
    }
    
}
