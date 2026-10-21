INSERT INTO events (name, description, event_date, available_seats, currency, price, created_at, updated_at)
VALUES
    ('Creative Tech Summit 2026',
     'Join designers, developers, and creative technologists for a full day of talks, hands-on workshops, and conversations about the ideas shaping the future of digital experiences.',
     '2027-09-10 10:00:00Z',
     50,
     'INR',
     100,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),

    ('The Future of Responsible AI',
     'Explore practical ways to build AI products that are transparent, inclusive, and useful. This online event brings together product leaders and researchers for a focused afternoon of talks and audience Q&A.',
     '2027-09-15 18:00:00Z',
     30,
     'INR',
     200,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),

    ('Product Makers Community Day',
     'Spend a day learning, sharing, and connecting with product makers. Attend in person in San Francisco or join the talks and community sessions online.',
     '2027-09-25 09:00:00Z',
     100,
     'INR',
     50,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP);

-- Update Statements

UPDATE events
SET
    name = 'Creative Tech Summit 2026',
    description = 'Join designers, developers, and creative technologists for a full day of talks, hands-on workshops, and conversations about the ideas shaping the future of digital experiences.',
    event_date = '2027-09-10 10:00:00Z',
    available_seats = 50,
    currency = 'INR',
    price = 100,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1;


UPDATE events
SET
    name = 'The Future of Responsible AI',
    description = 'Explore practical ways to build AI products that are transparent, inclusive, and useful. This online event brings together product leaders and researchers for a focused afternoon of talks and audience Q&A.',
    event_date = '2027-09-15 18:00:00Z',
    available_seats = 30,
    currency = 'INR',
    price = 200,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 2;


UPDATE events
SET
    name = 'Product Makers Community Day',
    description = 'Spend a day learning, sharing, and connecting with product makers. Attend in person in San Francisco or join the talks and community sessions online.',
    event_date = '2027-09-25 09:00:00Z',
    available_seats = 100,
    currency = 'INR',
    price = 50,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 3;