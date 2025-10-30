package Game;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages speedrun records for each level
 * Loads and saves best times to speedrun_records.json
 */
public class SpeedrunRecordManager {
    private static final String RECORDS_FILE = "speedrun_records.json";
    private static SpeedrunRecordManager instance;
    private Map<Integer, Long> bestTimes; // levelId -> best time in milliseconds

    private SpeedrunRecordManager() {
        bestTimes = new HashMap<>();
        loadRecords();
    }

    public static SpeedrunRecordManager getInstance() {
        if (instance == null) {
            instance = new SpeedrunRecordManager();
        }
        return instance;
    }

    /**
     * Load records from JSON file
     */
    private void loadRecords() {
        File file = new File(RECORDS_FILE);
        if (!file.exists()) {
            return; // No records yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            parseJson(json.toString());
        } catch (IOException e) {
            System.err.println("Error loading speedrun records: " + e.getMessage());
        }
    }

    /**
     * Simple JSON parser for our records format
     * Format: {"records":[{"levelId":0,"time":12345},{"levelId":1,"time":23456}]}
     */
    private void parseJson(String json) {
        bestTimes.clear();

        // Remove whitespace and outer braces
        json = json.trim().replaceAll("\\s+", "");

        // Find the records array
        int recordsStart = json.indexOf("[");
        int recordsEnd = json.lastIndexOf("]");

        if (recordsStart == -1 || recordsEnd == -1) {
            return;
        }

        String recordsArray = json.substring(recordsStart + 1, recordsEnd);

        // Split by record objects
        String[] records = recordsArray.split("\\},\\{");

        for (String record : records) {
            // Clean up the record string
            record = record.replace("{", "").replace("}", "").replace("\"", "");

            if (record.isEmpty()) continue;

            // Parse levelId and time
            String[] pairs = record.split(",");
            Integer levelId = null;
            Long time = null;

            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    if (key.equals("levelId")) {
                        levelId = Integer.parseInt(value);
                    } else if (key.equals("time")) {
                        time = Long.parseLong(value);
                    }
                }
            }

            if (levelId != null && time != null) {
                bestTimes.put(levelId, time);
            }
        }
    }

    /**
     * Save records to JSON file
     */
    private void saveRecords() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RECORDS_FILE))) {
            writer.println("{");
            writer.println("  \"records\": [");

            int count = 0;
            for (Map.Entry<Integer, Long> entry : bestTimes.entrySet()) {
                writer.print("    {\"levelId\": " + entry.getKey() + ", \"time\": " + entry.getValue() + "}");
                if (count < bestTimes.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
                count++;
            }

            writer.println("  ]");
            writer.println("}");
        } catch (IOException e) {
            System.err.println("Error saving speedrun records: " + e.getMessage());
        }
    }

    /**
     * Check if this time is a new record and save it if so
     * @param levelId The level that was completed
     * @param timeMs The completion time in milliseconds
     * @return true if this was a new record, false otherwise
     */
    public boolean saveRecord(int levelId, long timeMs) {
        Long currentBest = bestTimes.get(levelId);

        // If no record exists or new time is better, save it
        if (currentBest == null || timeMs < currentBest) {
            bestTimes.put(levelId, timeMs);
            saveRecords();
            return true;
        }

        return false;
    }

    /**
     * Get the best time for a level
     * @param levelId The level to get the record for
     * @return The best time in milliseconds, or null if no record exists
     */
    public Long getBestTime(int levelId) {
        return bestTimes.get(levelId);
    }

    /**
     * Format a time in milliseconds as MM:SS.CS (minutes:seconds.centiseconds)
     * @param timeMs Time in milliseconds
     * @return Formatted string like "01:23.45"
     */
    public static String formatTime(long timeMs) {
        long minutes = (timeMs / 1000) / 60;
        long seconds = (timeMs / 1000) % 60;
        long centiseconds = (timeMs % 1000) / 10;
        return String.format("%02d:%02d.%02d", minutes, seconds, centiseconds);
    }

    /**
     * Get formatted best time for display, or "No Record" if none exists
     * @param levelId The level to get the record for
     * @return Formatted time string or "No Record"
     */
    public String getBestTimeFormatted(int levelId) {
        Long bestTime = getBestTime(levelId);
        if (bestTime == null) {
            return "No Record";
        }
        return formatTime(bestTime);
    }
}
