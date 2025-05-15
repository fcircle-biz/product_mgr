-- pgAudit extension setup
CREATE EXTENSION IF NOT EXISTS pgaudit;

-- Set pgAudit configuration
SET pgaudit.log = 'ALL';
SET pgaudit.log_catalog = ON;
SET pgaudit.log_client = ON;
SET pgaudit.log_level = 'log';
SET pgaudit.log_parameter = ON;
SET pgaudit.log_relation = ON;
SET pgaudit.log_statement_once = OFF;

-- Make settings persistent
ALTER SYSTEM SET pgaudit.log = 'ALL';
ALTER SYSTEM SET pgaudit.log_catalog = ON;
ALTER SYSTEM SET pgaudit.log_client = ON;
ALTER SYSTEM SET pgaudit.log_level = 'log';
ALTER SYSTEM SET pgaudit.log_parameter = ON;
ALTER SYSTEM SET pgaudit.log_relation = ON;
ALTER SYSTEM SET pgaudit.log_statement_once = OFF;

-- Create audit log table for dashboard
CREATE TABLE IF NOT EXISTS audit_logs (
    id SERIAL PRIMARY KEY,
    log_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_name TEXT,
    database_name TEXT,
    process_id INTEGER,
    connection_from TEXT,
    session_id TEXT,
    session_line_num BIGINT,
    command_tag TEXT,
    session_start_time TIMESTAMP WITH TIME ZONE,
    virtual_transaction_id TEXT,
    transaction_id BIGINT,
    error_severity TEXT,
    sql_state_code TEXT,
    message TEXT,
    detail TEXT,
    hint TEXT,
    internal_query TEXT,
    internal_query_pos INTEGER,
    context TEXT,
    query TEXT,
    query_pos INTEGER,
    location TEXT,
    application_name TEXT
);

-- Create function to parse and insert audit logs
CREATE OR REPLACE FUNCTION parse_audit_log() RETURNS void AS $$
BEGIN
    -- This function would be called by an external process to parse pg_log files
    -- and insert them into the audit_logs table
    NULL;
END;
$$ LANGUAGE plpgsql;