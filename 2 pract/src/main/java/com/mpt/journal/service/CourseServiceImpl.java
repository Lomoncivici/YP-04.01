package com.mpt.journal.service;

import com.mpt.journal.entity.Course;
import com.mpt.journal.repository.InMemoryCourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    private final InMemoryCourseRepository repo;

    public CourseServiceImpl(InMemoryCourseRepository repo) {
        this.repo = repo;
        if (repo.findAll().isEmpty()) {
            repo.save(new Course(0, "Математика", 1, "Иванов И.И."));
            repo.save(new Course(0, "Физика", 1, "Петров П.П."));
            repo.save(new Course(0, "Информатика", 2, "Сидорова А.А."));
        }
    }

    @Override
    public List<Course> listAll() { return repo.findAll(); }

    @Override
    public Optional<Course> get(int id) { return repo.findById(id); }

    @Override
    public void create(Course c) {
        repo.save(c);
    }

    @Override
    public Course update(Course c) { return repo.save(c); }

    @Override
    public void delete(int id) { repo.delete(id); }
}