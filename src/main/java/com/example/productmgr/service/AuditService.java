package com.example.productmgr.service;

import com.example.productmgr.model.AuditLog;
import com.example.productmgr.repository.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditLogRepository.findTop100ByOrderByLogTimeDesc();
    }

    public List<AuditLog> searchLogs(String userName, String commandTag, 
                                    LocalDateTime startTime, LocalDateTime endTime,
                                    int page, int size) {
        return auditLogRepository.searchLogs(userName, commandTag, startTime, endTime, 
                                           PageRequest.of(page, size)).getContent();
    }

    public long countLogs(String userName, String commandTag,
                         LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.countLogs(userName, commandTag, startTime, endTime);
    }

    public Map<String, Object> getAuditStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);

        // Total logs count
        stats.put("totalLogs", auditLogRepository.count());
        stats.put("last24HoursCount", auditLogRepository.countLogs(null, null, last24Hours, null));
        stats.put("last7DaysCount", auditLogRepository.countLogs(null, null, last7Days, null));
        stats.put("last30DaysCount", auditLogRepository.countLogs(null, null, last30Days, null));

        // Command statistics
        stats.put("commandStats24h", auditLogRepository.getCommandStats(last24Hours));
        stats.put("commandStats7d", auditLogRepository.getCommandStats(last7Days));

        // User statistics
        stats.put("userStats24h", auditLogRepository.getUserStats(last24Hours));
        stats.put("userStats7d", auditLogRepository.getUserStats(last7Days));

        // Daily statistics for chart
        stats.put("dailyStats30d", auditLogRepository.getDailyStats(last30Days));

        return stats;
    }
}