package com.manage.contract.repository;

import com.manage.contract.domain.ContractTypeRevision;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ContractTypeRevisionRepository extends ReactiveMongoRepository<ContractTypeRevision, String> {
    Flux<ContractTypeRevision> findAllByName(String contractTypeName);
}
