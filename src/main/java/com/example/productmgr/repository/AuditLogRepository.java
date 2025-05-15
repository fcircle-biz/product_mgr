package com.example.productmgr.repository;

import com.example.productmgr.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findTop100ByOrderByLogTimeDesc();

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:userName IS NULL OR a.userName = :userName) AND " +
           "(:commandTag IS NULL OR a.commandTag = :commandTag) AND " +
           "(:startTime IS NULL OR a.logTime >= :startTime) AND " +
           "(:endTime IS NULL OR a.logTime <= :endTime) " +
           "ORDER BY a.logTime DESC")
    Page<AuditLog> searchLogs(@Param("userName") String userName,
                             @Param("commandTag") String commandTag,
                             @Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime,
                             Pageable pageable);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE " +
           "(:userName IS NULL OR a.userName = :userName) AND " +
           "(:commandTag IS NULL OR a.commandTag = :commandTag) AND " +
           "(:startTime IS NULL OR a.logTime >= :startTime) AND " +
           "(:endTime IS NULL OR a.logTime <= :endTime)")
    long countLogs(@Param("userName") String userName,
                   @Param("commandTag") String commandTag,
                   @Param("startTime") LocalDateTime startTime,
                   @Param("endTime") LocalDateTime endTime);

    @Query("SELECT a.commandTag as commandTag, COUNT(a) as count " +
           "FROM AuditLog a " +
           "WHERE a.logTime >= :startTime " +
           "GROUP BY a.commandTag " +
           "ORDER BY COUNT(a) DESC")
    List<Map<String, Object>> getCommandStats(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT a.userName as userName, COUNT(a) as count " +
           "FROM AuditLog a " +
           "WHERE a.logTime >= :startTime " +
           "GROUP BY a.userName " +
           "ORDER BY COUNT(a) DESC")
    List<Map<String, Object>> getUserStats(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT DATE(a.logTime) as date, COUNT(a) as count " +
           "FROM AuditLog a " +
           "WHERE a.logTime >= :startTime " +
           "GROUP BY DATE(a.logTime) " +
           "ORDER BY DATE(a.logTime)")
    List<Map<String, Object>> getDailyStats(@Param("startTime") LocalDateTime startTime);

    long deleteByLogTimeBefore(LocalDateTime cutoffDate);
}