package com.vgt.collections;

import com.vgt.collections.Model.Collection;
import com.vgt.collections.Model.UserCollections;
import com.vgt.collections.Model.dto.request.UserCollectionsRequestDto;
import com.vgt.collections.Model.dto.response.CollectionDetailDto;
import com.vgt.collections.Model.dto.response.UserCollectionsResponseDto;
import com.vgt.collections.Repository.UserCollectionsRepo;
import com.vgt.collections.Service.CollectionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CollectionsApplicationTests {

	@Test
	void contextLoads() {
	}

	@Mock
	private UserCollectionsRepo userCollectionsRepo;

	@InjectMocks
	private CollectionService collectionService; // inject mock ke service

	@Test
	void testGetUserCollections_Success() {
		String userId = "user123";
		UserCollectionsRequestDto requestDto = UserCollectionsRequestDto.builder()
				.userId(userId)
				.build();

		// mock collection data
		Collection collection = Collection.builder()
				.collectionId("col1")
				.collectionName("Favorite Games")
				.thumbnail("thumbnail.jpg")
				.build();

		UserCollections userCollections = UserCollections.builder()
				.userCollectionsId("uc1")
				.collectionId(collection)
				.build();


		List<UserCollections> mockCollections = List.of(userCollections);
		// kembalikan collection ketika pakai user id = user123
		when(userCollectionsRepo.findByUserId(userId)).thenReturn(mockCollections);

		// Call method
		var response = collectionService.getUserCollections(requestDto);

		// check not null
		assertNotNull(response);
		// check status code
		assertEquals(HttpStatus.OK, response.getStatusCode());

		var baseResponse = response.getBody();
		assertNotNull(baseResponse); // check if base response null
		assertEquals(200, baseResponse.getStatus()); // check status base response
		assertEquals("Success get user collections", baseResponse.getMessage()); // check success message

		var data = (UserCollectionsResponseDto)baseResponse.getData();
		assertNotNull(data); // check if data null
		assertEquals(userId, data.getUserId()); // check user id
		assertEquals(1, data.getCollections().size()); // check collection size (mock only have 1 collection)

		// check collection data
		CollectionDetailDto collectionDetail = data.getCollections().get(0);
		assertEquals("uc1", collectionDetail.getUserCollectionsId());
		assertEquals("col1", collectionDetail.getCollectionId());
		assertEquals("Favorite Games", collectionDetail.getCollectionName());
		assertEquals("thumbnail.jpg", collectionDetail.getThumbnail());

	}

	@Test
	void testGetUserCollections_Exception() {

		String userId = "user123";
		UserCollectionsRequestDto requestDto = UserCollectionsRequestDto.builder()
				.userId(userId)
				.build();

		// check error thrown
		when(userCollectionsRepo.findByUserId(userId)).thenThrow(new RuntimeException("Something wrong happened"));

		Exception exception = assertThrows(RuntimeException.class, () -> {
			collectionService.getUserCollections(requestDto);
		});

		assertEquals("Something wrong happened", exception.getMessage());
	}
}
