package com.manage.contract.repository;

import com.manage.contract.domain.MasterData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface MasterDataRepository extends ReactiveMongoRepository<MasterData, String> {
}
