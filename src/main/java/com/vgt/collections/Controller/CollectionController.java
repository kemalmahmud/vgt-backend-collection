package com.vgt.collections.Controller;

import com.vgt.collections.Model.dto.request.*;
import com.vgt.collections.Model.dto.response.BaseResponse;
import com.vgt.collections.Service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/collections")
public class CollectionController {

    @Autowired
    CollectionService collectionService;

    // add game to collection
    @PostMapping("/add-game")
    public ResponseEntity<BaseResponse> addGameToCollection(@RequestBody AddGameRequestDto request) {
        return collectionService.addGame(request);
    }

    // remove game from collection
    @PostMapping("/remove-game")
    public ResponseEntity<BaseResponse> removeGameToCollection(@RequestBody RemoveGameRequestDto request) {
        return collectionService.removeGame(request);
    }

    // move game to other collection
    @PostMapping("/move-game")
    public ResponseEntity<BaseResponse> addGameToCollection(@RequestBody MoveGameRequestDto request) {
        return collectionService.moveGame(request);
    }

    // create new collection
    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createNewCollection(@RequestBody NewCollectionRequestDto request) {
        return collectionService.newCollection(request);
    }

    // rename collection
    @PostMapping("/rename")
    public ResponseEntity<BaseResponse> renameCollection(@RequestBody RenameCollectionRequestDto request) {
        return collectionService.renameCollection(request);
    }

    // get user collection
    @PostMapping("/user-collections")
    public ResponseEntity<BaseResponse> userCollections(@RequestBody UserCollectionsRequestDto request) {
        return collectionService.getUserCollections(request);
    }

    // get collection detail
    @PostMapping("/detail")
    public ResponseEntity<BaseResponse> detailCollection(@RequestBody DetailCollectionRequestDto request) {
        return collectionService.getDetailCollection(request);
    }
}
