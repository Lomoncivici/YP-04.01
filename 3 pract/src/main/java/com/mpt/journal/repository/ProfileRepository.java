
package com.mpt.journal.repository;
import com.mpt.journal.entity.Profile;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {}
