package com.hyperion.datalake.repositories;


import com.hyperion.datalake.models.Hash;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component("mongoHashRepo")
public interface HashRepository extends MongoRepository<Hash, String> {
    Hash findByIteration(Integer iter);
    Long deleteByIteration(Integer iter);
//    List<Tutorial> findByPublished(boolean published);
}
