package com.manage.contract.repository;

import com.manage.contract.domain.UserSecurityMapping;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface UserSecurityMappingRepository extends ReactiveMongoRepository<UserSecurityMapping, String> {
    @Query("{ 'user.$id': '?0'}")
    Flux<UserSecurityMapping> findAllByUserId(String userId);

    @Query("{ 'user.$id': ObjectId('?0')}")
    Flux<UserSecurityMapping> findAllByUserObjectId(String userId);
}
