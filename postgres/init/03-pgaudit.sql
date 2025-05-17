-- pgAudit拡張機能の有効化
CREATE EXTENSION IF NOT EXISTS pgaudit;

-- pgAuditのログ設定
ALTER SYSTEM SET pgaudit.log = 'ALL';
ALTER SYSTEM SET pgaudit.log_parameter = ON;
ALTER SYSTEM SET pgaudit.log_statement_once = OFF;
ALTER SYSTEM SET pgaudit.log_catalog = ON;

-- 設定をリロード
SELECT pg_reload_conf();