package com.camunda.test.fetch_picture.repository;

import com.camunda.test.fetch_picture.model.AnimalPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalPictureRepository extends JpaRepository<AnimalPicture, Long> {
  AnimalPicture findTopByTypeOfAnimalOrderByIdDesc(String typeOfAnimal);
}
