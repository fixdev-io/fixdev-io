-- Page blocks: Hero
INSERT INTO blog.page_blocks (page, section, key, value, locale, sort_order) VALUES
('index', 'hero', 'hero.tag', 'Backend Engineer &middot; Java / Kotlin', 'ru', 0),
('index', 'hero', 'hero.title', 'Строю<br><span class="hero-title-em">надёжные</span>', 'ru', 1),
('index', 'hero', 'hero.subtitle', 'системы.', 'ru', 2),
('index', 'hero', 'hero.desc', 'Проектирую и разрабатываю высоконагруженные backend-системы. Микросервисы, API, интеграции — от архитектуры до деплоя.', 'ru', 3),
('index', 'hero', 'hero.cta_primary', 'Посмотреть работы', 'ru', 4),
('index', 'hero', 'hero.cta_ghost', 'Обсудить проект', 'ru', 5)
ON CONFLICT DO NOTHING;

-- Page blocks: About
INSERT INTO blog.page_blocks (page, section, key, value, locale, sort_order) VALUES
('index', 'about', 'about.tag', 'О себе', 'ru', 0),
('index', 'about', 'about.title', 'Java / Kotlin<br>разработчик', 'ru', 1),
('index', 'about', 'about.text', '<p>Занимаюсь backend-разработкой уже <strong>более 5 лет</strong>. Работал над высоконагруженными системами в финансовом секторе — знаю, что значит отказоустойчивость и производительность на практике.</p><p>Помогаю стартапам и бизнесу строить <strong>масштабируемую архитектуру</strong> с нуля или улучшать существующие системы. Умею объяснять технические решения без лишнего жаргона.</p><p>Интересует работа с интересными задачами — <strong>сложные интеграции, нагруженные API, микросервисы</strong>.</p>', 'ru', 2),
('index', 'about', 'about.card_label', '// typical workday', 'ru', 3)
ON CONFLICT DO NOTHING;

-- Page blocks: Services header
INSERT INTO blog.page_blocks (page, section, key, value, locale, sort_order) VALUES
('index', 'services', 'services.tag', 'Услуги', 'ru', 0),
('index', 'services', 'services.title', 'Чем могу помочь', 'ru', 1)
ON CONFLICT DO NOTHING;

-- Page blocks: Cases header
INSERT INTO blog.page_blocks (page, section, key, value, locale, sort_order) VALUES
('index', 'cases', 'cases.tag', 'Кейсы', 'ru', 0),
('index', 'cases', 'cases.title', 'Реальные проекты', 'ru', 1)
ON CONFLICT DO NOTHING;

-- Page blocks: Blog header
INSERT INTO blog.page_blocks (page, section, key, value, locale, sort_order) VALUES
('index', 'blog', 'blog.tag', 'Блог', 'ru', 0),
('index', 'blog', 'blog.title', 'Пишу о backend', 'ru', 1)
ON CONFLICT DO NOTHING;

-- Page blocks: Contact
INSERT INTO blog.page_blocks (page, section, key, value, locale, sort_order) VALUES
('index', 'contact', 'contact.tag', 'Контакт', 'ru', 0),
('index', 'contact', 'contact.title', 'Есть<br><em>задача?</em><br>Напишите.', 'ru', 1),
('index', 'contact', 'contact.desc', 'Расскажите о проекте — отвечу в течение 24 часов. Готов к обсуждению как небольших задач, так и долгосрочного сотрудничества.', 'ru', 2)
ON CONFLICT DO NOTHING;

-- Services
INSERT INTO blog.services (icon, title, description, locale, sort_order, active) VALUES
('⚙️', 'Backend разработка', 'Разработка серверной части с нуля: REST и gRPC API, микросервисная архитектура, интеграции с внешними системами.', 'ru', 1, TRUE),
('🏗️', 'Архитектурный консалтинг', 'Проектирование архитектуры под нагрузку. Помогу выбрать правильный стек, спроектировать схему данных и разделение сервисов.', 'ru', 2, TRUE),
('🔍', 'Code Review', 'Аудит кода и архитектуры: нахожу узкие места, предлагаю конкретные улучшения с примерами. Полезно перед масштабированием.', 'ru', 3, TRUE)
ON CONFLICT DO NOTHING;

