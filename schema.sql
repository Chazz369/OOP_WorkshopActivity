-- ============================================================
-- Inventory Management System - PostgreSQL Schema
-- ============================================================

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- USERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'staff'
                  CHECK (role IN ('admin', 'staff')),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ============================================================
-- CATEGORIES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS categories (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- ============================================================
-- ITEMS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS items (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku         VARCHAR(50)    NOT NULL UNIQUE,
    name        VARCHAR(150)   NOT NULL,
    category_id INT            NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    unit_price  NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    quantity    INT            NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    threshold   INT            NOT NULL DEFAULT 5 CHECK (threshold >= 0),
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- SALES LOG TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS sales_log (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id       UUID NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    quantity_sold INT  NOT NULL CHECK (quantity_sold > 0),
    sold_by       UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    sold_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_items_category  ON items(category_id);
CREATE INDEX IF NOT EXISTS idx_items_sku       ON items(sku);
CREATE INDEX IF NOT EXISTS idx_sales_item      ON sales_log(item_id);
CREATE INDEX IF NOT EXISTS idx_sales_sold_by   ON sales_log(sold_by);
CREATE INDEX IF NOT EXISTS idx_sales_sold_at   ON sales_log(sold_at);

-- ============================================================
-- SEED DATA - Default Categories
-- ============================================================
INSERT INTO categories (name) VALUES
    ('Electronics'),
    ('Clothing'),
    ('Food & Beverages'),
    ('Office Supplies'),
    ('Hardware')
ON CONFLICT (name) DO NOTHING;
