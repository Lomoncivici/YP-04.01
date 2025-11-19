
package com.mpt.journal.repository;
import com.mpt.journal.entity.Student;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.*;
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    @Query("select s from Student s where s.deleted = false and " +
           "(lower(s.lastName) like lower(concat('%', :q, '%')) or " +
           " lower(s.firstName) like lower(concat('%', :q, '%')) or " +
           " lower(s.middleName) like lower(concat('%', :q, '%')))")
    Page<Student> search(@Param("q") String q, Pageable pageable);
}
