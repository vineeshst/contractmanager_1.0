package com.manage.contract.repository;

import com.manage.contract.domain.MetaPrivileges;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface MetaSecurityPrivilegesRepository extends ReactiveMongoRepository<MetaPrivileges, String> {
    Mono<MetaPrivileges> findTopByOrderByDateDesc();
}
