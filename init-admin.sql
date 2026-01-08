-- init-admin.sql: Docker ile başlatıldığında admin kullanıcı ekler

INSERT INTO users (email, password, gender, role)
VALUES ('admin@admin.com',
        '$2a$10$wH8Qw8Qw8Qw8Qw8Qw8Qw8OQw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8', -- 'admin' bcrypt hash örneği
        'MALE',
        'ADMIN')
ON CONFLICT (email) DO NOTHING;