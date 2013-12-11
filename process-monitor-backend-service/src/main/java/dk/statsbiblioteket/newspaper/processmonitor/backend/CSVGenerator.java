package dk.statsbiblioteket.newspaper.processmonitor.backend;

import csv.TableWriter;
import csv.impl.CSVWriter;
import csv.impl.type.DateConversionHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class that generates a CSV blob based on batch events.
 */
public class CSVGenerator {
    /** List and order of events to present in CSV files in different columns */
    private static final List<String> EVENTS = Arrays.asList(
            "Shipped_to_supplier",
            "Data_Received",
            "Metadata_Archived",
            "Data_Archived",
            "Structure_Checked",
            "JPylyzed",
            "Metadata_checked",
            "auto-qa",
            "manuel-qa",
            "Approved",
            "Received_from_supplier");

    /** How many columns are used per event in detailed mode */
    private static final int COLUMNS_PER_EVENT = 3;
    /** How many columns are used per row for headers */
    private static final int ROW_HEADER_COLUMNS = 2;

    /**
     * Generate CSV blob for a list of batches and events happened on these.
     * The report will contain a header row, and a row per batch with events for that batch.
     *
     * @param batches The batches to generate CSV for.
     * @param details Whether details should be included in the report.
     * @return A CSV Blob
     * @throws IOException If the blob cannot be generated.
     */
    public static String generateCSVForBatchList(List<Batch> batches, boolean details)
            throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TableWriter csvWriter = getTableWriter(stream);
        printHeader(csvWriter, details);
        for (Batch batch : batches) {
            generateCSVForBatch(csvWriter, batch, details);
        }
        return stream.toString();
    }

    /**
     * Generate CSV blob for a single batch and events happened on this.
     * The report will contain a header row and a single row with events for the batch.
     *
     * @param batch The batch to generate CSV for.
     * @param details Whether details should be included in the report.
     * @return A CSV Blob
     * @throws IOException If the blob cannot be generated.
     */
    public static String generateCSVForBatch(Batch batch, boolean details) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TableWriter csvWriter = getTableWriter(stream);
        printHeader(csvWriter, details);
        generateCSVForBatch(csvWriter, batch, details);
        return stream.toString();
    }

    /**
     * Generate CSV blob for a single batch event.
     * The report will contain a single row, with the event name and information about the event.
     *
     *
     * @param event The event to generate CSV for.
     * @param details Whether details should be included in the report.
     * @return A CSV Blob
     * @throws IOException If the blob cannot be generated.
     */
    public static String generateCSVForEvent(Event event, boolean details) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TableWriter csvWriter = getTableWriter(stream);
        Object[] row = new Object[COLUMNS_PER_EVENT + ROW_HEADER_COLUMNS];
        // Print the event details.
        generateCSVForEvent(EVENTS.get(0), event, row, details);
        csvWriter.printRow(row);
        return stream.toString();
    }

    /**
     * Get a suitable table writer for a given output stream.
     * @param stream The stream to get a table writer for.
     * @return The table writer.
     */
    private static TableWriter getTableWriter(OutputStream stream) {
        CSVWriter csvWriter = new CSVWriter(stream);
        DateConversionHandler handler = new DateConversionHandler();
        handler.setPrintFormat("yyyy-MM-dd HH:mm:ss");
        csvWriter.registerTypeConversionHandler(handler);
        return csvWriter;
    }

    /**
     * Generate a header matching the CSV rows for all events on a batch.
     *
     * @param csvWriter The csvWriter to generate the header on.
     * @param details Whether the header should match a detailed report.
     * @throws IOException If the header row cannot be generated.
     */
    private static void printHeader(TableWriter csvWriter, boolean details) throws IOException {
        // A row with the right length.
        Object[] header;
        if (details) {
            header = new Object[EVENTS.size() * COLUMNS_PER_EVENT + ROW_HEADER_COLUMNS];
        } else {
            header = new Object[EVENTS.size() + ROW_HEADER_COLUMNS];
        }

        // Row headers
        updateCell(header, 0, "Batch");
        updateCell(header, 1, "Roundtrip");

        // Event headers
        int index = ROW_HEADER_COLUMNS;
        for (String event : EVENTS) {
            updateCell(header, index, event);
            if (details) {
                index += COLUMNS_PER_EVENT;
            } else {
                index++;
            }
        }
        csvWriter.printRow(header);
    }

    /**
     * Run through all events for a batch, and generate a row containing information about events for that batch.
     *
     * @param csvWriter The csvWriter to generate the row on.
     * @param batch The batch to generate a row for.
     * @param details Whether the row should contain details.
     * @throws IOException If the row cannot be generated.
     */
    private static void generateCSVForBatch(TableWriter csvWriter, Batch batch, boolean details) throws IOException {
        // A row with the right length.
        Object[] row;
        if (details) {
            row = new Object[EVENTS.size() * COLUMNS_PER_EVENT + ROW_HEADER_COLUMNS];
        } else {
            row = new Object[EVENTS.size() + ROW_HEADER_COLUMNS];
        }

        // Row headers
        updateCell(row, 0, batch.getBatchID());
        updateCell(row, 1, batch.getRoundTripNumber());

        // Events
        Map<String, Event> events = batch.getEvents();
        for (Map.Entry<String, Event> event : events.entrySet()) {
            // Fill out details for the event.
            generateCSVForEvent(event.getKey(), event.getValue(), row, details);
        }
        csvWriter.printRow(row);
    }

    /**
     * Given an event, fill out the cells of the row with information about that event.
     *
     * @param eventID The ID of the event.
     * @param event The event to process.
     * @param row The row to fill out cells in.
     * @param details If true, fills out details using multiple cells.
     */
    private static void generateCSVForEvent(String eventID, Event event, Object[] row, boolean details) {
        // Find the index of the event, to fill out the right cells.
        int index = EVENTS.indexOf(eventID);
        if (index == -1) {
            // Unknown events are not included in the report
            return;
        }
        if (details) {
            // If details are requested, use multiple cells
            updateCell(row, index * COLUMNS_PER_EVENT + ROW_HEADER_COLUMNS, event.isSuccess());
            //updateCell(row, index * COLUMNS_PER_DETAILED_EVENT + ROW_HEADER_COLUMNS + 1, event.getDate());
            updateCell(row, index * COLUMNS_PER_EVENT + ROW_HEADER_COLUMNS + 2, event.getDetails());
        } else {
            // Else just fill out if it was a success.
            updateCell(row, index + ROW_HEADER_COLUMNS, event.isSuccess());
        }
    }

    /**
     * Update a cell in a row. If the cell is empty, insert the value. Otherwise add the value, by updating it to a 
     * comma-separated list of values.
     *
     * @param row The row to update.
     * @param index The index of the cell in the row to update.
     * @param value The value to update the cell with.
     */
    private static void updateCell(Object[] row, int index, Object value) {
        if (row[index] == null) {
            row[index] = value;
        } else {
            row[index] = row[index].toString() + "," + value.toString();
        }
    }
}
