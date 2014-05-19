package dk.statsbiblioteket.newspaper.processmonitor.backend;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import dk.statsbiblioteket.newspaper.mfpakintegration.database.MfPakDAO;

@Component
public class MfPakBatchEnricher implements BatchEnricher {

    private MfPakDAO mfpak;

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
        return batches;
    }
    
}
