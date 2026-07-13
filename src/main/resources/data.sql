INSERT INTO users (id, name, created_at, updated_at)
VALUES (1, '민재', NOW(), NOW());

INSERT INTO user_points (id, user_id, point, created_at, updated_at)
VALUES (1, 1, 0, NOW(), NOW());

INSERT INTO menus (id, name, price, created_at, updated_at)
VALUES
    (1, '아메리카노', 4500, NOW(), NOW()),
    (2, '카페라떼', 5000, NOW(), NOW()),
    (3, '바닐라라떼', 5500, NOW(), NOW()),
    (4, '카푸치노', 5000, NOW(), NOW()),
    (5, '카라멜마끼아또', 6000, NOW(), NOW());