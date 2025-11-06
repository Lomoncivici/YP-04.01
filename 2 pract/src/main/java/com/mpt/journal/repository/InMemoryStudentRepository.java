package com.mpt.journal.repository;

import com.mpt.journal.entity.Student;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryStudentRepository {

    private final Map<Integer, Student> storage = new LinkedHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public List<Student> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Student> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Student save(Student s) {
        if (s.getId() == 0) {
            s.setId(idCounter.getAndIncrement());
        }
        storage.put(s.getId(), s);
        return s;
    }

    public void logicalDelete(int id) {
        findById(id).ifPresent(s -> {
            s.setDeleted(true);
            s.setDeletedAt(LocalDateTime.now());
        });
    }

    public void physicalDelete(int id) {
        storage.remove(id);
    }

    public void logicalDeleteBatch(Collection<Integer> ids) {
        ids.forEach(this::logicalDelete);
    }

    public void physicalDeleteBatch(Collection<Integer> ids) {
        ids.forEach(this::physicalDelete);
    }

    public List<Student> searchByName(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return findAll();
        return storage.values().stream()
                .filter(s -> (s.getName()!=null && s.getName().toLowerCase(Locale.ROOT).contains(q)) ||
                             (s.getLastName()!=null && s.getLastName().toLowerCase(Locale.ROOT).contains(q)) ||
                             (s.getFirstName()!=null && s.getFirstName().toLowerCase(Locale.ROOT).contains(q)))
                .collect(Collectors.toList());
    }

    public List<Student> filter(Integer courseId, Boolean deleted, String lastNamePrefix) {
        return storage.values().stream()
                .filter(s -> courseId == null || Objects.equals(s.getCourseId(), courseId))
                .filter(s -> deleted == null || s.isDeleted() == deleted)
                .filter(s -> lastNamePrefix == null || lastNamePrefix.isBlank() ||
                        (s.getLastName()!=null && s.getLastName().toLowerCase(Locale.ROOT)
                                .startsWith(lastNamePrefix.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
    }
}