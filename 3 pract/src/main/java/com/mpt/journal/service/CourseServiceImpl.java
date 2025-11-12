
package com.mpt.journal.service;

import com.mpt.journal.entity.Course;
import com.mpt.journal.repository.CourseRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repo;

    public CourseServiceImpl(CourseRepository repo) { this.repo = repo; }

    @Override
    public List<Course> listAll() { return repo.findByDeletedFalseOrderByTitleAsc(); }

    @Override
    public Optional<Course> get(int id) { return repo.findById((long) id); }

    @Override
    public void create(Course c) { repo.save(c); }

    @Override
    public Course update(Course c) { return repo.save(c); }

    @Override
    public void delete(int id) { repo.deleteById((long) id); }
}
