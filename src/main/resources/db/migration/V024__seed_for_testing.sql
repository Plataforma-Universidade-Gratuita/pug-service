-- V024__seed_for_testing.sql
BEGIN;

-- Cities (given)
INSERT INTO cities (id, name, ibge_code)
VALUES ('0d8ba23a-c0b3-4cbf-90e7-937162a2ae9f', 'Jaraguá do Sul', '4208906'),
       ('11dbc2f6-25ef-416f-8112-f7b6796b561d', 'Joinville', '4209102'),
       ('c2aebd97-cea5-4a1c-b4df-8c1ceec90460', 'Florianópolis', '4205407'),
       ('7455bfdb-cf83-4ca2-9ec8-bcbcacefb37b', 'Guaramirim', '4206504'),
       ('2813707d-2591-4c9f-ba6a-bf8100a15a8c', 'Araquari', '4201307'),
       ('19bfbf59-a2f7-4736-a145-2d95422fd62b', 'Blumenau', '4202404'),
       ('51d9902d-8643-43c8-a870-1506c2533feb', 'Schroeder', '4217402'),
       ('3caf1389-a2ee-4e01-89d8-0a7083f34ce4', 'Corupá', '4204509');

-- Users (valid CPFs)
INSERT INTO users (id, cpf, name)
VALUES ('7f2f5a10-0b6c-4d26-9d8a-000000000001', '00000000191', 'Alice Admin'),
       ('7f2f5a10-0b6c-4d26-9d8a-000000000002', '00000000353', 'Bob Admin'),
       ('7f2f5a10-0b6c-4d26-9d8a-000000000003', '00000000868', 'Carol Silva'),
       ('7f2f5a10-0b6c-4d26-9d8a-000000000004', '00000000515', 'Diego Lima'),
       ('7f2f5a10-0b6c-4d26-9d8a-000000000005', '00000000787', 'Emma Souza'),
       ('7f2f5a10-0b6c-4d26-9d8a-000000000006', '00000001082', 'Felipe Alves'),
       ('7f2f5a10-0b6c-4d26-9d8a-000000000007', '00000001163', 'Gi Costa'),
       ('7f2f5a10-0b6c-4d26-9d8a-000000000008', '00000000604', 'Bruno Rocha'),
       ('7f2f5a10-0b6c-4d26-9d8a-000000000009', '00000001244', 'Helena Barros'),
       ('7f2f5a10-0b6c-4d26-9d8a-00000000000a', '00000000272', 'Igor Pereira');

-- Admins
INSERT INTO admins (user_id)
VALUES ('7f2f5a10-0b6c-4d26-9d8a-000000000001'),
       ('7f2f5a10-0b6c-4d26-9d8a-000000000002');

-- Entities (valid CNPJs)
INSERT INTO entities (id, cnpj, name, city_id, address, active)
VALUES ('d0a1a9f0-9c4a-4d7f-8a10-0000000000e1', '00000003000125', 'Instituto Aurora',
        '0d8ba23a-c0b3-4cbf-90e7-937162a2ae9f', 'Rua Central, 100', true),
       ('d0a1a9f0-9c4a-4d7f-8a10-0000000000e2', '00000005000114', 'Fundação Horizonte',
        '11dbc2f6-25ef-416f-8112-f7b6796b561d', 'Av. das Flores, 55', true),
       ('d0a1a9f0-9c4a-4d7f-8a10-0000000000e3', '00000004000170', 'Associação Vale',
        'c2aebd97-cea5-4a1c-b4df-8c1ceec90460', 'Rua do Sol, 12', true);

-- Staff
INSERT INTO staff (id, user_id, email, entity_id, active)
VALUES ('b1c2d3e4-0000-4000-8000-000000000101', '7f2f5a10-0b6c-4d26-9d8a-000000000003', 'carol@aurora.org',
        'd0a1a9f0-9c4a-4d7f-8a10-0000000000e1', true),
       ('b1c2d3e4-0000-4000-8000-000000000102', '7f2f5a10-0b6c-4d26-9d8a-000000000004', 'diego@horizonte.org',
        'd0a1a9f0-9c4a-4d7f-8a10-0000000000e2', true),
       ('b1c2d3e4-0000-4000-8000-000000000103', '7f2f5a10-0b6c-4d26-9d8a-000000000001', 'alice@aurora.org',
        'd0a1a9f0-9c4a-4d7f-8a10-0000000000e1', true),
       ('b1c2d3e4-0000-4000-8000-000000000104', '7f2f5a10-0b6c-4d26-9d8a-000000000008', 'bruno@vale.org',
        'd0a1a9f0-9c4a-4d7f-8a10-0000000000e3', true);

