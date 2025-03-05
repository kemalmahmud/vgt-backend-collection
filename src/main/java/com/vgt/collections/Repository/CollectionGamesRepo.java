package com.vgt.collections.Repository;

import com.vgt.collections.Model.CollectionGames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionGamesRepo extends JpaRepository<CollectionGames, String> {

}
