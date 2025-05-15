FROM postgres:15

# Install pgAudit extension
RUN apt-get update && apt-get install -y postgresql-15-pgaudit && rm -rf /var/lib/apt/lists/*

# Copy configuration files  
COPY postgresql.conf /etc/postgresql/postgresql.conf

# Copy initialization scripts
COPY init/ /docker-entrypoint-initdb.d/
RUN chmod -R 755 /docker-entrypoint-initdb.d/