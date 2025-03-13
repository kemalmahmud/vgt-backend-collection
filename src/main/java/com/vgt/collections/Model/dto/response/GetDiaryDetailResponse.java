package com.vgt.collections.Model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDiaryDetailResponse {
    private Integer gameId;
    private String diaryId;
    private String diaryName;
    private List<DiaryEntriesResponse> entries;
}
