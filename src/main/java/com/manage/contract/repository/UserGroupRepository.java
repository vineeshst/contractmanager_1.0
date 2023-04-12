package com.manage.contract.repository;

import com.manage.contract.domain.UserGroup;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserGroupRepository extends ReactiveMongoRepository<UserGroup, String> {}
