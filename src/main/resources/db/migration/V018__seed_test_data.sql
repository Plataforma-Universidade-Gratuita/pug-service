-- Local test seed mirrored from pug-mocks.
-- Password hashes below were generated with the current %dev profile pepper.
-- Cities are resolved from V016 by IBGE code; areas and courses reuse the V017 reference rows.
-- This migration is intentionally gated and only runs when the Flyway placeholder is enabled.

DO $$
BEGIN
    IF '${seed_test_data_enabled}'::boolean THEN

INSERT INTO users (id, cpf, name, created_at, updated_at)
SELECT uuid_generate_v7(), data.cpf, data.name, data.created_at, data.updated_at
FROM (
         VALUES
             ('52998224725', 'Helena Souza', TIMESTAMPTZ '2025-01-02T09:00:00Z', TIMESTAMPTZ '2025-03-01T09:00:00Z'),
             ('12345678909', 'Caio Lima', TIMESTAMPTZ '2025-01-04T09:00:00Z', TIMESTAMPTZ '2025-03-02T09:00:00Z'),
             ('11144477735', 'Laura Mendes', TIMESTAMPTZ '2025-01-06T09:00:00Z', TIMESTAMPTZ '2025-03-03T09:00:00Z'),
             ('74185296355', 'Renata Costa', TIMESTAMPTZ '2025-01-08T09:00:00Z', TIMESTAMPTZ '2025-03-04T09:00:00Z'),
             ('98765432100', 'Gabriel Silva', TIMESTAMPTZ '2025-01-09T09:00:00Z', TIMESTAMPTZ '2025-03-05T09:00:00Z'),
             ('13579135759', 'Julia Oliveira', TIMESTAMPTZ '2025-01-10T09:00:00Z', TIMESTAMPTZ '2025-03-06T09:00:00Z'),
             ('24680246804', 'Vinicius Santos', TIMESTAMPTZ '2025-01-11T09:00:00Z', TIMESTAMPTZ '2025-03-07T09:00:00Z'),
             ('31415926590', 'Camila Rocha', TIMESTAMPTZ '2025-01-12T09:00:00Z', TIMESTAMPTZ '2025-03-08T09:00:00Z'),
             ('27182818205', 'Ana Beatriz', TIMESTAMPTZ '2025-01-13T09:00:00Z', TIMESTAMPTZ '2025-03-09T09:00:00Z'),
             ('16180339805', 'Bruno Pereira', TIMESTAMPTZ '2025-01-14T09:00:00Z', TIMESTAMPTZ '2025-03-10T09:00:00Z'),
             ('11235813207', 'Daniela Martins', TIMESTAMPTZ '2025-01-15T09:00:00Z', TIMESTAMPTZ '2025-03-11T09:00:00Z'),
             ('10101010133', 'Erica Fernandes', TIMESTAMPTZ '2025-01-16T09:00:00Z', TIMESTAMPTZ '2025-03-12T09:00:00Z'),
             ('20202020266', 'Felipe Gomes', TIMESTAMPTZ '2025-01-17T09:00:00Z', TIMESTAMPTZ '2025-03-13T09:00:00Z'),
             ('30303030399', 'Isabela Alves', TIMESTAMPTZ '2025-01-18T09:00:00Z', TIMESTAMPTZ '2025-03-14T09:00:00Z'),
             ('40404040411', 'Marcos Nunes', TIMESTAMPTZ '2025-01-19T09:00:00Z', TIMESTAMPTZ '2025-03-15T09:00:00Z')
     ) AS data(cpf, name, created_at, updated_at)
ON CONFLICT (cpf) DO NOTHING;

INSERT INTO accounts (id, user_id, email, account_type, password_hash, created_at, updated_at, active)
SELECT
    uuid_generate_v7(),
    users.id,
    data.email,
    data.account_type,
    data.password_hash,
    data.created_at,
    data.updated_at,
    data.active
