package com.mpt.journal.service;

import com.mpt.journal.entity.Student;
import com.mpt.journal.repository.StudentRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repo;

    public StudentServiceImpl(StudentRepository repo) {
        this.repo = repo;
    }

    // -------------------- READ --------------------

    @Override
    @Transactional(readOnly = true)
    public List<Student> listAll() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> get(int id) {
        return repo.findById((long) id);
    }

    @Transactional(readOnly = true)
    public Optional<Student> getByLongId(Long id) {
        return repo.findById(id);
    }

    // -------------------- CREATE / UPDATE --------------------

    /** Унифицированный метод сохранения. Работает как create и как update по наличию id. */
    @Transactional
    public Student save(Student s) {
        return repo.save(s);
    }

    @Override
    @Transactional
    public void create(Student s) {
        repo.save(s);
    }

    @Override
    @Transactional
    public Student update(Student s) {
        return repo.save(s);
    }

    // -------------------- DELETE (SOFT / HARD) --------------------

    @Override
    @Transactional
    public void logicalDelete(int id) {
        repo.findById((long) id).ifPresent(s -> {
            s.setDeleted(true);
            s.setDeletedAt(LocalDateTime.now());
            repo.save(s);
        });
    }

    @Override
    @Transactional
    public void physicalDelete(int id) {
        repo.deleteById((long) id);
    }

    @Override
    @Transactional
    public void logicalDeleteBatch(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        List<Long> longIds = toLongIds(ids);
        List<Student> found = repo.findAllById(longIds);
        LocalDateTime now = LocalDateTime.now();
        for (Student s : found) {
            s.setDeleted(true);
            s.setDeletedAt(now);
        }
        repo.saveAll(found);
    }

    @Override
    @Transactional
    public void physicalDeleteBatch(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        List<Long> longIds = toLongIds(ids);
        repo.deleteAllByIdInBatch(longIds);
    }

    // -------------------- SEARCH / FILTER / PAGE --------------------

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchByName(String q) {
        // Без зависимости от кастомного метода repo.search(...)
        if (q == null || q.isBlank()) {
            return repo.findAll(PageRequest.of(0, 50, Sort.by("lastName").ascending())).getContent();
        }
        String like = "%" + q.toLowerCase().trim() + "%";
        Specification<Student> spec = (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("lastName")), like),
                cb.like(cb.lower(root.get("firstName")), like),
                cb.like(cb.lower(root.get("middleName")), like)
        );
        return repo.findAll(spec, PageRequest.of(0, 50, Sort.by("lastName").ascending())).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> filter(Integer courseId, Boolean deleted, String lastNamePrefix) {
        Specification<Student> spec = Specification.where(null);
        if (deleted != null) {
            spec = spec.and((r, cq, cb) -> deleted ? cb.isTrue(r.get("deleted")) : cb.isFalse(r.get("deleted")));
        }
        if (courseId != null) {
            spec = spec.and((r, cq, cb) -> cb.equal(r.get("course").get("id"), courseId.longValue()));
        }
        if (lastNamePrefix != null && !lastNamePrefix.isBlank()) {
            String like = lastNamePrefix.toLowerCase().trim() + "%";
            spec = spec.and((r, cq, cb) -> cb.like(cb.lower(r.get("lastName")), like));
        }
        return repo.findAll(spec, Sort.by("lastName").ascending());
    }

    /** @deprecated Используй findPage(...) с Pageable. Оставлено для обратной совместимости. */
    @Deprecated
    @Override
    public List<Student> paginate(List<Student> list, int page, int size) {
        size = Math.max(size, 10);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        int from = Math.max(0, page * size);
        int to = Math.min(list.size(), from + size);
        if (from >= to) return Collections.emptyList();
        return list.subList(from, to);
    }

    @Transactional(readOnly = true)
    public Page<Student> findPage(String q,
                                  Long courseId,
                                  Boolean showDeleted,
                                  int page,
                                  int size,
                                  Sort sort) {
        Specification<Student> spec = Specification.where(null);

        if (showDeleted != null) {
            spec = spec.and((r, cq, cb) -> showDeleted ? cb.isTrue(r.get("deleted")) : cb.isFalse(r.get("deleted")));
        }
        if (courseId != null) {
            spec = spec.and((r, cq, cb) -> cb.equal(r.get("course").get("id"), courseId));
        }
        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase().trim() + "%";
            spec = spec.and((r, cq, cb) -> cb.or(
                    cb.like(cb.lower(r.get("lastName")), like),
                    cb.like(cb.lower(r.get("firstName")), like),
                    cb.like(cb.lower(r.get("middleName")), like)
            ));
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 10),
                sort == null ? Sort.by("lastName").ascending() : sort);
        return repo.findAll(spec, pageable);
    }

    // -------------------- helpers --------------------
    private static List<Long> toLongIds(Collection<Integer> ids) {
        return ids.stream().filter(Objects::nonNull).map(Integer::longValue).collect(Collectors.toList());
    }
}