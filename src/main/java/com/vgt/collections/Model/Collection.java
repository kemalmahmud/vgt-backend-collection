package com.vgt.collections.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "collection")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "collection_id", nullable = false)
    private String collectionId;

    @Column(name = "collection_name", nullable = false)
    private String collectionName;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "mandatory")
    private Boolean mandatory;

    @OneToMany(mappedBy = "collectionId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private CollectionGames collectionGames;

    @OneToOne(mappedBy = "collectionId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserCollections userCollections;

}