FROM (
         VALUES
             ('52998224725', 'helena.souza@admin.pug.br', 'ADMIN', '$2a$10$teFaa7OBtyXNytDf/KrrkuucVLEn2ZVGIYwMNAOWeTDI/XAl5I.c2', TIMESTAMPTZ '2025-01-03T09:00:00Z', TIMESTAMPTZ '2025-03-05T09:00:00Z', TRUE),
             ('12345678909', 'caio.lima@admin.pug.br', 'ADMIN', NULL, TIMESTAMPTZ '2025-01-05T09:00:00Z', TIMESTAMPTZ '2025-03-06T09:00:00Z', TRUE),
             ('11144477735', 'laura.mendes@admin.pug.br', 'ADMIN', '$2a$10$rznIbTk7bFIinfZmN5OfX.r01z.OwsEgu/FXxpsfpXX6TXgkk1Tfe', TIMESTAMPTZ '2025-01-07T09:00:00Z', TIMESTAMPTZ '2025-03-07T09:00:00Z', FALSE),
             ('74185296355', 'renata.costa@partner.pug.br', 'PARTNER', '$2a$10$n7mjRxtJ2ZvDCBkXMpRrFe015a.L3cBa1MXLBBLFdOSUDs2pwT2TK', TIMESTAMPTZ '2025-01-09T09:00:00Z', TIMESTAMPTZ '2025-03-08T09:00:00Z', TRUE),
             ('98765432100', 'gabriel.silva@partner.pug.br', 'PARTNER', '$2a$10$LgVVEWCpz2mrgHgGaaRjueV8Tt4Yfths9QJdR1vxoakbit2qYFjcO', TIMESTAMPTZ '2025-01-10T09:00:00Z', TIMESTAMPTZ '2025-03-09T09:00:00Z', FALSE),
             ('13579135759', 'julia.oliveira@partner.pug.br', 'PARTNER', NULL, TIMESTAMPTZ '2025-01-11T09:00:00Z', TIMESTAMPTZ '2025-03-10T09:00:00Z', TRUE),
             ('24680246804', 'vinicius.santos@partner.pug.br', 'PARTNER', '$2a$10$j6WcgOqdZAyaMu58h8CSQOCUKFa8XfQ7M/.W4d9EgSdRrFnOj28NW', TIMESTAMPTZ '2025-01-12T09:00:00Z', TIMESTAMPTZ '2025-03-11T09:00:00Z', FALSE),
             ('31415926590', 'camila.rocha@partner.pug.br', 'PARTNER', '$2a$10$K5XVlvf2pCwF3dowLwu7v.epuStVbPQIsKjWoJpEGRYXzOnbMuzAG', TIMESTAMPTZ '2025-01-13T09:00:00Z', TIMESTAMPTZ '2025-03-12T09:00:00Z', TRUE),
             ('74185296355', 'renata.costa@former-student.pug.br', 'FORMER_STUDENT', '$2a$10$ithi5IhWMsQM1JKM4zjisehHXbhUWZB5hiAIZxBvThgDx6dVIi1Wi', TIMESTAMPTZ '2025-01-14T09:00:00Z', TIMESTAMPTZ '2025-03-13T09:00:00Z', TRUE),
             ('27182818205', 'ana.beatriz@former-student.pug.br', 'FORMER_STUDENT', NULL, TIMESTAMPTZ '2025-01-15T09:00:00Z', TIMESTAMPTZ '2025-03-14T09:00:00Z', FALSE),
             ('16180339805', 'bruno.pereira@former-student.pug.br', 'FORMER_STUDENT', '$2a$10$B5c4orlKy0T6.oh7J4/75uov1j9/eqET4sLUPZ/SB.32O05ezK8Za', TIMESTAMPTZ '2025-01-16T09:00:00Z', TIMESTAMPTZ '2025-03-15T09:00:00Z', TRUE),
             ('98765432100', 'gabriel.silva@former-student.pug.br', 'FORMER_STUDENT', NULL, TIMESTAMPTZ '2025-01-17T09:00:00Z', TIMESTAMPTZ '2025-03-16T09:00:00Z', FALSE),
             ('13579135759', 'julia.oliveira@former-student.pug.br', 'FORMER_STUDENT', '$2a$10$rVRMDsJQAFfmuzTuUcPHmuMn.0kMJMnEmmwEVHPoKmriooYLttNYe', TIMESTAMPTZ '2025-01-18T09:00:00Z', TIMESTAMPTZ '2025-03-17T09:00:00Z', TRUE),
             ('11235813207', 'daniela.martins@former-student.pug.br', 'FORMER_STUDENT', NULL, TIMESTAMPTZ '2025-01-19T09:00:00Z', TIMESTAMPTZ '2025-03-18T09:00:00Z', FALSE),
             ('10101010133', 'erica.fernandes@former-student.pug.br', 'FORMER_STUDENT', '$2a$10$gr7QRNauh6AiiafGcZuDpexbwp6CQVfaaRmc7bewLw8mMWsb9XbIq', TIMESTAMPTZ '2025-01-20T09:00:00Z', TIMESTAMPTZ '2025-03-19T09:00:00Z', TRUE),
             ('24680246804', 'vinicius.santos@former-student.pug.br', 'FORMER_STUDENT', NULL, TIMESTAMPTZ '2025-01-21T09:00:00Z', TIMESTAMPTZ '2025-03-20T09:00:00Z', FALSE),
             ('31415926590', 'camila.rocha@former-student.pug.br', 'FORMER_STUDENT', '$2a$10$oqxNyrgoFrzT2zuQezo/LeCnn94SCD1Fm.NiF3QmU9MEza52aOdJ.', TIMESTAMPTZ '2025-01-22T09:00:00Z', TIMESTAMPTZ '2025-03-21T09:00:00Z', TRUE),
             ('20202020266', 'felipe.gomes@former-student.pug.br', 'FORMER_STUDENT', NULL, TIMESTAMPTZ '2025-01-23T09:00:00Z', TIMESTAMPTZ '2025-03-22T09:00:00Z', FALSE),
             ('30303030399', 'isabela.alves@former-student.pug.br', 'FORMER_STUDENT', '$2a$10$1iiyImAQyJynMfHL76QWm.rpZHcpkEBoGCoyswWWJqe7MoZEcHISm', TIMESTAMPTZ '2025-01-24T09:00:00Z', TIMESTAMPTZ '2025-03-23T09:00:00Z', TRUE),
             ('40404040411', 'marcos.nunes@former-student.pug.br', 'FORMER_STUDENT', NULL, TIMESTAMPTZ '2025-01-25T09:00:00Z', TIMESTAMPTZ '2025-03-24T09:00:00Z', FALSE)
     ) AS data(cpf, email, account_type, password_hash, created_at, updated_at, active)
         JOIN users ON users.cpf = data.cpf
