CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       metal_buy_history JSON,
                       metal_sell_history JSON,
                       currency_buy_history JSON,
                       currency_sell_history JSON,
                       preferences JSON,
                       created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT (NOW() AT TIME ZONE 'UTC')
);