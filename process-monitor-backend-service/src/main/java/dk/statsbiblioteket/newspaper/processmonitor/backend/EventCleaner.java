package dk.statsbiblioteket.newspaper.processmonitor.backend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventCleaner {

    private static Set<String> eventIDs = new HashSet<>(
            Arrays.asList("TODO", "")
    );

    public static Batch cleanBatch(Batch batch) {
        Map<String, Event> events = batch.getEvents();
        Map<String, Event> eventsResult = new HashMap<>();
        for (Map.Entry<String, Event> stringEventEntry : events.entrySet()) {
            if (eventIDs.contains(stringEventEntry.getKey())) {
                eventsResult.put(stringEventEntry.getKey(), stringEventEntry.getValue());
            }
        }
        Batch result = new Batch();
        result.setBatchID(batch.getBatchID());
        result.setEvents(eventsResult);
        return result;
    }
}