ON CONFLICT (email) DO NOTHING;

INSERT INTO admins (account_id, granted_at, campus)
SELECT accounts.id, data.granted_at, data.campus
FROM (
         VALUES
             ('helena.souza@admin.pug.br', TIMESTAMPTZ '2025-01-10T14:30:00Z', 'JARAGUA_DO_SUL'),
             ('caio.lima@admin.pug.br', TIMESTAMPTZ '2025-01-12T14:30:00Z', 'JOINVILLE'),
             ('laura.mendes@admin.pug.br', TIMESTAMPTZ '2025-01-14T14:30:00Z', 'JARAGUA_DO_SUL')
     ) AS data(email, granted_at, campus)
         JOIN accounts ON accounts.email = data.email
ON CONFLICT (account_id) DO NOTHING;

INSERT INTO entities (id, cnpj, name, city_id, address, created_at, updated_at)
SELECT uuid_generate_v7(), data.cnpj, data.name, cities.id, data.address, data.created_at, data.updated_at
FROM (
         VALUES
             ('11222333000181', 'Inova Tech Labs Ltda', '4209106', 'Rua das Industrias, 1450', TIMESTAMPTZ '2025-02-02T09:30:00Z', TIMESTAMPTZ '2025-03-10T09:30:00Z'),
             ('22333444000181', 'Instituto Crescer Social', '4208900', 'Avenida Central, 220', TIMESTAMPTZ '2025-02-06T09:30:00Z', TIMESTAMPTZ '2025-03-11T09:30:00Z'),
             ('33444555000181', 'Associacao Cultura Viva', '4202404', 'Rua da Cidadania, 88', TIMESTAMPTZ '2025-02-10T09:30:00Z', TIMESTAMPTZ '2025-03-14T09:30:00Z')
     ) AS data(cnpj, name, city_ibge_code, address, created_at, updated_at)
         JOIN cities ON cities.ibge_code = data.city_ibge_code
