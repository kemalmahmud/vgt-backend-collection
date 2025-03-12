package com.vgt.collections.Model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailCollectionResponseDto {
    private String collectionId;
    private String collectionName;
    private List<GamesInCollectionDto> games;
}
