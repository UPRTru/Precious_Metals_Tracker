CREATE TABLE notification_log (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_email VARCHAR(255) NOT NULL,
                                  asset_name VARCHAR(100) NOT NULL,
                                  current_price NUMERIC(19, 4) NOT NULL,
                                  target_price NUMERIC(19, 4) NOT NULL,
                                  operation VARCHAR(10) NOT NULL,
                                  sent_at TIMESTAMP WITHOUT TIME ZONE DEFAULT (NOW() AT TIME ZONE 'UTC')
);