package com.mpt.journal.repository;
import com.mpt.journal.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByDeletedFalseOrderByTitleAsc(); // оставим для выпадающих списков
    Page<Course> findByDeletedFalse(Pageable pageable); // для пагинации
}
