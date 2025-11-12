package com.mpt.journal.service;

import com.mpt.journal.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> listAll();
    Optional<Course> get(int id);
    void create(Course c);
    Course update(Course c);
    void delete(int id);
}