package com.vgt.collections.Repository;

import com.vgt.collections.Model.CollectionGames;
import com.vgt.collections.Model.UserCollections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserCollectionsRepo extends JpaRepository<UserCollections, String> {
    @Query(value = "select uc from UserCollections uc where uc.userId = :userId and uc.isDeleted = false")
    List<UserCollections> findByUserId(String userId);

    @Query(value = "select cg.* from user_collections uc " +
            "join collection c on c.collection_id = uc.collection_id " +
            "join collection_games cg on c.collection_id = cg.collection_id " +
            "where uc.user_id = :userId and uc.is_deleted = false and c.collection_name = 'Now Playing'", nativeQuery = true)
    List<CollectionGames> findNowPlaying(String userId);
}
