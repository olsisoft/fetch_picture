package com.camunda.test.fetch_picture.service;

import com.camunda.test.fetch_picture.model.AnimalPicture;
import com.camunda.test.fetch_picture.repository.AnimalPictureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnimalPictureServiceTest {

    @Mock
    private AnimalPictureRepository repository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AnimalPictureService animalPictureService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // Test saveFetchedPictures for a valid animal type
    @Test
    public void testSaveFetchedPicturesForCats() {
        AnimalPicture[] mockPictures = new AnimalPicture[]{
                new AnimalPicture("1", "cat", "https://mockurl.com/cat1.jpg", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
                new AnimalPicture("2", "cat", "https://mockurl.com/cat2.jpg", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        };

        String apiUrl = "https://api.thecatapi.com/v1/images/search";

        when(webClientBuilder.baseUrl(apiUrl)).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri((URI) any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AnimalPicture[].class)).thenReturn(Mono.just(mockPictures));

        animalPictureService.saveFetchedPictures("cat", 2);

        verify(repository, times(2)).save(any(AnimalPicture.class));
    }

    // Test saveFetchedPictures for bears (static image case)
    @Test
    public void testSaveFetchedPicturesForBears() {

        animalPictureService.saveFetchedPictures("bear", 2);

        verify(repository, times(2)).save(any(AnimalPicture.class));
    }

    // Test saveFetchedPictures for invalid animal type
    @Test
    public void testSaveFetchedPicturesForInvalidAnimalType() {

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            animalPictureService.saveFetchedPictures("elephant", 2);
        });

        assertEquals("elephant does not exist", exception.getMessage());
    }

    // Test getTheLatestSavedPicture
    @Test
    public void testGetTheLatestSavedPicture() {

        AnimalPicture mockPicture = new AnimalPicture("1", "cat", "https://mockurl.com/cat1.jpg", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        when(repository.findTopByTypeOfAnimalOrderByIdDesc("cat")).thenReturn(mockPicture);

        // Act
        AnimalPicture result = animalPictureService.getTheLatestSavedPicture("cat");

        // Assert
        assertNotNull(result);
        assertEquals("https://mockurl.com/cat1.jpg", result.getUrl());
        assertEquals("cat", result.getTypeOfAnimal());
    }

    // Test getTheLatestSavedPicture for missing animal type
    @Test
    public void testGetTheLatestSavedPictureNoResults() {
        // Arrange
        when(repository.findTopByTypeOfAnimalOrderByIdDesc("cat")).thenReturn(null);

        // Act
        AnimalPicture result = animalPictureService.getTheLatestSavedPicture("cat");

        // Assert
        assertNull(result);
    }
}
