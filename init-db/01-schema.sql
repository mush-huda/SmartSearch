CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    artist VARCHAR(255),
    city VARCHAR(100),
    genre VARCHAR(100),
    venue VARCHAR(255),
    event_date DATE,
    price_eur NUMERIC(8,2),
    available_seats INT
);
