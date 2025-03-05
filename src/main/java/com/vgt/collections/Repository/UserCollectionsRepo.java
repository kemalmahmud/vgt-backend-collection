package com.vgt.collections.Repository;

import com.vgt.collections.Model.UserCollections;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCollectionsRepo extends JpaRepository<UserCollections, String> {
}
