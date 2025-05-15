package com.example.productmgr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_time")
    private LocalDateTime logTime;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "database_name")
    private String databaseName;

    @Column(name = "process_id")
    private Integer processId;

    @Column(name = "connection_from")
    private String connectionFrom;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "session_line_num")
    private Long sessionLineNum;

    @Column(name = "command_tag")
    private String commandTag;

    @Column(name = "session_start_time")
    private LocalDateTime sessionStartTime;

    @Column(name = "virtual_transaction_id")
    private String virtualTransactionId;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "error_severity")
    private String errorSeverity;

    @Column(name = "sql_state_code")
    private String sqlStateCode;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(columnDefinition = "TEXT")
    private String hint;

    @Column(name = "internal_query", columnDefinition = "TEXT")
    private String internalQuery;

    @Column(name = "internal_query_pos")
    private Integer internalQueryPos;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(columnDefinition = "TEXT")
    private String query;

    @Column(name = "query_pos")
    private Integer queryPos;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Column(name = "application_name")
    private String applicationName;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getConnectionFrom() {
        return connectionFrom;
    }

    public void setConnectionFrom(String connectionFrom) {
        this.connectionFrom = connectionFrom;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSessionLineNum() {
        return sessionLineNum;
    }

    public void setSessionLineNum(Long sessionLineNum) {
        this.sessionLineNum = sessionLineNum;
    }

    public String getCommandTag() {
        return commandTag;
    }

    public void setCommandTag(String commandTag) {
        this.commandTag = commandTag;
    }

    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(LocalDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public String getVirtualTransactionId() {
        return virtualTransactionId;
    }

    public void setVirtualTransactionId(String virtualTransactionId) {
        this.virtualTransactionId = virtualTransactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getErrorSeverity() {
        return errorSeverity;
    }

    public void setErrorSeverity(String errorSeverity) {
        this.errorSeverity = errorSeverity;
    }

    public String getSqlStateCode() {
        return sqlStateCode;
    }

    public void setSqlStateCode(String sqlStateCode) {
        this.sqlStateCode = sqlStateCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getInternalQuery() {
        return internalQuery;
    }

    public void setInternalQuery(String internalQuery) {
        this.internalQuery = internalQuery;
    }

    public Integer getInternalQueryPos() {
        return internalQueryPos;
    }

    public void setInternalQueryPos(Integer internalQueryPos) {
        this.internalQueryPos = internalQueryPos;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getQueryPos() {
        return queryPos;
    }

    public void setQueryPos(Integer queryPos) {
        this.queryPos = queryPos;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}