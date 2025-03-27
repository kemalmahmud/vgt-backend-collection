package com.vgt.collections.Controller;

import com.vgt.collections.Model.dto.request.*;
import com.vgt.collections.Model.dto.response.BaseResponse;
import com.vgt.collections.Service.CollectionService;
import com.vgt.collections.Service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    DiaryService diaryService;

    // get now playing
    @PostMapping("/now-playing")
    public ResponseEntity<BaseResponse> nowPlaying(@RequestBody GetNowPlayingRequest request) {
        return diaryService.getNowPlaying(request);
    }

    // get all diary from user
    @PostMapping("/all")
    public ResponseEntity<BaseResponse> getAllDiaries(@RequestBody GetAllDiaryRequest request) {
        return diaryService.getAllUserDiaries(request);
    }

    // get detail diary
    @PostMapping("/detail")
    public ResponseEntity<BaseResponse> getDiaryDetail(@RequestBody GetDiaryDetailRequest request) {
        return diaryService.getDiaryDetail(request);
    }

    // save diary
    @PostMapping("/save")
    public ResponseEntity<BaseResponse> saveDiaryProgress(@RequestBody SaveDiaryRequest request) {
        return diaryService.saveDiary(request);
    }

    // update diary
    @PostMapping("/update")
    public ResponseEntity<BaseResponse> updateDiaryProgress(@RequestBody UpdateDiaryRequest request) {
        return diaryService.updateDiaryRecord(request);
    }
}
