
package com.mpt.journal.repository;
import com.mpt.journal.entity.Tag;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    java.util.List<Tag> findAllByOrderByNameAsc();
}
