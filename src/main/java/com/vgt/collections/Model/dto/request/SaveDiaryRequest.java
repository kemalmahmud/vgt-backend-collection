package com.vgt.collections.Model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveDiaryRequest {
    private String userId;
    private Integer gameId;
    private String gameName;
    private Integer minutesPlayed;
    private Integer hoursPlayed;
    private String text;
}
