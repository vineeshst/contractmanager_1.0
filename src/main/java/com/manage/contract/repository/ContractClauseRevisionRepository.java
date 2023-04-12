package com.manage.contract.repository;

import com.manage.contract.domain.ContractClauseRevision;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ContractClauseRevisionRepository extends ReactiveMongoRepository<ContractClauseRevision, String> {}
