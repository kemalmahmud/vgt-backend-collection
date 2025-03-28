package com.vgt.collections.Model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveGameRequestDto {
    private String collectionGamesId;
    private String newCollectionId;
}
