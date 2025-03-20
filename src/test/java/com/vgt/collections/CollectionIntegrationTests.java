package com.vgt.collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vgt.collections.Model.Collection;
import com.vgt.collections.Model.UserCollections;
import com.vgt.collections.Model.dto.request.UserCollectionsRequestDto;
import com.vgt.collections.Repository.CollectionRepo;
import com.vgt.collections.Repository.UserCollectionsRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class CollectionIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCollectionsRepo userCollectionsRepo;

    @Autowired
    private CollectionRepo collectionRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetUserCollections_Success() throws Exception {
        // Given: Persiapkan data dalam database
        Collection collection = collectionRepo.save(Collection.builder()
                .collectionId("col1") // note : matiin generated value di model untuk sementara supaya bisa jalan
                .collectionName("Favorite Games")
                .thumbnail("thumbnail.jpg")
                .build());

        userCollectionsRepo.save(UserCollections.builder()
                .userCollectionsId("uc1")
                .collectionId(collection)
                .userId("user123")
                .build());

        UserCollectionsRequestDto requestDto = UserCollectionsRequestDto.builder()
                .userId("user123")
                .build();
        String requestJson = objectMapper.writeValueAsString(requestDto); // Konversi DTO ke JSON


        // When: Panggil endpoint dengan MockMvc
        mockMvc.perform(post("/api/collections/user-collections")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success get user collections"))
                .andExpect(jsonPath("$.data.userId").value("user123"))
                .andExpect(jsonPath("$.data.collections[0].collectionId").value("col1"))
                .andExpect(jsonPath("$.data.collections[0].collectionName").value("Favorite Games"));
    }
}
