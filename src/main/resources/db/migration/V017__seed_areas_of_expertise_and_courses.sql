INSERT INTO areas_of_expertise (id, name, created_at, updated_at)
VALUES
    (uuid_generate_v7(), 'Escola de Negócios', now(), now()),
    (uuid_generate_v7(), 'Escola de Design & Arquitetura', now(), now()),
    (uuid_generate_v7(), 'Escola de Saúde', now(), now()),
    (uuid_generate_v7(), 'Escola de Direito', now(), now()),
    (uuid_generate_v7(), 'Escola Politécnica', now(), now())
    ON CONFLICT (name) DO NOTHING;

INSERT INTO courses (id, name, area_of_expertise_id, created_at, updated_at)
SELECT
    uuid_generate_v7(),
    course_data.course_name,
    areas_of_expertise.id,
    now(),
    now()
FROM (
         VALUES
             ('Escola de Negócios', 'Administração'),
             ('Escola de Negócios', 'Ciências Contábeis'),

             ('Escola de Design & Arquitetura', 'Arquitetura & Urbanismo'),
             ('Escola de Design & Arquitetura', 'Design'),

             ('Escola de Saúde', 'Biomedicina'),
             ('Escola de Saúde', 'Enfermagem'),
             ('Escola de Saúde', 'Nutrição'),
             ('Escola de Saúde', 'Psicologia'),

             ('Escola de Direito', 'Direito'),

             ('Escola Politécnica', 'Engenharia Civil'),
             ('Escola Politécnica', 'Engenharia Elétrica'),
             ('Escola Politécnica', 'Engenharia Mecânica'),
             ('Escola Politécnica', 'Engenharia de Produção'),
             ('Escola Politécnica', 'Engenharia de Software')
     ) AS course_data(area_name, course_name)
     JOIN areas_of_expertise
          ON areas_of_expertise.name = course_data.area_name
    ON CONFLICT (name) DO NOTHING;