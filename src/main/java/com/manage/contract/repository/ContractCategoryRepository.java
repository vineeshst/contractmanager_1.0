package com.manage.contract.repository;

import com.manage.contract.domain.ContractCategory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractCategoryRepository extends ReactiveMongoRepository<ContractCategory, String> {
}
