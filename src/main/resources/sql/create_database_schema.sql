-- ==========================
-- Users table
-- ==========================
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- ADMIN, TELLER, MANAGER, AUDITOR
    created_at TIMESTAMP DEFAULT NOW(),
    is_active BOOLEAN DEFAULT TRUE
);

-- ==========================
-- Clients table
-- ==========================
CREATE TABLE clients (
    client_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    date_of_birth DATE,
    registration_date DATE DEFAULT CURRENT_DATE,
    is_active BOOLEAN DEFAULT TRUE
);

-- ==========================
-- Accounts table
-- ==========================
CREATE TABLE accounts (
    account_id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    balance NUMERIC(15,2) DEFAULT 0,
    account_type VARCHAR(20) NOT NULL, -- CURRENT, SAVINGS, CREDIT
    currency VARCHAR(10) DEFAULT 'MAD',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

-- ==========================
-- Credits table
-- ==========================
CREATE TABLE credits (
    credit_id SERIAL PRIMARY KEY,
    account_id INT NOT NULL,
    loan_amount NUMERIC(15,2) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    loan_term_months INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    credit_status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, LATE, CLOSED
    CONSTRAINT fk_account_credit FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

-- ==========================
-- Fee rules table
-- ==========================
CREATE TABLE fee_rules (
    fee_id SERIAL PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL, -- TRANSFER, WITHDRAWFOREIGN, DEPOSIT, ETC
    mode VARCHAR(10) NOT NULL, -- FIX, PERCENT
    value NUMERIC(10,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'MAD',
    is_active BOOLEAN DEFAULT TRUE
);

-- ==========================
-- Transactions table
-- ==========================
CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    source_account_id INT NOT NULL,
    target_account_id INT,
    transaction_type VARCHAR(30) NOT NULL, -- DEPOSIT, WITHDRAW, TRANSFER
    amount NUMERIC(15,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SETTLED, FAILED
    transaction_date TIMESTAMP DEFAULT NOW(),
    fee_id INT, -- reference to the applied fee
    CONSTRAINT fk_source_account FOREIGN KEY (source_account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_target_account FOREIGN KEY (target_account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_fee FOREIGN KEY (fee_id) REFERENCES fee_rules(fee_id)
);
