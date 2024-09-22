package com.camunda.test.fetch_picture.service;

import com.camunda.test.fetch_picture.model.AnimalPicture;
import com.camunda.test.fetch_picture.repository.AnimalPictureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AnimalPictureService {

    private static final Logger logger = LoggerFactory.getLogger(AnimalPictureService.class);

    private final AnimalPictureRepository repository;

    private WebClient webClient;

    public AnimalPictureService (AnimalPictureRepository repository){
        this.repository = repository;

    }

    /*
        Fetch animal's picture based on the type of animal and the number of pictures
     */
    private AnimalPicture[] pictureLookup(String typeOfAnimal, int numberOfPictures) {
        logger.info("Lookup {} pictures for animal type: {}", numberOfPictures, typeOfAnimal);
        String apiUrl = switch (typeOfAnimal.toLowerCase()) {
            case "cat" -> "https://api.thecatapi.com/v1/images/search";
            case "dog" -> "https://api.thedogapi.com/v1/images/search";
            case "bear" -> "https://placebear.com/200/300";
            default -> {
                logger.error("Invalid animal type: {}", typeOfAnimal);
                throw new IllegalStateException(typeOfAnimal.toLowerCase() + " does not exist");
            }
        };

        if ("bear".equals(typeOfAnimal.toLowerCase())){
            return  createBearImages(numberOfPictures, apiUrl);
        }
        logger.debug("Fetching image from: {}", apiUrl);
        webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("limit", numberOfPictures)
                        .build())
                .retrieve()
                .bodyToMono(AnimalPicture[].class)
                .block();
    }



    /*
      Saved fetched pictures
     */
    public void saveFetchedPictures(final String typeOfAnimal, final int numberOfPictures){

        AnimalPicture[]pictures = pictureLookup(typeOfAnimal, numberOfPictures);

        String createdAt;

        for (int i = 0; i < numberOfPictures; i++){
            try{
                AnimalPicture picture = pictures[i];

                createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                AnimalPicture animalPicture = new AnimalPicture(picture.getId(), typeOfAnimal, picture.getUrl(), createdAt);

                repository.save(animalPicture);

                logger.info("Saved picture {} for animal: {}", picture.getUrl(), picture.getTypeOfAnimal());

            }catch (Exception e){

                logger.error("Error while fetching or saving picture for animal type: {}", typeOfAnimal, e);
            }

        }
    }

    /*
      Get the latest Saved pictures based on the type of naimal
     */
    public AnimalPicture getTheLatestSavedPicture(String typeOfAnimal){

        logger.info("Fetching the last picture for animal type: {}", typeOfAnimal);

        AnimalPicture animalPicture = repository.findTopByTypeOfAnimalOrderByIdDesc(typeOfAnimal);

        if (animalPicture != null) {
            logger.info("Found picture: {} for animal type: {}", animalPicture.getUrl(), animalPicture);
        } else {
            logger.warn("No pictures found for animal type: {}", animalPicture);
        }

        return animalPicture;
    }

    private AnimalPicture[] createBearImages(final int numberOfPictures, final String url) {

        logger.info("Returning {} static bear images from URL: {}", numberOfPictures, url);

        AnimalPicture[] bearImages = new AnimalPicture[numberOfPictures];
        for (int i = 0; i < numberOfPictures; i++) {
            String id = String.valueOf(i);
            bearImages[i] = new AnimalPicture(id, "bear", url, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        return bearImages;
    }
}