ON CONFLICT (cnpj) DO NOTHING;

INSERT INTO staff (account_id, entity_id)
SELECT accounts.id, entities.id
FROM (
         VALUES
             ('renata.costa@partner.pug.br', '11222333000181'),
             ('gabriel.silva@partner.pug.br', '11222333000181'),
             ('julia.oliveira@partner.pug.br', '22333444000181'),
             ('vinicius.santos@partner.pug.br', '22333444000181'),
             ('camila.rocha@partner.pug.br', '33444555000181')
     ) AS data(email, cnpj)
         JOIN accounts ON accounts.email = data.email
         JOIN entities ON entities.cnpj = data.cnpj
ON CONFLICT (account_id) DO NOTHING;

-- Course mapping used to reuse the seeded V017 course rows:
-- Computer Science -> Engenharia de Software
-- Information Systems -> Engenharia de Software
-- Design -> Design
-- Digital Media -> Design
-- Data Science -> Engenharia de ProduÃ§Ã£o
-- Business Analytics -> AdministraÃ§Ã£o
INSERT INTO former_students (
    account_id,
    academic_registration,
    campus,
    course_id,
    required_hours,
    completed_hours,
    start_date,
    due_date,
    concluded,
    created_at,
    updated_at
)
SELECT
    accounts.id,
    data.academic_registration,
    data.campus,
    courses.id,
    data.required_hours,
    data.completed_hours,
    data.start_date,
    data.due_date,
    data.completed_hours >= data.required_hours,
    data.created_at,
    data.updated_at
FROM (
         VALUES
             ('renata.costa@former-student.pug.br', '2023101001', 'JARAGUA_DO_SUL', 'Engenharia de Software', 240.00, 120.00, DATE '2026-02-10', DATE '2026-11-30', TIMESTAMPTZ '2025-03-01T10:00:00Z', TIMESTAMPTZ '2025-03-18T10:00:00Z'),
             ('ana.beatriz@former-student.pug.br', '2023101002', 'JOINVILLE', 'Engenharia de Software', 240.00, 40.00, DATE '2026-02-12', DATE '2026-11-30', TIMESTAMPTZ '2025-03-02T10:00:00Z', TIMESTAMPTZ '2025-03-19T10:00:00Z'),
             ('bruno.pereira@former-student.pug.br', '2023102001', 'JARAGUA_DO_SUL', 'Engenharia de Software', 180.00, 60.00, DATE '2026-02-15', DATE '2026-11-25', TIMESTAMPTZ '2025-03-03T10:00:00Z', TIMESTAMPTZ '2025-03-20T10:00:00Z'),
             ('gabriel.silva@former-student.pug.br', '2023102002', 'JOINVILLE', 'Engenharia de Software', 180.00, 20.00, DATE '2026-02-16', DATE '2026-11-25', TIMESTAMPTZ '2025-03-04T10:00:00Z', TIMESTAMPTZ '2025-03-21T10:00:00Z'),
             ('julia.oliveira@former-student.pug.br', '2023202001', 'JOINVILLE', 'Design', 160.00, 0.00, DATE '2026-03-01', DATE '2026-12-10', TIMESTAMPTZ '2025-03-05T10:00:00Z', TIMESTAMPTZ '2025-03-22T10:00:00Z'),
             ('daniela.martins@former-student.pug.br', '2023202002', 'JARAGUA_DO_SUL', 'Design', 160.00, 30.00, DATE '2026-03-02', DATE '2026-12-10', TIMESTAMPTZ '2025-03-06T10:00:00Z', TIMESTAMPTZ '2025-03-23T10:00:00Z'),
             ('erica.fernandes@former-student.pug.br', '2023203001', 'JOINVILLE', 'Design', 200.00, 200.00, DATE '2026-03-03', DATE '2026-12-15', TIMESTAMPTZ '2025-03-07T10:00:00Z', TIMESTAMPTZ '2025-03-24T10:00:00Z'),
             ('vinicius.santos@former-student.pug.br', '2023203002', 'JOINVILLE', 'Design', 200.00, 80.00, DATE '2026-03-04', DATE '2026-12-15', TIMESTAMPTZ '2025-03-08T10:00:00Z', TIMESTAMPTZ '2025-03-25T10:00:00Z'),
             ('camila.rocha@former-student.pug.br', '2023301001', 'JARAGUA_DO_SUL', 'Engenharia de ProduÃ§Ã£o', 220.00, 110.00, DATE '2026-03-06', DATE '2026-12-20', TIMESTAMPTZ '2025-03-09T10:00:00Z', TIMESTAMPTZ '2025-03-26T10:00:00Z'),
             ('felipe.gomes@former-student.pug.br', '2023301002', 'JOINVILLE', 'Engenharia de ProduÃ§Ã£o', 220.00, 10.00, DATE '2026-03-07', DATE '2026-12-20', TIMESTAMPTZ '2025-03-10T10:00:00Z', TIMESTAMPTZ '2025-03-27T10:00:00Z'),
             ('isabela.alves@former-student.pug.br', '2023302001', 'JARAGUA_DO_SUL', 'AdministraÃ§Ã£o', 140.00, 70.00, DATE '2026-03-08', DATE '2026-12-22', TIMESTAMPTZ '2025-03-11T10:00:00Z', TIMESTAMPTZ '2025-03-28T10:00:00Z'),
             ('marcos.nunes@former-student.pug.br', '2023302002', 'JOINVILLE', 'AdministraÃ§Ã£o', 140.00, 0.00, DATE '2026-03-09', DATE '2026-12-22', TIMESTAMPTZ '2025-03-12T10:00:00Z', TIMESTAMPTZ '2025-03-29T10:00:00Z')
     ) AS data(
         email,
         academic_registration,
         campus,
         course_name,
         required_hours,
         completed_hours,
         start_date,
         due_date,
         created_at,
         updated_at
     )
         JOIN accounts ON accounts.email = data.email
         JOIN courses ON courses.name = data.course_name