-- Schools (areas)
INSERT INTO schools (id, name)
VALUES ('aaaabbbb-0000-4000-8000-000000000001', 'Information Technology'),
       ('aaaabbbb-0000-4000-8000-000000000002', 'Medicine'),
       ('aaaabbbb-0000-4000-8000-000000000003', 'Education');

-- Courses (by area)
INSERT INTO courses (id, name, school_id)
VALUES ('ccccdddd-0000-4000-8000-000000000001', 'Software Development', 'aaaabbbb-0000-4000-8000-000000000001'),
       ('ccccdddd-0000-4000-8000-000000000002', 'Networks and Systems', 'aaaabbbb-0000-4000-8000-000000000001'),
       ('ccccdddd-0000-4000-8000-000000000003', 'Clinical Skills', 'aaaabbbb-0000-4000-8000-000000000002'),
       ('ccccdddd-0000-4000-8000-000000000004', 'Pedagogy Basics', 'aaaabbbb-0000-4000-8000-000000000003');

-- Students
INSERT INTO students (id, user_id, email, academic_registration, course_id, required_hours, completed_hours, start_date,
                      due_date, active)
VALUES ('deadbeef-0000-4000-8000-000000000001', '7f2f5a10-0b6c-4d26-9d8a-000000000005', 'emma@students.it.edu',
        'IT2025-0001', 'ccccdddd-0000-4000-8000-000000000001', 60.00, 7.50, '2025-01-01', '2025-12-31', true),
       ('deadbeef-0000-4000-8000-000000000002', '7f2f5a10-0b6c-4d26-9d8a-000000000006', 'felipe@students.med.edu',
        'MED2025-0002', 'ccccdddd-0000-4000-8000-000000000003', 80.00, 5.00, '2025-03-01', '2025-12-01', true),
       ('deadbeef-0000-4000-8000-000000000003', '7f2f5a10-0b6c-4d26-9d8a-000000000007', 'gi@students.edu.edu',
        'EDU2025-0003', 'ccccdddd-0000-4000-8000-000000000004', 40.00, 12.00, '2025-02-01', '2025-11-30', true);

-- Projects
INSERT INTO projects (id, name, entity_id, description)
VALUES ('f00df00d-0000-4000-8000-000000000001', 'Community Food Drive', 'd0a1a9f0-9c4a-4d7f-8a10-0000000000e1',
        'Collect and distribute food.'),
       ('f00df00d-0000-4000-8000-000000000002', 'After-School Tutoring', 'd0a1a9f0-9c4a-4d7f-8a10-0000000000e1',
        'Tutoring for local students.'),
       ('f00df00d-0000-4000-8000-000000000003', 'Beach Cleanup', 'd0a1a9f0-9c4a-4d7f-8a10-0000000000e2',
        'Clean coastal areas.'),
       ('f00df00d-0000-4000-8000-000000000004', 'Health Outreach', 'd0a1a9f0-9c4a-4d7f-8a10-0000000000e3',
        'Community health awareness.');

-- Projects by schools
INSERT INTO projects_by_schools (id, project_id, school_id)
VALUES ('abcdabcd-0000-4000-8000-000000000001', 'f00df00d-0000-4000-8000-000000000001',
        'aaaabbbb-0000-4000-8000-000000000002'), -- Medicine
       ('abcdabcd-0000-4000-8000-000000000002', 'f00df00d-0000-4000-8000-000000000002',
        'aaaabbbb-0000-4000-8000-000000000001'), -- IT
       ('abcdabcd-0000-4000-8000-000000000003', 'f00df00d-0000-4000-8000-000000000002',
        'aaaabbbb-0000-4000-8000-000000000003'), -- Education
       ('abcdabcd-0000-4000-8000-000000000004', 'f00df00d-0000-4000-8000-000000000003',
        'aaaabbbb-0000-4000-8000-000000000001');
-- IT

-- Allocations (with max_participants)
INSERT INTO allocations (id, project_id, offered_hours, allocated_hours, status, start_date, end_date, created_by,
                         completed_at, max_participants)
