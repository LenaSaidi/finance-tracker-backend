CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "transaction" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    date TIMESTAMP NOT NULL,
    description TEXT,
    category_id UUID,
    CONSTRAINT fk_transaction_category
        FOREIGN KEY (category_id)
        REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS goal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    target_amount DECIMAL(10,2) NOT NULL,
    current_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    deadline DATE,
    created_date TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO category (name) VALUES
    ('Food'),
    ('Rent'),
    ('Entertainment'),
    ('Transport'),
    ('Health'),
    ('Education'),
    ('Shopping'),
    ('Salary'),
    ('Freelance'),
    ('Other')
ON CONFLICT (name) DO NOTHING;

