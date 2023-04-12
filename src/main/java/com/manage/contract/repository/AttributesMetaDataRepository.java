package com.manage.contract.repository;

import com.manage.contract.domain.AttributesMetaData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AttributesMetaDataRepository extends ReactiveMongoRepository<AttributesMetaData, String> {

}
