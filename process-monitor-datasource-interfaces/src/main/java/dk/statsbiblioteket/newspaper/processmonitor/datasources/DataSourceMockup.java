package dk.statsbiblioteket.newspaper.processmonitor.datasources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/17/13
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataSourceMockup implements DataSource {

    private final ArrayList<Batch> dummyBatches;

    public DataSourceMockup() {


        Event e1 = new Event();
        e1.setEventID("foo");
        e1.setSucces(true);

        Event e2 = new Event();
        e2.setEventID("bar");
        e2.setSucces(false);

        Event e3 = new Event();
        e3.setEventID("baz");
        e3.setSucces(true);
        List<Event> b1Events = new ArrayList<Event>();
        b1Events.add(e1);
        b1Events.add(e2);
        b1Events.add(e3);

        Batch b1 = new Batch();
        b1.setBatchID("hans");
        b1.setEventList(b1Events);

        Event e4 = new Event();
        e4.setEventID("foo");
        e4.setSucces(true);

        Event e5 = new Event();
        e5.setEventID("bar");
        e5.setSucces(false);

        Event e6 = new Event();
        e6.setEventID("baz");
        e6.setSucces(false);

        List<Event> b2Events = new ArrayList<Event>();
        b2Events.add(e4);
        b2Events.add(e5);
        b2Events.add(e6);

        Batch b2 = new Batch();
        b2.setBatchID("bjarne");
        b2.setEventList(b2Events);

        dummyBatches = new ArrayList<Batch>();
        dummyBatches.add(b1);
        dummyBatches.add(b2);

    }

    @Override
    public List<Batch> getBatches(boolean includeDetails, Map<String, String> filters) {
        return dummyBatches;
    }

    @Override
    public Batch getBatch(String batchID, boolean includeDetails) {
        Batch batch = null;
        for (Batch b : dummyBatches) {
            if (b.getBatchID().equals(batchID)) {
                batch = b;
            }
        }
        return batch;
    }

    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) {
        Event event = null;
        for (Batch b : dummyBatches) {
            if (b.getBatchID().equals(batchID)) {
                for (Event e : b.getEventList()) {
                    if (e.getEventID().equals(eventID)) {
                        event = e;
                    }
                }
            }
        }
        return event;
    }
}
