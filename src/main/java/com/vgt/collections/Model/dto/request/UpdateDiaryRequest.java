package com.vgt.collections.Model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDiaryRequest {
    private String diaryDetailId;
    private Integer minutesPlayed;
    private Integer hoursPlayed;
    private String text;
}
