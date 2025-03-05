package com.vgt.collections.Model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddGameRequestDto {
    private String collectionId;
    private Integer gameId;
    private String gameName;
    private String gameCover;
    private String gameSummary;
}