-- Service tags
INSERT INTO blog.service_tags (service_id, name, sort_order) VALUES
((SELECT id FROM blog.services WHERE title = 'Backend разработка' AND locale = 'ru'), 'Java', 1),
((SELECT id FROM blog.services WHERE title = 'Backend разработка' AND locale = 'ru'), 'Kotlin', 2),
((SELECT id FROM blog.services WHERE title = 'Backend разработка' AND locale = 'ru'), 'Spring Boot', 3),
((SELECT id FROM blog.services WHERE title = 'Backend разработка' AND locale = 'ru'), 'Microservices', 4),
((SELECT id FROM blog.services WHERE title = 'Архитектурный консалтинг' AND locale = 'ru'), 'DDD', 1),
((SELECT id FROM blog.services WHERE title = 'Архитектурный консалтинг' AND locale = 'ru'), 'Event-driven', 2),
((SELECT id FROM blog.services WHERE title = 'Архитектурный консалтинг' AND locale = 'ru'), 'Architecture', 3),
((SELECT id FROM blog.services WHERE title = 'Code Review' AND locale = 'ru'), 'Performance', 1),
((SELECT id FROM blog.services WHERE title = 'Code Review' AND locale = 'ru'), 'Security', 2),
((SELECT id FROM blog.services WHERE title = 'Code Review' AND locale = 'ru'), 'Best Practices', 3)
ON CONFLICT DO NOTHING;

-- Case studies
INSERT INTO blog.case_studies (industry, title, description, locale, sort_order, active) VALUES
('Финансы · Высокая нагрузка', 'Платёжный микросервис для крупного банка', 'Разработал высоконагруженный сервис обработки транзакций. Рефакторинг монолита на микросервисы с Kafka-интеграцией и zero-downtime деплоем.', 'ru', 1, TRUE),
('Финансы · Интеграция', 'API-шлюз для интеграции внешних провайдеров', 'Проектирование и реализация универсального API-gateway для подключения сторонних финансовых систем. Circuit breaker, retry-политики, observability.', 'ru', 2, TRUE)
ON CONFLICT DO NOTHING;

-- Case metrics
INSERT INTO blog.case_metrics (case_study_id, metric_value, label, sort_order) VALUES
((SELECT id FROM blog.case_studies WHERE title = 'Платёжный микросервис для крупного банка' AND locale = 'ru'), '10M+', 'транзакций/день', 1),
((SELECT id FROM blog.case_studies WHERE title = 'Платёжный микросервис для крупного банка' AND locale = 'ru'), '~12ms', 'latency p99', 2),
((SELECT id FROM blog.case_studies WHERE title = 'Платёжный микросервис для крупного банка' AND locale = 'ru'), '99.99%', 'uptime', 3),
((SELECT id FROM blog.case_studies WHERE title = 'API-шлюз для интеграции внешних провайдеров' AND locale = 'ru'), '15+', 'провайдеров', 1),
((SELECT id FROM blog.case_studies WHERE title = 'API-шлюз для интеграции внешних провайдеров' AND locale = 'ru'), '3×', 'быстрее интеграций', 2)
ON CONFLICT DO NOTHING;

-- Contact links
INSERT INTO blog.contact_links (icon, label, url, locale, sort_order, active) VALUES
('✉', 'hello@fixdev.io', 'mailto:hello@fixdev.io', 'ru', 1, TRUE),
('✈', 'Telegram: @fixdev', 'https://t.me/fixdev', 'ru', 2, TRUE),
('in', 'LinkedIn', 'https://linkedin.com/in/fixdev', 'ru', 3, TRUE)
ON CONFLICT DO NOTHING;

-- Tech tags
INSERT INTO blog.tech_tags (name, accent, locale, sort_order) VALUES
('Java', TRUE, 'ru', 1),
('Kotlin', TRUE, 'ru', 2),
('Spring Boot', FALSE, 'ru', 3),
('PostgreSQL', FALSE, 'ru', 4),
('Kafka', FALSE, 'ru', 5),
('Redis', FALSE, 'ru', 6),
('Docker', FALSE, 'ru', 7),
('K8s', FALSE, 'ru', 8),
('REST / gRPC', FALSE, 'ru', 9)
ON CONFLICT DO NOTHING;
