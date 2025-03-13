package com.vgt.collections.Repository;

import com.vgt.collections.Model.UserDiaries;
import com.vgt.collections.Model.UserDiaryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDiaryDetailRepo extends JpaRepository<UserDiaryDetail, String> {
}
