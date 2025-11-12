package com.mpt.journal.service;

import com.mpt.journal.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    List<Student> listAll();
    Optional<Student> get(int id);
    Optional<Student> getByLongId(Long id);

    /** Унифицированное сохранение: create/update по наличию id. */
    Student save(Student s);

    void create(Student s);
    Student update(Student s);

    void logicalDelete(int id);
    void physicalDelete(int id);
    void logicalDeleteBatch(Collection<Integer> ids);
    void physicalDeleteBatch(Collection<Integer> ids);

    List<Student> searchByName(String q);
    List<Student> filter(Integer courseId, Boolean deleted, String lastNamePrefix);

    /** Использовать Page + findPage(...). Оставлено для совместимости. */
    @Deprecated
    List<Student> paginate(List<Student> list, int page, int size);

    /** Поиск/фильтр/сорт/пагинация через JPA Specification + Pageable. */
    Page<Student> findPage(String q, Long courseId, Boolean showDeleted, int page, int size, Sort sort);
}