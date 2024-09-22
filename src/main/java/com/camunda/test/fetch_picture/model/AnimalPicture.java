package com.camunda.test.fetch_picture.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "animal_picture")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AnimalPicture {
    @Id
    @NonNull
    private String id;

    @NonNull
    private String typeOfAnimal;

    @NonNull
    private String url;

    @NonNull
    private String createdAt;
}
