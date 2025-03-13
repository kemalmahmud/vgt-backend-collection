package com.vgt.collections.Service;

import com.google.type.DateTime;
import com.vgt.collections.Model.UserDiaries;
import com.vgt.collections.Model.UserDiaryDetail;
import com.vgt.collections.Model.dto.request.*;
import com.vgt.collections.Model.dto.response.*;
import com.vgt.collections.Repository.UserCollectionsRepo;
import com.vgt.collections.Repository.UserDiariesRepo;
import com.vgt.collections.Repository.UserDiaryDetailRepo;
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
        BaseResponse result = new BaseResponse();
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

            result.setData(resp);
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success get now playing");
            return ResponseEntity.ok(result);
        }
        catch(Exception e) {
            log.error("error in getNowPlaying, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed to get data");
            return ResponseEntity.badRequest().body(result);
        }
    }

    // save diary record progress
    @Transactional
    public ResponseEntity<BaseResponse> saveDiary(SaveDiaryRequest request) {
        BaseResponse result = new BaseResponse();
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

            result.setData(SaveDiaryResponse.builder()
                    .diaryId(diary.getDiaryId()).build());
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success create new collection");
            return ResponseEntity.ok(result);
        }
        catch(Exception e) {
            log.error("error in saveDiary, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed to save diary");
            return ResponseEntity.badRequest().body(result);
        }
    }

    // update diary record progress
    public ResponseEntity<BaseResponse> updateDiaryRecord(UpdateDiaryRequest request) {
        BaseResponse result = new BaseResponse();
        try {
            var diaryRecord = userDiaryDetailRepo.findById(request.getDiaryDetailId()).orElse(null);
            if(diaryRecord == null) throw  new Exception("Diary record not found");
            diaryRecord.setMinutesPlayed(request.getMinutesPlayed());
            diaryRecord.setHoursPlayed(request.getHoursPlayed());
            diaryRecord.setDiaryText(request.getText());
            userDiaryDetailRepo.save(diaryRecord);

            result.setData(SaveDiaryResponse.builder()
                    .diaryId(diaryRecord.getUserDiaries().getDiaryId()).build());
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success create new collection");
            return ResponseEntity.ok(result);
        }
        catch(Exception e) {
            log.error("error in updateDiaryRecord, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed to update diary");
            return ResponseEntity.badRequest().body(result);
        }
    }

    // get all user diaries
    public ResponseEntity<BaseResponse> getAllUserDiaries(GetAllDiaryRequest request) {
        BaseResponse result = new BaseResponse();
        try {
            var diaries = userDiariesRepo.findByUserId(request.getUserId());
            GetAllDiaryResponse resp = new GetAllDiaryResponse();
            resp.setUserId(request.getUserId());
            List<GetAllDiaryResponse.AllDiaryData> datas = new ArrayList<>();
            diaries.forEach(x -> {
                datas.add(GetAllDiaryResponse.AllDiaryData.builder()
                        .gameId(x.getGameId())
                        .diaryId(x.getDiaryId())
                        .diaryName(x.getDiaryName()).build());

            });
            resp.setDiaries(datas);
            result.setData(resp);
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success get all diaries");
            return ResponseEntity.ok(result);
        }
        catch(Exception e) {
            log.error("error in getAllUserDiaries, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed to get diaries");
            return ResponseEntity.badRequest().body(result);
        }
    }

    // get diary detail from 1 game
    public ResponseEntity<BaseResponse> getDiaryDetail(GetDiaryDetailRequest request) {
        BaseResponse result = new BaseResponse();
        try {
            var diary = userDiariesRepo.findById(request.getDiaryId()).orElse(null);
            if(diary == null) throw new Exception("Diary not found");
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

            result.setData(GetDiaryDetailResponse.builder()
                    .diaryId(request.getDiaryId())
                    .diaryName(diary.getDiaryName())
                    .gameId(diary.getGameId())
                    .entries(entries).build());
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success get diary detail");
            return ResponseEntity.ok(result);
        }
        catch(Exception e) {
            log.error("error in getDiaryDetail, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed to get diary detail");
            return ResponseEntity.badRequest().body(result);
        }
    }
}
