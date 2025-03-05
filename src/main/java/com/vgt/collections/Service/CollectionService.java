package com.vgt.collections.Service;

import com.vgt.collections.Enum.InitialCollectionEnum;
import com.vgt.collections.Model.Collection;
import com.vgt.collections.Model.CollectionGames;
import com.vgt.collections.Model.UserCollections;
import com.vgt.collections.Model.dto.request.AddGameRequestDto;
import com.vgt.collections.Model.dto.request.NewCollectionRequestDto;
import com.vgt.collections.Model.dto.request.RemoveGameRequestDto;
import com.vgt.collections.Model.dto.request.RenameCollectionRequestDto;
import com.vgt.collections.Model.dto.response.AddGameResponseDto;
import com.vgt.collections.Model.dto.response.BaseResponse;
import com.vgt.collections.Model.dto.response.CollectionResponseDto;
import com.vgt.collections.Repository.CollectionGamesRepo;
import com.vgt.collections.Repository.CollectionRepo;
import com.vgt.collections.Repository.UserCollectionsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CollectionService {

    @Autowired
    private CollectionRepo collectionRepo;
    @Autowired
    private UserCollectionsRepo userCollectionsRepo;
    @Autowired
    private CollectionGamesRepo collectionGamesRepo;

    //create initial collection for new user using kafka
    public void createInitialCollections() {
        var userId = "cc9b463d-512b-42f1-80d4-ca0d5e70ccd7"; // test doang
        for (InitialCollectionEnum colType : InitialCollectionEnum.values()) {
            // collection
            var newCol = createNewCollection(colType.getName(), "", true);
            // user collection
            createNewUserCollections(userId, newCol);
        }
    }

    public ResponseEntity<BaseResponse> newCollection(NewCollectionRequestDto request) {
        BaseResponse result = new BaseResponse();
        try {
            // collection
            var newCol = createNewCollection(request.getName(), "", false);
            // user collection
            createNewUserCollections(request.getUserId(), newCol);
            result.setData(CollectionResponseDto.builder().collectionId(newCol.getCollectionId()).build());
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success create new collection");
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            log.error("error in newCollection, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed create new collection");
            return ResponseEntity.badRequest().body(result);
        }
    }

    public ResponseEntity<BaseResponse> renameCollection(RenameCollectionRequestDto request) {
        BaseResponse result = new BaseResponse();
        try {
            var col = collectionRepo.findById(request.getCollectionId()).orElse(null);
            if(col != null) {
                col.setCollectionName(request.getNewName());
                collectionRepo.save(col);
                result.setData(CollectionResponseDto.builder().collectionId(col.getCollectionId()).build());
                result.setStatus(HttpStatus.OK.value());
                result.setMessage("Success rename collection");
                return ResponseEntity.ok(result);
            } else throw new Exception("Collection not found");
        }
        catch (Exception e) {
            log.error("error in renameCollection, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed rename collection");
            return ResponseEntity.badRequest().body(result);
        }
    }

    public ResponseEntity<BaseResponse> addGame(AddGameRequestDto request) {
        BaseResponse result = new BaseResponse();
        try {
            var newGame = addGameToCollection(request);
            result.setData(newGame);
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success add game");
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            log.error("error in addGame, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed add game");
            return ResponseEntity.badRequest().body(result);
        }
    }

    public AddGameResponseDto addGameToCollection(AddGameRequestDto request) {
        try {
            var col = collectionRepo.findById(request.getCollectionId()).orElse(null);
            if(col == null) throw new Exception("Collection not found");
            var colGames = CollectionGames.builder()
                    .gameId(request.getGameId())
                    .gameName(request.getGameName())
                    .gameCover(request.getGameCover())
                    .gameSummary(request.getGameSummary())
                    .collectionId(col)
                    .build();
            var newCol = collectionGamesRepo.save(colGames);
            return AddGameResponseDto.builder().collectionGamesId(newCol.getCollectionGamesId()).build();
        }
        catch(Exception e) {
            log.error("Error in addGameToCollection, " + e.getMessage());
            return null;
        }
    }

    public ResponseEntity<BaseResponse> removeGame(RemoveGameRequestDto request) {
        BaseResponse result = new BaseResponse();
        try {
            removeGameFromCollection(request.getCollectionId());
            result.setData(null);
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success remove game");
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            log.error("error in removeGame, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed remove game");
            return ResponseEntity.badRequest().body(result);
        }
    }

    public void removeGameFromCollection(String collectionGameId) {
        try {
            var colGame = collectionGamesRepo.findById(collectionGameId).orElse(null);
            if(colGame == null) throw new Exception("Collection game not found");
            colGame.setIsDeleted(true);
            collectionGamesRepo.save(colGame);
        }
        catch(Exception e) {
            log.error("Error in removeGameFromCollection, " + e.getMessage());
        }
    }

    public Collection createNewCollection(String name, String thumbnail, Boolean isMandatory) {
        try {
            var col = Collection.builder()
                    .collectionName(name)
                    .thumbnail(thumbnail)
                    .mandatory(isMandatory)
                    .build();
            return collectionRepo.save(col);
        }
        catch(Exception e) {
            log.error("Error in createNewCollection, " + e.getMessage());
            throw e;
        }
    }

    public void createNewUserCollections(String userId, Collection collection) {
        try {
            var userCol = UserCollections.builder()
                    .userId(userId)
                    .collectionId(collection)
                    .build();
            userCollectionsRepo.save(userCol);
        }
        catch(Exception e) {
            log.error("Error in createNewUserCollections, " + e.getMessage());
            throw e;
        }
    }
}
