package com.camunda.test.fetch_picture.controller;

import com.camunda.test.fetch_picture.model.AnimalPicture;
import com.camunda.test.fetch_picture.service.AnimalPictureService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animalpictures")
public class AnimalPictureController {

    private AnimalPictureService animalPictureService;

    public AnimalPictureController(AnimalPictureService animalPictureService){
        this.animalPictureService = animalPictureService;
    }

    /*
        Api to save fetched animal's pictures
     */
    @PostMapping("/{typeOfAnimal}/{count}")
    public String savePictures(@PathVariable String typeOfAnimal, @PathVariable int count) {
        animalPictureService.saveFetchedPictures(typeOfAnimal, count);
        return count + " pictures saved successfully!";
    }

    /*
        API to fetch the last saved picture of an animal
     */
    @GetMapping("/{typeOfAnimal}/last")
    public AnimalPicture getLastPicture(@PathVariable String typeOfAnimal) {
        return animalPictureService.getTheLatestSavedPicture(typeOfAnimal);
    }
}