VALUES ('ab12cd34-0000-4000-8000-000000000001', 'f00df00d-0000-4000-8000-000000000001', 100.00, 30.00, 'OPEN',
        '2025-10-01', '2025-11-30', 'b1c2d3e4-0000-4000-8000-000000000101', NULL, 10),
       ('ab12cd34-0000-4000-8000-000000000002', 'f00df00d-0000-4000-8000-000000000002', 120.00, 40.00, 'OPEN',
        '2025-09-01', '2025-12-15', 'b1c2d3e4-0000-4000-8000-000000000103', NULL, 15),
       ('ab12cd34-0000-4000-8000-000000000003', 'f00df00d-0000-4000-8000-000000000003', 60.00, 60.00, 'COMPLETED',
        '2025-08-01', '2025-08-31', 'b1c2d3e4-0000-4000-8000-000000000102', '2025-08-31 18:00:00+00', 8),
       ('ab12cd34-0000-4000-8000-000000000004', 'f00df00d-0000-4000-8000-000000000004', 200.00, 0.00, 'OPEN',
        '2025-10-05', '2026-01-31', 'b1c2d3e4-0000-4000-8000-000000000104', NULL, 20);

-- Enrollments
INSERT INTO enrollments (id, allocation_id, student_id, status, request_at, accepted_at, closing_status_at)
VALUES ('e1e1e1e1-0000-4000-8000-000000000001', 'ab12cd34-0000-4000-8000-000000000001',
        'deadbeef-0000-4000-8000-000000000001', 'ACCEPTED', '2025-10-01 09:00:00+00', '2025-10-02 10:00:00+00', NULL),
       ('e1e1e1e1-0000-4000-8000-000000000002', 'ab12cd34-0000-4000-8000-000000000001',
        'deadbeef-0000-4000-8000-000000000002', 'PENDING', '2025-10-05 12:00:00+00', NULL, NULL),
       ('e1e1e1e1-0000-4000-8000-000000000003', 'ab12cd34-0000-4000-8000-000000000001',
        'deadbeef-0000-4000-8000-000000000003', 'ACCEPTED', '2025-10-04 11:00:00+00', '2025-10-05 09:30:00+00', NULL),
       ('e1e1e1e1-0000-4000-8000-000000000004', 'ab12cd34-0000-4000-8000-000000000002',
        'deadbeef-0000-4000-8000-000000000002', 'ACCEPTED', '2025-09-10 14:00:00+00', '2025-09-11 08:00:00+00', NULL),
       ('e1e1e1e1-0000-4000-8000-000000000005', 'ab12cd34-0000-4000-8000-000000000002',
        'deadbeef-0000-4000-8000-000000000001', 'ACCEPTED', '2025-09-12 10:00:00+00', '2025-09-15 10:00:00+00', NULL),
       ('e1e1e1e1-0000-4000-8000-000000000006', 'ab12cd34-0000-4000-8000-000000000003',
        'deadbeef-0000-4000-8000-000000000003', 'COMPLETED', '2025-08-05 13:00:00+00', '2025-08-06 09:00:00+00',
        '2025-08-31 18:00:00+00');

-- Attendances
INSERT INTO attendances (id, enrollment_id, duration, latitude, longitude, status, qr_validation_hash, validated_by,
                         validated_at)
VALUES ('0f0f0f0f-0000-4000-8000-000000000001', 'e1e1e1e1-0000-4000-8000-000000000001', 3.50, -26.486000, -49.066000,
        'VALIDATED', 'hash1', 'b1c2d3e4-0000-4000-8000-000000000101', '2025-10-03 10:00:00+00'),
       ('0f0f0f0f-0000-4000-8000-000000000002', 'e1e1e1e1-0000-4000-8000-000000000003', 2.00, -26.487500, -49.070000,
        'VALIDATED', 'hash2', 'b1c2d3e4-0000-4000-8000-000000000101', '2025-10-06 09:00:00+00'),
       ('0f0f0f0f-0000-4000-8000-000000000003', 'e1e1e1e1-0000-4000-8000-000000000004', 5.00, -26.304000, -48.846000,
        'VALIDATED', 'hash3', 'b1c2d3e4-0000-4000-8000-000000000103', '2025-09-20 14:00:00+00'),
       ('0f0f0f0f-0000-4000-8000-000000000004', 'e1e1e1e1-0000-4000-8000-000000000005', 4.00, -26.304500, -48.847000,
        'VALIDATED', 'hash4', 'b1c2d3e4-0000-4000-8000-000000000103', '2025-09-25 15:00:00+00'),
       ('0f0f0f0f-0000-4000-8000-000000000005', 'e1e1e1e1-0000-4000-8000-000000000006', 10.00, -26.304800, -48.848000,
        'VALIDATED', 'hash5', 'b1c2d3e4-0000-4000-8000-000000000102', '2025-08-20 10:00:00+00');

COMMIT;
