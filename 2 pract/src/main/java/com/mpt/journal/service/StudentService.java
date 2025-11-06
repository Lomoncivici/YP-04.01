package com.mpt.journal.service;

import com.mpt.journal.entity.Student;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    List<Student> listAll();
    Optional<Student> get(int id);
    void create(Student s);
    Student update(Student s);
    void logicalDelete(int id);
    void physicalDelete(int id);
    void logicalDeleteBatch(Collection<Integer> ids);
    void physicalDeleteBatch(Collection<Integer> ids);

    List<Student> searchByName(String q);
    List<Student> filter(Integer courseId, Boolean deleted, String lastNamePrefix);

    List<Student> paginate(List<Student> list, int page, int size);
}