ON CONFLICT (account_id) DO NOTHING;

INSERT INTO projects (
    id,
    name,
    entity_id,
    description,
    created_by,
    created_at,
    updated_at,
    closed_at,
    offered_hours,
    completed_hours,
    status,
    max_participants
)
SELECT
    uuid_generate_v7(),
    data.name,
    entities.id,
    data.description,
    accounts.id,
    data.created_at,
    data.updated_at,
    data.closed_at,
    data.offered_hours,
    data.completed_hours,
    data.status,
    data.max_participants
FROM (
         VALUES
             ('11222333000181', 'Portal Comunitario', 'Plataforma digital para gestao de atendimento comunitario.', 'renata.costa@partner.pug.br', TIMESTAMPTZ '2025-03-05T08:00:00Z', TIMESTAMPTZ '2025-04-12T16:00:00Z', NULL::TIMESTAMPTZ, 60.00, 24.00, 'IN_PROGRESS', 20),
             ('11222333000181', 'Laboratorio de Acessibilidade', 'Projeto de acessibilidade digital para servicos comunitarios.', 'gabriel.silva@partner.pug.br', TIMESTAMPTZ '2025-03-08T08:00:00Z', TIMESTAMPTZ '2025-04-01T16:00:00Z', NULL::TIMESTAMPTZ, 80.00, 0.00, 'PLANNED', 16),
             ('22333444000181', 'Oficina UX Social', 'Ciclo de oficinas para melhorar servicos com pesquisa e prototipacao.', 'julia.oliveira@partner.pug.br', TIMESTAMPTZ '2025-03-10T08:00:00Z', TIMESTAMPTZ '2025-04-04T16:00:00Z', NULL::TIMESTAMPTZ, 40.00, 12.00, 'ON_HOLD', 12),
             ('22333444000181', 'Trilha de Dados Aplicados', 'Capacitacao pratica em dados para organizacoes sociais.', 'vinicius.santos@partner.pug.br', TIMESTAMPTZ '2025-03-12T08:00:00Z', TIMESTAMPTZ '2025-05-15T18:00:00Z', TIMESTAMPTZ '2025-05-15T18:00:00Z', 30.00, 30.00, 'COMPLETED', 10),
             ('22333444000181', 'Rede de Cuidado Comunitario', 'Estruturacao de fluxo de atendimento para rede comunitaria.', 'helena.souza@admin.pug.br', TIMESTAMPTZ '2025-03-15T08:00:00Z', TIMESTAMPTZ '2025-05-20T17:00:00Z', TIMESTAMPTZ '2025-05-20T17:00:00Z', 50.00, 5.00, 'CANCELED', 14)
     ) AS data(
         cnpj,
         name,
         description,
         created_by_email,
         created_at,
         updated_at,
         closed_at,
         offered_hours,
         completed_hours,
         status,
         max_participants
     )
         JOIN entities ON entities.cnpj = data.cnpj
         JOIN accounts ON accounts.email = data.created_by_email
