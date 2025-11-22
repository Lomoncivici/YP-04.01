CREATE TABLE roles (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id          SERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(100),
    email       VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE passenger_profiles (
    id              SERIAL PRIMARY KEY,
    user_id         INT UNIQUE,
    last_name       VARCHAR(50)  NOT NULL,
    first_name      VARCHAR(50)  NOT NULL,
    middle_name     VARCHAR(50),
    birth_date      DATE         NOT NULL,
    passport_number VARCHAR(20)  NOT NULL,
    phone           VARCHAR(20),
    CONSTRAINT fk_passenger_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE airports (
    id       SERIAL PRIMARY KEY,
    code     CHAR(3)      NOT NULL UNIQUE,
    name     VARCHAR(100) NOT NULL,
    city     VARCHAR(100) NOT NULL,
    country  VARCHAR(100) NOT NULL
);

CREATE TABLE routes (
    id                    SERIAL PRIMARY KEY,
    departure_airport_id  INT NOT NULL,
    arrival_airport_id    INT NOT NULL,
    distance_km           INT NOT NULL CHECK (distance_km > 0),

    CONSTRAINT fk_route_departure_airport
        FOREIGN KEY (departure_airport_id) REFERENCES airports(id),

    CONSTRAINT fk_route_arrival_airport
        FOREIGN KEY (arrival_airport_id) REFERENCES airports(id),

    CONSTRAINT chk_route_airports_different
        CHECK (departure_airport_id <> arrival_airport_id)
);

CREATE TABLE aircrafts (
    id             SERIAL PRIMARY KEY,
    model          VARCHAR(50) NOT NULL,
    total_seats    INT         NOT NULL CHECK (total_seats > 0),
    economy_seats  INT         NOT NULL CHECK (economy_seats >= 0),
    business_seats INT         NOT NULL CHECK (business_seats >= 0),

    CONSTRAINT chk_aircraft_seats_sum
        CHECK (economy_seats + business_seats <= total_seats)
);

CREATE TABLE flights (
    id              SERIAL PRIMARY KEY,
    flight_number   VARCHAR(10)  NOT NULL UNIQUE,
    route_id        INT          NOT NULL,
    aircraft_id     INT          NOT NULL,
    departure_time  TIMESTAMP    NOT NULL,
    arrival_time    TIMESTAMP    NOT NULL,
    base_price      NUMERIC(10,2) NOT NULL CHECK (base_price > 0),
    status          VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED', 

    CONSTRAINT fk_flight_route
        FOREIGN KEY (route_id) REFERENCES routes(id),

    CONSTRAINT fk_flight_aircraft
        FOREIGN KEY (aircraft_id) REFERENCES aircrafts(id),

    CONSTRAINT chk_flight_times
        CHECK (arrival_time > departure_time)
);

CREATE TABLE bookings (
    id           SERIAL PRIMARY KEY,
    user_id      INT          NOT NULL,
    flight_id    INT          NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status       VARCHAR(20)  NOT NULL DEFAULT 'NEW',
    total_price  NUMERIC(10,2) NOT NULL CHECK (total_price >= 0),

    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT fk_booking_flight
        FOREIGN KEY (flight_id) REFERENCES flights(id)
);

CREATE TABLE tickets (
    id              SERIAL PRIMARY KEY,
    booking_id      INT          NOT NULL,
    passenger_id    INT          NOT NULL,
    seat            VARCHAR(5)   NOT NULL,
    fare_class      VARCHAR(20)  NOT NULL,
    price           NUMERIC(10,2) NOT NULL CHECK (price > 0),

    CONSTRAINT fk_ticket_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,

    CONSTRAINT fk_ticket_passenger
        FOREIGN KEY (passenger_id) REFERENCES passenger_profiles(id),

    CONSTRAINT uq_ticket_booking_seat UNIQUE (booking_id, seat)
);

INSERT INTO roles (name) VALUES
    ('ROLE_USER'),
    ('ROLE_MANAGER'),
    ('ROLE_ADMIN');
