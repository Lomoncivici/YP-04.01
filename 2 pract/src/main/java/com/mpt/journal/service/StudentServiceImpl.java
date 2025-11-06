package com.mpt.journal.service;

import com.mpt.journal.entity.Student;
import com.mpt.journal.repository.InMemoryStudentRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

    private final InMemoryStudentRepository repo;

    public StudentServiceImpl(InMemoryStudentRepository repo) {
        this.repo = repo;
        if (repo.findAll().isEmpty()) {
            for (int i = 1; i <= 37; i++) {
                Student s = new Student(0, "Имя"+i, "Фамилия"+i, "Отчество"+i, (i%3)+1);
                repo.save(s);
            }
        }
    }

    @Override
    public List<Student> listAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Student> get(int id) {
        return repo.findById(id);
    }

    @Override
    public void create(Student s) {
        repo.save(s);
    }

    @Override
    public Student update(Student s) {
        return repo.save(s);
    }

    @Override
    public void logicalDelete(int id) { repo.logicalDelete(id); }

    @Override
    public void physicalDelete(int id) { repo.physicalDelete(id); }

    @Override
    public void logicalDeleteBatch(Collection<Integer> ids) { repo.logicalDeleteBatch(ids); }

    @Override
    public void physicalDeleteBatch(Collection<Integer> ids) { repo.physicalDeleteBatch(ids); }

    @Override
    public List<Student> searchByName(String q) { return repo.searchByName(q); }

    @Override
    public List<Student> filter(Integer courseId, Boolean deleted, String lastNamePrefix) {
        return repo.filter(courseId, deleted, lastNamePrefix);
    }

    @Override
    public List<Student> paginate(List<Student> list, int page, int size) {
        int minSize = 10;
        int s = Math.max(size, minSize);
        int from = Math.max(0, (page-1) * s);
        int to = Math.min(list.size(), from + s);
        if (from >= list.size()) return Collections.emptyList();
        return list.subList(from, to);
    }
}