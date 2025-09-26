-- ===========================
-- 1) ENUM / DOMAIN TYPES
-- ===========================
CREATE TYPE account_type AS ENUM ('COURANT','EPARGNE','CREDIT');
CREATE TYPE account_status AS ENUM ('ACTIVE','CLOSED','SUSPENDED','PENDING');

CREATE TYPE transaction_type AS ENUM (
  'DEPOSIT','WITHDRAW','TRANSFERIN','TRANSFEROUT','FEE','FEEINCOME','TRANSFER_EXTERNAL'
);
CREATE TYPE transaction_status AS ENUM ('PENDING','SETTLED','FAILED');

CREATE TYPE credit_status AS ENUM ('PENDING','ACTIVE','LATE','CLOSED','REFUSED');

CREATE TYPE fee_mode AS ENUM ('FIX','PERCENT');

CREATE TYPE role_name AS ENUM ('ADMIN','MANAGER','TELLER','AUDITOR');

CREATE TYPE account_credit_relation AS ENUM ('REPAYMENT_ACCOUNT','GUARANTEE_ACCOUNT');

CREATE TYPE role_in_account AS ENUM ('OWNER','COOWNER','AUTHORIZED_USER');

CREATE TYPE schedule_status AS ENUM ('DUE','PAID','LATE','WAIVED');

-- ===========================
-- 2) Configuration tables
-- ===========================
CREATE TABLE fee_rules (
  fee_rule_id    SERIAL PRIMARY KEY,
  operation_type VARCHAR(100) NOT NULL, -- e.g. 'TRANSFER_EXTERNAL', 'WITHDRAW_FOREIGN'
  mode           fee_mode NOT NULL,
  value          NUMERIC(18,6) NOT NULL, -- if mode = PERCENT => 0..100; if FIX => currency amount
  currency       CHAR(3) NOT NULL,
  is_active      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
);

CREATE TABLE exchange_rates (
  rate_id      SERIAL PRIMARY KEY,
  from_currency CHAR(3) NOT NULL,
  to_currency   CHAR(3) NOT NULL,
  rate          NUMERIC(18,8) NOT NULL CHECK (rate > 0),
  last_updated  TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (from_currency, to_currency)
);

-- ===========================
-- 3) Core domain tables
-- ===========================
CREATE TABLE users (
  user_id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  customer_id VARCHAR(64),
  name                 VARCHAR(255) NOT NULL,
  email                VARCHAR(255) NOT NULL UNIQUE,
  phone_number         VARCHAR(50),
  address              TEXT,
  password_hash        VARCHAR(255),
  is_active            BOOLEAN NOT NULL DEFAULT TRUE,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE roles (
  role_id   SMALLSERIAL PRIMARY KEY,
  role_name role_name UNIQUE NOT NULL,
  description TEXT
);

-- Accounts
CREATE TABLE accounts (
  account_id        VARCHAR(64) PRIMARY KEY, -- ex: BK-2025-0001
  primary_owner_id  BIGINT REFERENCES users(user_id) ON DELETE SET NULL,
  balance           NUMERIC(18,2) NOT NULL DEFAULT 0, -- BigDecimal 2 decimals
  account_type      account_type NOT NULL,
  currency          CHAR(3) NOT NULL, -- ISO code (MAD, EUR, USD)
  status            account_status NOT NULL DEFAULT 'ACTIVE',
  overdraft_allowed BOOLEAN NOT NULL DEFAULT FALSE,
  overdraft_limit   NUMERIC(18,2) NOT NULL DEFAULT 0,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  closed_at         TIMESTAMPTZ,
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Many-to-many: users <-> accounts (joint accounts)
CREATE TABLE user_accounts (
  user_id       BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  account_id    VARCHAR(64) NOT NULL REFERENCES accounts(account_id) ON DELETE CASCADE,
  role_in_account role_in_account NOT NULL DEFAULT 'OWNER',
  joined_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, account_id)
);

-- Many-to-many: users <-> roles (flexible RBAC)
CREATE TABLE user_roles (
  user_id   BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  role_id   SMALLINT NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
  assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, role_id)
);

