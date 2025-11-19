-- === СХЕМА ===
SET search_path = journal, public;

-- ============ 1) DEPARTMENTS (1 -> M COURSES) ============
CREATE TABLE departments (
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR(100)  NOT NULL UNIQUE,
  deleted      BOOLEAN       NOT NULL DEFAULT FALSE,
  deleted_at   TIMESTAMPTZ   NULL,
  created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- Ускоряем поиск по активным
CREATE INDEX idx_departments_active ON departments (id) WHERE deleted = FALSE;

-- ============ 2) COURSES (M -> 1 DEPARTMENT, 1 -> M STUDENTS) ============
CREATE TABLE courses (
  id             BIGSERIAL PRIMARY KEY,
  title          VARCHAR(80) NOT NULL UNIQUE,
  year           INTEGER     NOT NULL,
  department_id  BIGINT      NOT NULL REFERENCES departments(id) ON UPDATE CASCADE,
  deleted        BOOLEAN     NOT NULL DEFAULT FALSE,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE courses
  ADD CONSTRAINT chk_courses_year CHECK (year BETWEEN 1 AND 6);

CREATE INDEX idx_courses_department ON courses(department_id) WHERE deleted = FALSE;
CREATE INDEX idx_courses_title ON courses(LOWER(title)) WHERE deleted = FALSE;

-- ============ 3) STUDENTS (M -> 1 COURSE, 1 <-> 1 PROFILE, M <-> M TAGS) ============
CREATE TABLE students (
  id           BIGSERIAL PRIMARY KEY,
  last_name    VARCHAR(60)  NOT NULL,
  first_name   VARCHAR(60)  NOT NULL,
  middle_name  VARCHAR(60)  NULL,
  course_id    BIGINT       NOT NULL REFERENCES courses(id) ON UPDATE CASCADE,
  deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
  deleted_at   TIMESTAMPTZ  NULL,
  created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Поиск по ФИО и фильтр активных
CREATE INDEX idx_students_active ON students(id) WHERE deleted = FALSE;
CREATE INDEX idx_students_course ON students(course_id) WHERE deleted = FALSE;
CREATE INDEX idx_students_last_name ON students(LOWER(last_name)) WHERE deleted = FALSE;
CREATE INDEX idx_students_first_name ON students(LOWER(first_name)) WHERE deleted = FALSE;
CREATE INDEX idx_students_middle_name ON students(LOWER(middle_name)) WHERE middle_name IS NOT NULL AND deleted = FALSE;

-- ============ 4) PROFILE (1 <-> 1 STUDENT) ============
CREATE TABLE profiles (
  id          BIGSERIAL PRIMARY KEY,
  student_id  BIGINT NOT NULL UNIQUE
              REFERENCES students(id) ON DELETE CASCADE ON UPDATE CASCADE,
  email       VARCHAR(120) NULL,
  phone       VARCHAR(20)  NULL,
  address     VARCHAR(255) NULL,
  created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Простые проверки формата (не строгие, под учебный проект)
ALTER TABLE profiles
  ADD CONSTRAINT chk_profiles_email_format
  CHECK (email IS NULL OR email ~* '^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$');

ALTER TABLE profiles
  ADD CONSTRAINT chk_profiles_phone_format
  CHECK (phone IS NULL OR phone ~ '^\+?[0-9\- ]{7,20}$');

CREATE INDEX idx_profiles_student ON profiles(student_id);

-- ============ 5) TAGS (M <-> M STUDENTS) ============
CREATE TABLE tags (
  id          BIGSERIAL PRIMARY KEY,
  name        VARCHAR(50) NOT NULL UNIQUE,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tags_name ON tags(LOWER(name));

-- Связующая таблица M <-> M
CREATE TABLE student_tags (
  student_id  BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE ON UPDATE CASCADE,
  tag_id      BIGINT NOT NULL REFERENCES tags(id)     ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY (student_id, tag_id)
);

CREATE INDEX idx_student_tags_tag ON student_tags(tag_id);

-- ============ ТРИГГЕРЫ updated_at (опционально, удобно) ============
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at := NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE t TEXT;
BEGIN
  FOR t IN SELECT 'departments' UNION ALL
           SELECT 'courses'     UNION ALL
           SELECT 'students'    UNION ALL
           SELECT 'profiles'
  LOOP
    EXECUTE format('DROP TRIGGER IF EXISTS trg_%1$s_updated_at ON %1$s;', t);
    EXECUTE format('CREATE TRIGGER trg_%1$s_updated_at BEFORE UPDATE ON %1$s
                    FOR EACH ROW EXECUTE FUNCTION set_updated_at();', t);
  END LOOP;
END $$;

-- ============ ПРЕДСТАВЛЕНИЕ ДЛЯ УДОБНОГО СПИСКА СТУДЕНТОВ (опц.) ============
CREATE OR REPLACE VIEW v_students_full AS
SELECT s.id,
       s.last_name,
       s.first_name,
       s.middle_name,
       s.deleted,
       s.deleted_at,
       c.id   AS course_id,
       c.title AS course_title,
       c.year  AS course_year,
       d.id   AS department_id,
       d.name AS department_name
FROM students s
JOIN courses c     ON c.id = s.course_id AND c.deleted = FALSE
JOIN departments d ON d.id = c.department_id AND d.deleted = FALSE;

-- ============ ТЕСТОВЫЕ ДАННЫЕ (по желанию) ============
INSERT INTO departments (name) VALUES
  ('ФКН'), ('ИВТ')
ON CONFLICT DO NOTHING;

INSERT INTO courses (title, year, department_id)
VALUES
  ('Алгоритмы', 1, (SELECT id FROM departments WHERE name='ФКН')),
  ('Базы данных', 2, (SELECT id FROM departments WHERE name='ФКН')),
  ('Физика', 1, (SELECT id FROM departments WHERE name='ИВТ'))
ON CONFLICT DO NOTHING;

INSERT INTO tags (name) VALUES ('Отличник'), ('Стипендия'), ('Иностранец')
ON CONFLICT DO NOTHING;

INSERT INTO students (last_name, first_name, middle_name, course_id)
VALUES
  ('Иванов', 'Иван', 'Иванович', (SELECT id FROM courses WHERE title='Алгоритмы')),
  ('Петров', 'Пётр', NULL,       (SELECT id FROM courses WHERE title='Базы данных')),
  ('Сидорова', 'Анна', NULL,     (SELECT id FROM courses WHERE title='Физика'));

-- Профили 1:1
INSERT INTO profiles (student_id, email, phone, address)
SELECT s.id, 'user'||s.id||'@mail.test', '+7 900 000-00-0'||s.id, 'Адрес '||s.id
FROM students s
WHERE NOT EXISTS (SELECT 1 FROM profiles p WHERE p.student_id = s.id);

-- Теги для студентов (М:М)
INSERT INTO student_tags (student_id, tag_id)
SELECT (SELECT id FROM students WHERE last_name='Иванов'),
       (SELECT id FROM tags WHERE name='Отличник')
WHERE NOT EXISTS (
  SELECT 1 FROM student_tags
  WHERE student_id=(SELECT id FROM students WHERE last_name='Иванов')
    AND tag_id=(SELECT id FROM tags WHERE name='Отличник')
);

INSERT INTO student_tags (student_id, tag_id)
SELECT (SELECT id FROM students WHERE last_name='Петров'),
       (SELECT id FROM tags WHERE name='Стипендия')
WHERE NOT EXISTS (
  SELECT 1 FROM student_tags
  WHERE student_id=(SELECT id FROM students WHERE last_name='Петров')
    AND tag_id=(SELECT id FROM tags WHERE name='Стипендия')
);
