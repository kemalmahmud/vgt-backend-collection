package com.vgt.collections.Repository;

import com.vgt.collections.Model.CollectionGames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionGamesRepo extends JpaRepository<CollectionGames, String> {
    @Query(value = "select cg from CollectionGames cg where cg.gameId = :gameId and cg.collectionId.collectionId = :collectionId and cg.isDeleted = false")
    Optional<CollectionGames> findByGameId(Integer gameId, String collectionId);
}
