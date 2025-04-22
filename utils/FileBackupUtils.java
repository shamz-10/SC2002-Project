package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class FileBackupUtils {
    private static final String BACKUP_DIR = "data/backup/";
    private static Map<String, String> originalFiles = new HashMap<>();

    public static void backupOriginalFiles() {
        try {
            // Create backup directory if it doesn't exist
            new File(BACKUP_DIR).mkdirs();

            // Get all CSV files from the data directory
            File dataDir = new File("data");
            File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".csv"));

            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    Path source = Paths.get(file.getPath());
                    Path backup = Paths.get(BACKUP_DIR + fileName);
                    
                    // Create backup
                    Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
                    originalFiles.put(fileName, backup.toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Error creating file backups: " + e.getMessage());
        }
    }

    public static void restoreOriginalFiles() {
        try {
            for (Map.Entry<String, String> entry : originalFiles.entrySet()) {
                String fileName = entry.getKey();
                Path backup = Paths.get(entry.getValue());
                Path original = Paths.get("data/" + fileName);
                
                // Restore from backup
                Files.copy(backup, original, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.out.println("Error restoring file backups: " + e.getMessage());
        }
    }
} 