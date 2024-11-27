-- Добавляем пользователей
INSERT INTO users (id, name, email) VALUES (1, 'Owner', 'owner@example.com');
INSERT INTO users (id, name, email) VALUES (2, 'Booker', 'booker@example.com');

-- Добавляем предметы
INSERT INTO items (id, name, description, available, owner_id)
VALUES (1, 'Item 1', 'Available item', TRUE, 1),
       (2, 'Item 2', 'Available item', TRUE, 2);

-- Добавляем бронирования
INSERT INTO bookings (id, start_date, end_date, status, item_id, booker_id)
VALUES (1, DATEADD(DAY, -1, CURRENT_TIMESTAMP), DATEADD(DAY, 1, CURRENT_TIMESTAMP), 'WAITING', 1, 2);