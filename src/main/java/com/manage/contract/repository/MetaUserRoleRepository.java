package com.manage.contract.repository;

import com.manage.contract.domain.UserRole;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MetaUserRoleRepository extends ReactiveMongoRepository<UserRole, String> {}
