package com.vgt.collections.Model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamesInCollectionDto {
    private String collectionGamesId;
    private Integer gameId;
    private String gameName;
    private String gameCover;
    private String gameSummary;
}
