-- ==========================================
-- 1. РОЛИ
-- ==========================================

INSERT INTO roles (name) VALUES
    ('ROLE_USER'),
    ('ROLE_MANAGER'),
    ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;


-- ==========================================
-- 2. ПОЛЬЗОВАТЕЛИ
-- ==========================================
-- Пароль пока простой текст 'password'. Потом заменим на хэши.

INSERT INTO users (username, password, full_name, email, enabled)
VALUES 
    ('admin',   'password', 'Администратор Системы', 'admin@example.com',   TRUE),
    ('manager', 'password', 'Менеджер Бронирования', 'manager@example.com', TRUE),
    ('user1',   'password', 'Иванов Иван Иванович',  'user1@example.com',   TRUE)
ON CONFLICT (username) DO NOTHING;


-- ==========================================
-- 3. СВЯЗКА ПОЛЬЗОВАТЕЛЕЙ И РОЛЕЙ
-- ==========================================

-- admin -> ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- manager -> ROLE_MANAGER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'manager' AND r.name = 'ROLE_MANAGER'
ON CONFLICT DO NOTHING;

-- user1 -> ROLE_USER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'user1' AND r.name = 'ROLE_USER'
ON CONFLICT DO NOTHING;


-- ==========================================
-- 4. ПРОФИЛЬ ПАССАЖИРА ДЛЯ user1
-- ==========================================

INSERT INTO passenger_profiles (
    user_id, last_name, first_name, middle_name,
    birth_date, passport_number, phone
)
SELECT 
    u.id,
    'Иванов', 'Иван', 'Иванович',
    DATE '1995-04-15',
    '1234 567890',
    '+7-900-123-45-67'
FROM users u
WHERE u.username = 'user1'
ON CONFLICT (user_id) DO NOTHING;


-- ==========================================
-- 5. АЭРОПОРТЫ
-- ==========================================

INSERT INTO airports (code, name, city, country)
VALUES
    ('SVO', 'Шереметьево',  'Москва', 'Россия'),
    ('DME', 'Домодедово',   'Москва', 'Россия'),
    ('LED', 'Пулково',      'Санкт-Петербург', 'Россия'),
    ('AER', 'Сочи',         'Сочи', 'Россия')
ON CONFLICT (code) DO NOTHING;


-- ==========================================
-- 6. МАРШРУТЫ
-- ==========================================

-- SVO -> LED
INSERT INTO routes (departure_airport_id, arrival_airport_id, distance_km)
SELECT a1.id, a2.id, 630
FROM airports a1, airports a2
WHERE a1.code = 'SVO' AND a2.code = 'LED'
LIMIT 1;

-- LED -> SVO
INSERT INTO routes (departure_airport_id, arrival_airport_id, distance_km)
SELECT a1.id, a2.id, 630
FROM airports a1, airports a2
WHERE a1.code = 'LED' AND a2.code = 'SVO'
LIMIT 1;

-- SVO -> AER
INSERT INTO routes (departure_airport_id, arrival_airport_id, distance_km)
SELECT a1.id, a2.id, 1360
FROM airports a1, airports a2
WHERE a1.code = 'SVO' AND a2.code = 'AER'
LIMIT 1;


-- ==========================================
-- 7. САМОЛЁТЫ
-- ==========================================

INSERT INTO aircrafts (model, total_seats, economy_seats, business_seats)
VALUES
    ('Airbus A320', 180, 156, 24),
    ('Boeing 737-800', 190, 162, 28);


-- ==========================================
-- 8. РЕЙСЫ
-- ==========================================

-- Для примера возьмём несколько дат декабря 2025 года.

-- Рейс SU1001: SVO -> LED
INSERT INTO flights (flight_number, route_id, aircraft_id, departure_time, arrival_time, base_price, status)
SELECT 
    'SU1001',
    r.id,
    a.id,
    TIMESTAMP '2025-12-01 08:00:00',
    TIMESTAMP '2025-12-01 09:30:00',
    7500.00,
    'SCHEDULED'
