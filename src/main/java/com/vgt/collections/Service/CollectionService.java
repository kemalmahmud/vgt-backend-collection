package com.vgt.collections.Service;

import com.vgt.collections.Enum.InitialCollectionEnum;
import com.vgt.collections.Model.Collection;
import com.vgt.collections.Model.CollectionGames;
import com.vgt.collections.Model.UserCollections;
import com.vgt.collections.Model.dto.request.*;
import com.vgt.collections.Model.dto.response.*;
import com.vgt.collections.Repository.CollectionGamesRepo;
import com.vgt.collections.Repository.CollectionRepo;
import com.vgt.collections.Repository.UserCollectionsRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
//    @KafkaListener(topics = "initial-collection-topic", groupId = "collection-service-group")
    @Transactional
    public void createInitialCollections(String userId) {
        try {
            for (InitialCollectionEnum colType : InitialCollectionEnum.values()) {
                // collection
                var newCol = createNewCollection(colType.getName(), "", true);
                // user collection
                createNewUserCollections(userId, newCol);
            }
        }
        catch(Exception e) {
            log.error("Error in createInitialCollections");
        }
    }

    @Transactional
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

    public ResponseEntity<BaseResponse> moveGame(MoveGameRequestDto request) {
        BaseResponse result = new BaseResponse();
        try {
            // remove the old one
            var oldCollectionGames = collectionGamesRepo.findById(request.getCollectionGamesId()).orElse(null);
            if(oldCollectionGames == null) throw new Exception("Games not found in this collection");
            removeGameFromCollection(oldCollectionGames.getCollectionGamesId());

            // add to new one
            var newReq = AddGameRequestDto.builder()
                    .collectionId(request.getNewCollectionId())
                    .gameId(oldCollectionGames.getGameId())
                    .gameCover(oldCollectionGames.getGameCover())
                    .gameSummary(oldCollectionGames.getGameSummary())
                    .gameName(oldCollectionGames.getGameName()).build();

            var newGame = addGameToCollection(newReq);
            result.setData(newGame);
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success move game");
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed move game");
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

    @Transactional
    public AddGameResponseDto addGameToCollection(AddGameRequestDto request) throws Exception {
        try {
            var col = collectionRepo.findById(request.getCollectionId()).orElse(null);
            if(col == null) throw new Exception("Collection not found");

            // cek apakah gamenya sudah ada
            var cg = collectionGamesRepo.findByGameId(request.getGameId(), request.getCollectionId()).orElse(null);
            if(cg != null) throw new Exception("Already exist in collection");

            var colGames = CollectionGames.builder()
                    .gameId(request.getGameId())
                    .gameName(request.getGameName())
                    .gameCover(request.getGameCover())
                    .gameSummary(request.getGameSummary())
                    .collectionId(col)
                    .isDeleted(false)
                    .build();
            var newCol = collectionGamesRepo.save(colGames);
            return AddGameResponseDto.builder().collectionGamesId(newCol.getCollectionGamesId()).build();
        }
        catch(Exception e) {
            log.error("Error in addGameToCollection, " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<BaseResponse> removeGame(RemoveGameRequestDto request) {
        BaseResponse result = new BaseResponse();
        try {
            removeGameFromCollection(request.getCollectionGamesId());
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

    @Transactional
    public void removeGameFromCollection(String colGamesId) throws Exception {
        try {
            var colGame = collectionGamesRepo.findById(colGamesId).orElse(null);
            if(colGame == null) throw new Exception("Collection game not found");
            colGame.setIsDeleted(true);
            collectionGamesRepo.save(colGame);
        }
        catch(Exception e) {
            log.error("Error in removeGameFromCollection, " + e.getMessage());
            throw e;
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
                    .isDeleted(false)
                    .build();
            userCollectionsRepo.save(userCol);
        }
        catch(Exception e) {
            log.error("Error in createNewUserCollections, " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<BaseResponse> getDetailCollection(DetailCollectionRequestDto request) {
        BaseResponse result = new BaseResponse();
        try {
            var col = collectionRepo.findById(request.getCollectionId()).orElse(null);
            if(col != null) {
                List<GamesInCollectionDto> games = new ArrayList<>();
                for(var g : col.getCollectionGames()) {
                    if(g.getIsDeleted()) continue; // skip yang deleted
                    games.add(GamesInCollectionDto.builder()
                                    .collectionGamesId(g.getCollectionGamesId())
                                    .gameId(g.getGameId())
                                    .gameCover(g.getGameCover())
                                    .gameSummary(g.getGameSummary())
                                    .gameName(g.getGameName())
                            .build());
                }
                var allGamesInCol = DetailCollectionResponseDto.builder()
                        .collectionId(col.getCollectionId())
                        .collectionName(col.getCollectionName())
                        .games(games).build();
                result.setData(allGamesInCol);
                result.setStatus(HttpStatus.OK.value());
                result.setMessage("Success get collection detail");
                return ResponseEntity.ok(result);
            }
            else throw new Exception("Collection not found");
        }
        catch(Exception e) {
            log.error("error in getDetailCollection, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed get detail collection");
            return ResponseEntity.badRequest().body(result);
        }
    }

    public ResponseEntity<BaseResponse> getUserCollections(UserCollectionsRequestDto request) {
        BaseResponse result = new BaseResponse();
        try {
            var collections = userCollectionsRepo.findByUserId(request.getUserId());
            List<CollectionDetailDto> details = new ArrayList<>();
            collections.stream().forEach( c -> {
                details.add(CollectionDetailDto.builder()
                        .userCollectionsId(c.getUserCollectionsId())
                        .collectionId(c.getCollectionId().getCollectionId())
                        .collectionName(c.getCollectionId().getCollectionName())
                        .thumbnail(c.getCollectionId().getThumbnail()).build());
            });

            var data = UserCollectionsResponseDto.builder()
                    .userId(request.getUserId())
                    .collections(details)
                    .build();
            result.setData(data);
            result.setStatus(HttpStatus.OK.value());
            result.setMessage("Success get user collections");
            return ResponseEntity.ok(result);
        }
        catch(Exception e) {
            log.error("error in getUserCollections, " + e.getMessage());
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("Failed get user collections");
            return ResponseEntity.badRequest().body(result);
        }
    }
}
