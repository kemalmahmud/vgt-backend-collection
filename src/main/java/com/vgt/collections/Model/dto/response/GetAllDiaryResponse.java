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
public class GetAllDiaryResponse {
    private String userId;
    private List<AllDiaryData> diaries;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class AllDiaryData {
        private Integer gameId;
        private String diaryId;
        private String diaryName;
    }
}
