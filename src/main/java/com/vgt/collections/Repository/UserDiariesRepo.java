package com.vgt.collections.Repository;

import com.vgt.collections.Model.Collection;
import com.vgt.collections.Model.UserDiaries;
import com.vgt.collections.Model.UserDiaryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDiariesRepo extends JpaRepository<UserDiaries, String> {
    Optional<UserDiaries> findByUserIdAndGameId(String userId, Integer gameId);
    List<UserDiaries> findByUserId(String userId);
}
