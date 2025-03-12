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
@Table(name = "user_collections")
public class UserCollections {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_collections_id", nullable = false)
    private String userCollectionsId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collection_id")
    private Collection collectionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
