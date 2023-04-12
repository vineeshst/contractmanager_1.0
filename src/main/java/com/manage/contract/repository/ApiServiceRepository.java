package com.manage.contract.repository;

import com.manage.contract.domain.ApiService;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ApiServiceRepository extends ReactiveMongoRepository<ApiService, String> {
    Mono<ApiService> findByServiceName(String serviceName);
}
