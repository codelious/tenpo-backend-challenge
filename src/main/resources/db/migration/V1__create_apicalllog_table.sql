CREATE TABLE api_call_log (
    id SERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    parameters TEXT,
    response TEXT,
    http_status INTEGER NOT NULL
);