FROM routes r, aircrafts a
WHERE r.id = (
        SELECT r2.id FROM routes r2
        JOIN airports d ON r2.departure_airport_id = d.id
        JOIN airports arr ON r2.arrival_airport_id = arr.id
        WHERE d.code = 'SVO' AND arr.code = 'LED'
        LIMIT 1
    )
  AND a.model = 'Airbus A320'
LIMIT 1;

-- Рейс SU1002: LED -> SVO
INSERT INTO flights (flight_number, route_id, aircraft_id, departure_time, arrival_time, base_price, status)
SELECT 
    'SU1002',
    r.id,
    a.id,
    TIMESTAMP '2025-12-01 18:00:00',
    TIMESTAMP '2025-12-01 19:30:00',
    7200.00,
    'SCHEDULED'
FROM routes r, aircrafts a
WHERE r.id = (
        SELECT r2.id FROM routes r2
        JOIN airports d ON r2.departure_airport_id = d.id
        JOIN airports arr ON r2.arrival_airport_id = arr.id
        WHERE d.code = 'LED' AND arr.code = 'SVO'
        LIMIT 1
    )
  AND a.model = 'Airbus A320'
LIMIT 1;

-- Рейс SU2001: SVO -> AER
INSERT INTO flights (flight_number, route_id, aircraft_id, departure_time, arrival_time, base_price, status)
SELECT 
    'SU2001',
    r.id,
    a.id,
    TIMESTAMP '2025-12-05 10:00:00',
    TIMESTAMP '2025-12-05 12:30:00',
    9500.00,
    'SCHEDULED'
FROM routes r, aircrafts a
WHERE r.id = (
        SELECT r2.id FROM routes r2
        JOIN airports d ON r2.departure_airport_id = d.id
        JOIN airports arr ON r2.arrival_airport_id = arr.id
        WHERE d.code = 'SVO' AND arr.code = 'AER'
        LIMIT 1
    )
  AND a.model = 'Boeing 737-800'
LIMIT 1;


-- ==========================================
-- 9. БРОНИРОВАНИЯ ДЛЯ user1
-- ==========================================

-- Бронирование №1: user1 на рейс SU1001 (SVO -> LED)
INSERT INTO bookings (user_id, flight_id, created_at, status, total_price)
SELECT 
    u.id,
    f.id,
    TIMESTAMP '2025-11-20 12:00:00',
    'PAID',
    7500.00
FROM users u, flights f
WHERE u.username = 'user1' AND f.flight_number = 'SU1001'
LIMIT 1;

-- Бронирование №2: user1 на рейс SU2001 (SVO -> AER)
INSERT INTO bookings (user_id, flight_id, created_at, status, total_price)
SELECT 
    u.id,
    f.id,
    TIMESTAMP '2025-11-21 15:30:00',
    'NEW',
    9500.00
FROM users u, flights f
WHERE u.username = 'user1' AND f.flight_number = 'SU2001'
LIMIT 1;


-- ==========================================
-- 10. БИЛЕТЫ ПО ЭТИМ БРОНИРОВАНИЯМ
-- ==========================================

-- Для удобства найдём id пассажира user1
-- и id его бронирований через подзапросы в INSERT.

-- Билет 1: бронирование на SU1001, место 12A
INSERT INTO tickets (booking_id, passenger_id, seat, fare_class, price)
SELECT 
    b.id,
    p.id,
    '12A',
    'ECONOMY',
    7500.00
FROM bookings b
JOIN users u ON b.user_id = u.id
JOIN flights f ON b.flight_id = f.id
JOIN passenger_profiles p ON p.user_id = u.id
WHERE u.username = 'user1' AND f.flight_number = 'SU1001'
LIMIT 1;

-- Билет 2: бронирование на SU2001, место 5C
INSERT INTO tickets (booking_id, passenger_id, seat, fare_class, price)
SELECT 
    b.id,
    p.id,
    '5C',
    'ECONOMY',
    9500.00
FROM bookings b
JOIN users u ON b.user_id = u.id
JOIN flights f ON b.flight_id = f.id
JOIN passenger_profiles p ON p.user_id = u.id
WHERE u.username = 'user1' AND f.flight_number = 'SU2001'
LIMIT 1;
