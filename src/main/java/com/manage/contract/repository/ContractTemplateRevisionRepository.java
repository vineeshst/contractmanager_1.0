package com.manage.contract.repository;

import com.manage.contract.domain.ContractTemplateRevision;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ContractTemplateRevisionRepository extends ReactiveMongoRepository<ContractTemplateRevision, String> {}
