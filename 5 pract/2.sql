-- сколько строк в таблицах
SELECT 'departments' AS t, COUNT(*) FROM public.departments
UNION ALL SELECT 'courses', COUNT(*) FROM public.courses
UNION ALL SELECT 'students', COUNT(*) FROM public.students
UNION ALL SELECT 'profiles', COUNT(*) FROM public.profiles
UNION ALL SELECT 'tags', COUNT(*) FROM public.tags
UNION ALL SELECT 'student_tags', COUNT(*) FROM public.student_tags;

-- посмотреть первые записи
SELECT * FROM public.departments ORDER BY id LIMIT 10;
SELECT * FROM public.courses ORDER BY id LIMIT 10;
SELECT * FROM public.students ORDER BY id LIMIT 10;