ON CONFLICT (entity_id, name) DO NOTHING;

-- Area mapping used to reuse the seeded V017 area rows:
-- Software Engineering -> Escola PolitÃ©cnica
-- Product Design -> Escola de Design & Arquitetura
INSERT INTO project_areas_of_expertise (project_id, area_of_expertise_id)
SELECT projects.id, areas_of_expertise.id
FROM (
         VALUES
             ('11222333000181', 'Portal Comunitario', 'Escola PolitÃ©cnica'),
             ('11222333000181', 'Laboratorio de Acessibilidade', 'Escola PolitÃ©cnica'),
             ('22333444000181', 'Oficina UX Social', 'Escola de Design & Arquitetura'),
             ('22333444000181', 'Trilha de Dados Aplicados', 'Escola PolitÃ©cnica')
     ) AS data(cnpj, project_name, area_name)
         JOIN entities ON entities.cnpj = data.cnpj
         JOIN projects ON projects.entity_id = entities.id AND projects.name = data.project_name
         JOIN areas_of_expertise ON areas_of_expertise.name = data.area_name
ON CONFLICT (project_id, area_of_expertise_id) DO NOTHING;

INSERT INTO enrollments (
    project_id,
    former_student_id,
    status,
    created_at,
    updated_at,
    accepted_at,
    closing_status_at
)
SELECT
    projects.id,
    former_students.account_id,
    data.status,
    data.created_at,
    data.updated_at,
    data.accepted_at,
    data.closing_status_at
