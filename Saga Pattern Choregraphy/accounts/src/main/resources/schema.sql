CREATE TABLE accounts (
    account_number BIGSERIAL PRIMARY KEY,
    mobile_number VARCHAR(20) NOT NULL,
    account_type VARCHAR(100) NOT NULL,
    branch_address VARCHAR(200) NOT NULL,
    active_sw BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATE NOT NULL,
    created_by VARCHAR(20) NOT NULL,
    updated_at DATE,
    updated_by VARCHAR(20)
    );
