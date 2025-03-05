package com.vgt.collections.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "collection_games")
public class CollectionGames {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "collection_games_id", nullable = false)
    private String collectionGamesId;

    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id")
    private Collection collectionId;

    @Column(name = "game_cover")
    private String gameCover;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "game_summary")
    private String gameSummary;

    @Column(name = "user_rating")
    private Integer userRating;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
