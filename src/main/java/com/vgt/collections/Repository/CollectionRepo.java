package com.vgt.collections.Repository;

import com.vgt.collections.Model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRepo  extends JpaRepository<Collection, String> {
}
