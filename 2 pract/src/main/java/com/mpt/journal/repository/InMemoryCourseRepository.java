package com.mpt.journal.repository;

import com.mpt.journal.entity.Course;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryCourseRepository {

    private final Map<Integer, Course> storage = new LinkedHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public List<Course> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Course> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Course save(Course c) {
        if (c.getId() == 0) {
            c.setId(idCounter.getAndIncrement());
        }
        storage.put(c.getId(), c);
        return c;
    }

    public void delete(int id) {
        storage.remove(id);
    }
}