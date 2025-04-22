package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileResetUtils {
    private static final String[] PRESET_FILES = {
        "HDBOfficerList.csv",
        "HDBManagerList.csv",
        "BTOProjectList.csv",
        "ApplicantList.csv"
    };

    public static void resetCsvFiles() {
        try {
            // Get all CSV files from the data directory
            File dataDir = new File("data");
            File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".csv"));

            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    List<String> content = new ArrayList<>();
                    
                    // Read the file content
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.trim().isEmpty()) {  // Skip empty lines
                                content.add(line);
                            }
                        }
                    }

                    // For preset files, keep all original content
                    // For other files, keep only the header
                    try (FileWriter writer = new FileWriter(file, false)) {
                        if (isPresetFile(fileName)) {
                            // Write all original content for preset files
                            for (String line : content) {
                                writer.write(line + "\n");
                            }
                        } else {
                            // Write only header for non-preset files
                            if (!content.isEmpty()) {
                                writer.write(content.get(0) + "\n");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error resetting CSV files: " + e.getMessage());
            e.printStackTrace();  // Add stack trace for debugging
        } catch (Exception e) {
            System.out.println("Unexpected error while resetting CSV files: " + e.getMessage());
            e.printStackTrace();  // Add stack trace for debugging
        }
    }

    private static boolean isPresetFile(String fileName) {
        if (fileName == null) {
            return false;
        }
        for (String presetFile : PRESET_FILES) {
            if (presetFile.equals(fileName)) {
                return true;
            }
        }
        return false;
    }
} 