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
}
