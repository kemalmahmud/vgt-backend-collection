package com.vgt.collections.Service;

import com.google.type.DateTime;
import com.vgt.collections.ErrorHandler.NotFoundException;
import com.vgt.collections.Model.UserDiaries;
import com.vgt.collections.Model.UserDiaryDetail;
import com.vgt.collections.Model.dto.request.*;
import com.vgt.collections.Model.dto.response.*;
import com.vgt.collections.Repository.UserCollectionsRepo;
import com.vgt.collections.Repository.UserDiariesRepo;
import com.vgt.collections.Repository.UserDiaryDetailRepo;
import com.vgt.collections.Utils.Response;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class DiaryService {

    @Autowired
    UserDiariesRepo userDiariesRepo;
    @Autowired
    UserDiaryDetailRepo userDiaryDetailRepo;
    @Autowired
    UserCollectionsRepo userCollectionsRepo;

    // get games and progress in now playing collection
    public ResponseEntity<BaseResponse> getNowPlaying(GetNowPlayingRequest request) {
        try {
            var games = userCollectionsRepo.findNowPlaying(request.getUserId());
            List<GetNowPlayingResponse> resp = new ArrayList<>();
            games.forEach(x -> {
                // cek apakah sudah ada progress hari ini
                var hour = 0;
                var minute = 0;
                var text = "";
                var diary = userDiariesRepo.findByUserIdAndGameId(request.getUserId(), x.getGameId()).orElse(null);
                if (diary != null && !diary.getDiaryDetails().isEmpty()) {
                    var lastEntry = diary.getDiaryDetails().stream()
                            .max(Comparator.comparing(UserDiaryDetail::getRecordedTime));
                    if(lastEntry.get().getRecordedTime().toLocalDate().equals(LocalDate.now())) {
                        hour = lastEntry.get().getHoursPlayed();
                        minute = lastEntry.get().getMinutesPlayed();
                        text = lastEntry.get().getDiaryText();
                    }
                }

                resp.add(GetNowPlayingResponse.builder()
                                .gameId(x.getGameId())
                                .gameName(x.getGameName())
                                .gameCover(x.getGameCover())
                                .hoursPlayed(hour)
                                .minutesPlayed(minute)
                                .text(text)
                        .build());
            });
            return Response.success(resp, "Success get now playing");
        }
        catch(Exception e) {
            log.error("error in getNowPlaying, " + e.getMessage());
            throw e;
        }
    }

    // save diary record progress
    @Transactional
    public ResponseEntity<BaseResponse> saveDiary(SaveDiaryRequest request) {
        try {
            // cek apakah diary nya sudah ada
            var diary = userDiariesRepo.findByUserIdAndGameId(request.getUserId(), request.getGameId()).orElse(null);
            if (diary == null) { // diary belum ada
                var newDiary = UserDiaries.builder()
                        .userId(request.getUserId())
                        .diaryName("My Diary of : " + request.getGameName())
                        .isDeleted(false)
                        .build();
                diary = userDiariesRepo.save(newDiary);
            }

            // save diary detail
            var newRecord = UserDiaryDetail.builder()
                    .userDiaries(diary)
                    .minutesPlayed(request.getMinutesPlayed())
                    .hoursPlayed(request.getHoursPlayed())
                    .diaryText(request.getText())
                    .recordedTime(LocalDateTime.now()).build();
            userDiaryDetailRepo.save(newRecord);

            return Response.success(SaveDiaryResponse.builder()
                    .diaryId(diary.getDiaryId()).build(), "Success create new collection");
        }
        catch(Exception e) {
            log.error("error in saveDiary, " + e.getMessage());
            throw e;
        }
    }

    // update diary record progress
    public ResponseEntity<BaseResponse> updateDiaryRecord(UpdateDiaryRequest request) {
        try {
            var diaryRecord = userDiaryDetailRepo.findById(request.getDiaryDetailId()).orElse(null);
            if(diaryRecord == null) throw  new NotFoundException("Diary record not found", "updateDiaryRecord");
            diaryRecord.setMinutesPlayed(request.getMinutesPlayed());
            diaryRecord.setHoursPlayed(request.getHoursPlayed());
            diaryRecord.setDiaryText(request.getText());
            userDiaryDetailRepo.save(diaryRecord);

            return Response.success(SaveDiaryResponse.builder()
                    .diaryId(diaryRecord.getUserDiaries().getDiaryId()).build(), "Success create new collection");
        }
        catch(Exception e) {
            log.error("error in updateDiaryRecord, " + e.getMessage());
            throw e;
        }
    }

    // get all user diaries
    public ResponseEntity<BaseResponse> getAllUserDiaries(GetAllDiaryRequest request) {
        BaseResponse result = new BaseResponse();
        try {
            var diaries = userDiariesRepo.findByUserId(request.getUserId());
            GetAllDiaryResponse resp = new GetAllDiaryResponse();
            resp.setUserId(request.getUserId());
            List<DiaryDataResponse> datas = new ArrayList<>();
            diaries.forEach(x -> {
                datas.add(DiaryDataResponse.builder()
                        .gameId(x.getGameId())
                        .diaryId(x.getDiaryId())
                        .diaryName(x.getDiaryName()).build());

            });
            resp.setDiaries(datas);

            return Response.success(resp, "Success get all diaries");
        }
        catch(Exception e) {
            log.error("error in getAllUserDiaries, " + e.getMessage());
            throw e;
        }
    }

    // get diary detail from 1 game
    public ResponseEntity<BaseResponse> getDiaryDetail(GetDiaryDetailRequest request) {
        try {
            var diary = userDiariesRepo.findById(request.getDiaryId()).orElse(null);
            if(diary == null) throw new NotFoundException("Diary not found", "getDiaryDetail");
            List<DiaryEntriesResponse> entries = new ArrayList<>();
            diary.getDiaryDetails().forEach(x -> {
                entries.add(DiaryEntriesResponse.builder()
                                .id(x.getDiaryDetailId())
                                .minutesPlayed(x.getMinutesPlayed())
                                .hoursPlayed(x.getHoursPlayed())
                                .text(x.getDiaryText())
                                .recordedTime(x.getRecordedTime())
                        .build());
            });

            var result = GetDiaryDetailResponse.builder()
                    .diaryId(request.getDiaryId())
                    .diaryName(diary.getDiaryName())
                    .gameId(diary.getGameId())
                    .entries(entries).build();
            return Response.success(result, "Success get diary detail");
        }
        catch(Exception e) {
            log.error("error in getDiaryDetail, " + e.getMessage());
            throw e;
        }
    }
}
