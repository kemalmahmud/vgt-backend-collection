package com.vgt.collections.Model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryEntriesResponse {
    private String id;
    private Integer minutesPlayed;
    private Integer hoursPlayed;
    private String text;
    private LocalDateTime recordedTime;
}