-- Transactions (double-entry via source/target account fields)
CREATE TABLE transactions (
  transaction_id    VARCHAR(64) PRIMARY KEY,
  source_account_id VARCHAR(64) REFERENCES accounts(account_id) ON DELETE SET NULL,
  target_account_id VARCHAR(64) REFERENCES accounts(account_id) ON DELETE SET NULL,
  transaction_type  transaction_type NOT NULL,
  amount            NUMERIC(18,2) NOT NULL CHECK (amount > 0),
  currency          CHAR(3) NOT NULL,
  status            transaction_status NOT NULL DEFAULT 'PENDING',
  fee_rule_id       INT REFERENCES fee_rules(fee_rule_id) ON DELETE SET NULL,
  aml_flag          BOOLEAN NOT NULL DEFAULT FALSE,
  transaction_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
  description       TEXT
);

-- Credits table
CREATE TABLE credits (
  credit_id         VARCHAR(64) PRIMARY KEY,
  customer_id       BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  linked_account_id VARCHAR(64) REFERENCES accounts(account_id) ON DELETE SET NULL, -- debit account for repayment
  loan_amount       NUMERIC(18,2) NOT NULL CHECK (loan_amount > 0),
  interest_rate     NUMERIC(8,4) NOT NULL CHECK (interest_rate >= 0), -- store as percent (e.g. 5.5)
  loan_term_months  INT NOT NULL CHECK (loan_term_months > 0),
  start_date        DATE,
  end_date          DATE,
  monthly_installment NUMERIC(18,2),
  credit_status     credit_status NOT NULL DEFAULT 'PENDING',
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT credit_dates CHECK (start_date IS NULL OR end_date IS NULL OR start_date <= end_date)
);

-- Pivot if a credit can be associated with multiple accounts (repayments from several accounts)
CREATE TABLE account_credits (
  account_id   VARCHAR(64) NOT NULL REFERENCES accounts(account_id) ON DELETE CASCADE,
  credit_id    VARCHAR(64) NOT NULL REFERENCES credits(credit_id) ON DELETE CASCADE,
  relation_type account_credit_relation NOT NULL DEFAULT 'REPAYMENT_ACCOUNT',
  PRIMARY KEY (account_id, credit_id)
);

-- Credit repayment schedule (échéancier)
CREATE TABLE credit_schedule (
  schedule_id    BIGSERIAL PRIMARY KEY,
  credit_id      VARCHAR(64) NOT NULL REFERENCES credits(credit_id) ON DELETE CASCADE,
  due_date       DATE NOT NULL,
  amount_due     NUMERIC(18,2) NOT NULL CHECK (amount_due >= 0),
  amount_paid    NUMERIC(18,2) NOT NULL DEFAULT 0 CHECK (amount_paid >= 0),
  status         schedule_status NOT NULL DEFAULT 'DUE',
  penalty_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
  paid_at        TIMESTAMPTZ
);

-- Audit / logs for traceability
CREATE TABLE audit_logs (
  log_id     BIGSERIAL PRIMARY KEY,
  user_id    BIGINT REFERENCES users(user_id),
  action     VARCHAR(120) NOT NULL, -- LOGIN, CREATE_ACCOUNT, EXECUTE_TRANSACTION, etc.
  object_type VARCHAR(60),
  object_id   VARCHAR(128),
  timestamp   TIMESTAMPTZ NOT NULL DEFAULT now(),
  details     JSONB -- free-form details for the audit entry
);

-- ===========================
-- 4) INDEXES / COMMON ACCESS PATHS
-- ===========================
CREATE INDEX idx_transactions_date ON transactions (transaction_date);
CREATE INDEX idx_transactions_status ON transactions (status);
CREATE INDEX idx_accounts_balance ON accounts (balance);
CREATE INDEX idx_credits_customer ON credits (customer_id);
CREATE INDEX idx_credit_schedule_due ON credit_schedule (due_date);

-- ===========================
-- 5) TRIGGER: auto-update updated_at on updates
-- ===========================
CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$;

-- attach to tables that have updated_at
CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_accounts_updated_at
BEFORE UPDATE ON accounts
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_credits_updated_at
BEFORE UPDATE ON credits
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_fee_rules_updated_at
BEFORE UPDATE ON fee_rules
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
