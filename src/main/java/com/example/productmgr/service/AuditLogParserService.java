package com.example.productmgr.service;

import com.example.productmgr.model.AuditLog;
import com.example.productmgr.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuditLogParserService {

    private final AuditLogRepository auditLogRepository;
    private static final String LOG_PATH = "/var/lib/postgresql/data/pg_log/";
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+) \\[([\\d]+)\\] ([^@]*)@([^ ]*) LOG:  AUDIT: (.*)$"
    );
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // Track last processed position
    private Long lastProcessedPosition = 0L;
    private String lastProcessedFile = "";

    public AuditLogParserService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Scheduled(fixedDelay = 60000) // Run every minute
    public void parseAndStoreAuditLogs() {
        try {
            // Get the latest PostgreSQL log file
            Path logDir = Paths.get(LOG_PATH);
            if (!Files.exists(logDir)) {
                return;
            }

            Path latestLogFile = Files.list(logDir)
                .filter(path -> path.toString().endsWith(".log"))
                .max((p1, p2) -> {
                    try {
                        return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .orElse(null);

            if (latestLogFile == null) {
                return;
            }

            // Check if we're processing a new file
            String currentFileName = latestLogFile.getFileName().toString();
            if (!currentFileName.equals(lastProcessedFile)) {
                lastProcessedPosition = 0L;
                lastProcessedFile = currentFileName;
            }

            // Parse and store logs
            List<AuditLog> auditLogs = parseLogFile(latestLogFile);
            if (!auditLogs.isEmpty()) {
                auditLogRepository.saveAll(auditLogs);
            }
            
        } catch (Exception e) {
            // Log error but don't throw to prevent scheduler from stopping
            System.err.println("Error parsing audit logs: " + e.getMessage());
        }
    }

    private List<AuditLog> parseLogFile(Path logFile) throws IOException {
        List<AuditLog> auditLogs = new ArrayList<>();
        
        try (RandomAccessFile raf = new RandomAccessFile(logFile.toFile(), "r")) {
            // Move to last processed position
            raf.seek(lastProcessedPosition);
            
            String line;
            while ((line = raf.readLine()) != null) {
                AuditLog log = parseLogLine(line);
                if (log != null) {
                    auditLogs.add(log);
                }
            }
            
            // Update last processed position
            lastProcessedPosition = raf.getFilePointer();
        }
        
        return auditLogs;
    }

    private AuditLog parseLogLine(String line) {
        if (line == null || !line.contains("AUDIT:")) {
            return null;
        }

        try {
            // Parse CSV format for pgAudit logs
            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (parts.length < 10) {
                return null;
            }

            AuditLog log = new AuditLog();
            
            // Parse timestamp
            String timestamp = parts[0].replace("\"", "");
            log.setLogTime(LocalDateTime.parse(timestamp.substring(0, 23), DATE_FORMAT));
            
            // Parse other fields
            log.setUserName(parts[1].replace("\"", ""));
            log.setDatabaseName(parts[2].replace("\"", ""));
            log.setProcessId(Integer.parseInt(parts[3].replace("\"", "")));
            log.setConnectionFrom(parts[4].replace("\"", ""));
            log.setSessionId(parts[5].replace("\"", ""));
            log.setSessionLineNum(Long.parseLong(parts[6].replace("\"", "")));
            log.setCommandTag(parts[7].replace("\"", ""));
            
            // Extract query from message
            if (parts.length > 13 && parts[13].contains("AUDIT:")) {
                String message = parts[13].replace("\"", "");
                String[] auditParts = message.split(",");
                if (auditParts.length > 0) {
                    log.setCommandTag(auditParts[0].replace("AUDIT: ", ""));
                }
                if (auditParts.length > 3) {
                    log.setQuery(auditParts[3]);
                }
            }
            
            log.setApplicationName(parts.length > 22 ? parts[22].replace("\"", "") : "");
            
            return log;
            
        } catch (Exception e) {
            // Skip malformed lines
            return null;
        }
    }

    public void rotateOldLogs() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            // Delete logs older than 30 days
            long deletedCount = auditLogRepository.deleteByLogTimeBefore(cutoffDate);
            System.out.println("Deleted " + deletedCount + " old audit logs");
        } catch (Exception e) {
            System.err.println("Error rotating logs: " + e.getMessage());
        }
    }
}