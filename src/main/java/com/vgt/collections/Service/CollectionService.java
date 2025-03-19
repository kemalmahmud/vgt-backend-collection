package com.vgt.collections.Service;

import com.vgt.collections.Enum.InitialCollectionEnum;
import com.vgt.collections.ErrorHandler.GameAlreadyExistException;
import com.vgt.collections.ErrorHandler.NotFoundException;
import com.vgt.collections.Model.Collection;
import com.vgt.collections.Model.CollectionGames;
import com.vgt.collections.Model.UserCollections;
import com.vgt.collections.Model.dto.request.*;
import com.vgt.collections.Model.dto.response.*;
import com.vgt.collections.Repository.CollectionGamesRepo;
import com.vgt.collections.Repository.CollectionRepo;
import com.vgt.collections.Repository.UserCollectionsRepo;
import com.vgt.collections.Utils.Response;
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
        try {
            // collection
            var newCol = createNewCollection(request.getName(), "", false);
            // user collection
            createNewUserCollections(request.getUserId(), newCol);
            return Response.success(CollectionResponseDto.builder().collectionId(newCol.getCollectionId()).build(), "Success create new collection");
        }
        catch (Exception e) {
            log.error("error in newCollection, " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<BaseResponse> renameCollection(RenameCollectionRequestDto request) {
        try {
            var col = collectionRepo.findById(request.getCollectionId()).orElse(null);
            if(col != null) {
                col.setCollectionName(request.getNewName());
                collectionRepo.save(col);
                return Response.success(CollectionResponseDto.builder().collectionId(col.getCollectionId()).build(), "Success rename collection");
            } else throw new NotFoundException("Collection not found", "renameCollection");
        }
        catch (Exception e) {
            log.error("error in renameCollection, " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<BaseResponse> moveGame(MoveGameRequestDto request) {
        try {
            // remove the old one
            var oldCollectionGames = collectionGamesRepo.findById(request.getCollectionGamesId()).orElse(null);
            if(oldCollectionGames == null) throw new NotFoundException("Games not found in this collection", "moveGame");
            removeGameFromCollection(oldCollectionGames.getCollectionGamesId());

            // add to new one
            var newReq = AddGameRequestDto.builder()
                    .collectionId(request.getNewCollectionId())
                    .gameId(oldCollectionGames.getGameId())
                    .gameCover(oldCollectionGames.getGameCover())
                    .gameSummary(oldCollectionGames.getGameSummary())
                    .gameName(oldCollectionGames.getGameName()).build();

            var newGame = addGameToCollection(newReq);
            return Response.success(newGame, "Success move game");
        }
        catch (Exception e) {
            log.error("error in moveGame, " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<BaseResponse> addGame(AddGameRequestDto request) {
        try {
            var newGame = addGameToCollection(request);
            return Response.success(newGame, "Success add game");
        }
        catch (Exception e) {
            log.error("error in addGame, " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public AddGameResponseDto addGameToCollection(AddGameRequestDto request) {
        try {
            var col = collectionRepo.findById(request.getCollectionId()).orElse(null);
            if(col == null) throw new NotFoundException("Collection not found", "addGameToCollection");

            // cek apakah gamenya sudah ada
            var cg = collectionGamesRepo.findByGameId(request.getGameId(), request.getCollectionId()).orElse(null);
            if(cg != null) throw new GameAlreadyExistException("addGameToCollection");

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
        try {
            removeGameFromCollection(request.getCollectionGamesId());
            return Response.success(null, "Success remove game");
        }
        catch (Exception e) {
            log.error("error in removeGame, " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void removeGameFromCollection(String colGamesId){
        try {
            var colGame = collectionGamesRepo.findById(colGamesId).orElse(null);
            if(colGame == null) throw new NotFoundException("Collection game not found", "removeGameFromCollection");
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
                return Response.success(allGamesInCol, "Success get collection detail");
            }
            else throw new NotFoundException("Collection not found", "getDetailCollection");
        }
        catch(Exception e) {
            log.error("error in getDetailCollection, " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<BaseResponse> getUserCollections(UserCollectionsRequestDto request) {
        try {
            var collections = userCollectionsRepo.findByUserId(request.getUserId());
            List<CollectionDetailDto> details = new ArrayList<>();
            collections.forEach(c -> details.add(CollectionDetailDto.builder()
                    .userCollectionsId(c.getUserCollectionsId())
                    .collectionId(c.getCollectionId().getCollectionId())
                    .collectionName(c.getCollectionId().getCollectionName())
                    .thumbnail(c.getCollectionId().getThumbnail()).build()));

            var data = UserCollectionsResponseDto.builder()
                    .userId(request.getUserId())
                    .collections(details)
                    .build();
            return Response.success(data, "Success get user collections");
        }
        catch(Exception e) {
            log.error("error in getUserCollections, " + e.getMessage());
            throw e;
        }
    }
}
