package com.manage.contract.repository;

import com.manage.contract.domain.SecurityGroup;
import com.manage.contract.domain.UserSecurityMapping;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface SecurityGroupRepository extends ReactiveMongoRepository<SecurityGroup, String> {
    @Query("{'members.$id': ObjectId('?0')}")
    Flux<SecurityGroup> findAllByUserObjectId(String userId);

    @Query("{'members.$id': '?0'}")
    Flux<SecurityGroup> findAllByUserId(String userId);
}
