package com.vgt.collections.Repository;

import com.vgt.collections.Model.UserCollections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserCollectionsRepo extends JpaRepository<UserCollections, String> {
    @Query(value = "select uc from UserCollections uc where uc.userId = :userId and uc.isDeleted = false")
    List<UserCollections> findByUserId(String userId);
}
