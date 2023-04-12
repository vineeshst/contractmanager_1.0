package com.manage.contract.repository;

import com.manage.contract.domain.ContractType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface ContractTypeRepository extends ReactiveMongoRepository<ContractType, String> {
    Flux<ContractType> findAllByContractCategory(String categoryId);
    Mono<ContractType> findByName(String contractTypeName);
}
