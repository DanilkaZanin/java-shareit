-- Добавляем пользователей
INSERT INTO users (id, name, email) VALUES (1, 'John Doe', 'john.doe@example.com');
INSERT INTO users (id, name, email) VALUES (2, 'Jane Smith', 'jane.smith@example.com');

-- Добавляем запросы
INSERT INTO requests (id, description, created, requestor_id)
VALUES (1, 'Request for an item', CURRENT_TIMESTAMP, 1);

-- Добавляем предметы
INSERT INTO items (id, name, description, available, owner_id, request_id)
VALUES (1, 'Item 1', 'Description for item 1', TRUE, 2, 1);

-- Добавляем комментарии
INSERT INTO comments (id, text, item_id, author_id, created)
VALUES (1, 'Great item!', 1, 1, CURRENT_TIMESTAMP);