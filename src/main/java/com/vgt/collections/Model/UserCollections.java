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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id")
    private Collection collectionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
