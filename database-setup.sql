-- Database setup script for EDC Management System
-- Run this script in PostgreSQL as a superuser

-- Create database
CREATE DATABASE edc_management;

-- Create user
CREATE USER edc_user WITH PASSWORD 'edc_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE edc_management TO edc_user;

-- Connect to the database
\c edc_management;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO edc_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO edc_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO edc_user;

-- Optional: Create tables manually (Spring Boot will auto-create them based on entities)
-- But here's the structure for reference:

/*
-- Terminal EDC table
CREATE TABLE IF NOT EXISTS terminal_edc (
    id BIGSERIAL PRIMARY KEY,
    terminal_id VARCHAR(255) NOT NULL UNIQUE,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    merchant_name VARCHAR(255),
    ip_address VARCHAR(45),
    port INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_ping TIMESTAMP
);

-- Echo Log table
CREATE TABLE IF NOT EXISTS echo_log (
    id BIGSERIAL PRIMARY KEY,
    terminal_id VARCHAR(255) NOT NULL,
    request_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    response_status VARCHAR(50),
    client_ip VARCHAR(45),
    user_agent TEXT,
    signature_valid BOOLEAN,
    error_message TEXT
);

-- Indexes for better performance
CREATE INDEX idx_terminal_edc_terminal_id ON terminal_edc(terminal_id);
CREATE INDEX idx_terminal_edc_status ON terminal_edc(status);
CREATE INDEX idx_terminal_edc_location ON terminal_edc(location);

CREATE INDEX idx_echo_log_terminal_id ON echo_log(terminal_id);
CREATE INDEX idx_echo_log_timestamp ON echo_log(request_timestamp);
CREATE INDEX idx_echo_log_status ON echo_log(response_status);

-- Sample data
INSERT INTO terminal_edc (terminal_id, location, status, merchant_name, ip_address, port) VALUES
('EDC001', 'Jakarta Pusat', 'ACTIVE', 'Merchant A', '192.168.1.100', 8080),
('EDC002', 'Jakarta Selatan', 'ACTIVE', 'Merchant B', '192.168.1.101', 8080),
('EDC003', 'Bandung', 'INACTIVE', 'Merchant C', '192.168.1.102', 8080),
('EDC004', 'Surabaya', 'ACTIVE', 'Merchant D', '192.168.1.103', 8080),
('EDC005', 'Medan', 'MAINTENANCE', 'Merchant E', '192.168.1.104', 8080);
*/