FROM (
         VALUES
             ('11222333000181', 'Portal Comunitario', 'renata.costa@former-student.pug.br', 'APPROVED', TIMESTAMPTZ '2026-03-01T09:00:00Z', TIMESTAMPTZ '2026-03-05T13:00:00Z', TIMESTAMPTZ '2026-03-05T13:00:00Z', NULL::TIMESTAMPTZ),
             ('11222333000181', 'Laboratorio de Acessibilidade', 'renata.costa@former-student.pug.br', 'PENDING', TIMESTAMPTZ '2026-03-12T09:00:00Z', TIMESTAMPTZ '2026-03-12T09:00:00Z', NULL::TIMESTAMPTZ, NULL::TIMESTAMPTZ),
             ('22333444000181', 'Trilha de Dados Aplicados', 'bruno.pereira@former-student.pug.br', 'COMPLETED', TIMESTAMPTZ '2026-03-04T09:00:00Z', TIMESTAMPTZ '2026-05-02T18:00:00Z', TIMESTAMPTZ '2026-03-08T14:00:00Z', TIMESTAMPTZ '2026-05-02T18:00:00Z'),
             ('22333444000181', 'Oficina UX Social', 'gabriel.silva@former-student.pug.br', 'ON_HOLD', TIMESTAMPTZ '2026-03-05T09:00:00Z', TIMESTAMPTZ '2026-03-18T11:00:00Z', TIMESTAMPTZ '2026-03-09T11:00:00Z', NULL::TIMESTAMPTZ),
             ('22333444000181', 'Rede de Cuidado Comunitario', 'julia.oliveira@former-student.pug.br', 'CANCELED', TIMESTAMPTZ '2026-03-06T09:00:00Z', TIMESTAMPTZ '2026-03-20T10:00:00Z', NULL::TIMESTAMPTZ, TIMESTAMPTZ '2026-03-20T10:00:00Z'),
             ('11222333000181', 'Portal Comunitario', 'daniela.martins@former-student.pug.br', 'EXITED', TIMESTAMPTZ '2026-03-07T09:00:00Z', TIMESTAMPTZ '2026-04-18T16:00:00Z', TIMESTAMPTZ '2026-03-10T10:30:00Z', TIMESTAMPTZ '2026-04-18T16:00:00Z'),
             ('11222333000181', 'Laboratorio de Acessibilidade', 'vinicius.santos@former-student.pug.br', 'REJECTED', TIMESTAMPTZ '2026-03-08T09:00:00Z', TIMESTAMPTZ '2026-03-21T12:00:00Z', NULL::TIMESTAMPTZ, TIMESTAMPTZ '2026-03-21T12:00:00Z'),
             ('22333444000181', 'Oficina UX Social', 'erica.fernandes@former-student.pug.br', 'REMOVED', TIMESTAMPTZ '2026-03-09T09:00:00Z', TIMESTAMPTZ '2026-04-25T17:00:00Z', TIMESTAMPTZ '2026-03-11T13:00:00Z', TIMESTAMPTZ '2026-04-25T17:00:00Z')
     ) AS data(
         cnpj,
         project_name,
         former_student_email,
         status,
         created_at,
         updated_at,
         accepted_at,
         closing_status_at
     )
         JOIN entities ON entities.cnpj = data.cnpj
         JOIN projects ON projects.entity_id = entities.id AND projects.name = data.project_name
         JOIN accounts ON accounts.email = data.former_student_email
         JOIN former_students ON former_students.account_id = accounts.id
ON CONFLICT (project_id, former_student_id) DO NOTHING;

INSERT INTO attendances (
    id,
    project_id,
    former_student_id,
    duration,
    status,
    qr_validation_hash,
    validated_by,
    validated_at,
    created_at,
    updated_at
)
SELECT
    uuid_generate_v7(),
    projects.id,
    former_students.account_id,
    data.duration,
    data.status,
    data.qr_validation_hash,
    validators.id,
    data.validated_at,
    data.created_at,
    data.updated_at
FROM (
         VALUES
             ('11222333000181', 'Portal Comunitario', 'renata.costa@former-student.pug.br', 4.00, 'WAITING', 'qr-portal-comunitario-001', NULL, NULL::TIMESTAMPTZ, TIMESTAMPTZ '2026-03-15T17:00:00Z', TIMESTAMPTZ '2026-03-15T17:00:00Z'),
             ('22333444000181', 'Trilha de Dados Aplicados', 'bruno.pereira@former-student.pug.br', 4.00, 'PRESENT', 'qr-trilha-dados-001', 'helena.souza@admin.pug.br', TIMESTAMPTZ '2026-03-15T18:00:00Z', TIMESTAMPTZ '2026-03-15T17:00:00Z', TIMESTAMPTZ '2026-03-15T18:00:00Z'),
             ('22333444000181', 'Oficina UX Social', 'erica.fernandes@former-student.pug.br', 2.00, 'ABSENT', 'qr-oficina-ux-social-001', 'caio.lima@admin.pug.br', TIMESTAMPTZ '2026-03-18T19:30:00Z', TIMESTAMPTZ '2026-03-18T18:00:00Z', TIMESTAMPTZ '2026-03-18T19:30:00Z')
     ) AS data(
         cnpj,
         project_name,
         former_student_email,
         duration,
         status,
         qr_validation_hash,
         validator_email,
         validated_at,
         created_at,
         updated_at
     )
         JOIN entities ON entities.cnpj = data.cnpj
         JOIN projects ON projects.entity_id = entities.id AND projects.name = data.project_name
         JOIN accounts AS students ON students.email = data.former_student_email
         JOIN former_students ON former_students.account_id = students.id
         LEFT JOIN accounts AS validators ON validators.email = data.validator_email
ON CONFLICT (qr_validation_hash) DO NOTHING;

    END IF;
END $$;
