FROM postgres:15

# Install pgAudit extension
RUN apt-get update && apt-get install -y postgresql-15-pgaudit && rm -rf /var/lib/apt/lists/*

# Copy configuration files  
COPY postgresql.conf /etc/postgresql/postgresql.conf
COPY init-pgaudit.sql /docker-entrypoint-initdb.d/10-init-pgaudit.sql