package com.vgt.collections.Repository;

import com.vgt.collections.Model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepo  extends JpaRepository<Collection, String> {

}
