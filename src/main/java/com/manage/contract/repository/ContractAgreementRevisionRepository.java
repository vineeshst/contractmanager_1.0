package com.manage.contract.repository;

import com.manage.contract.domain.ContractAgreementRevision;
import java.util.List;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ContractAgreementRevisionRepository extends ReactiveMongoRepository<ContractAgreementRevision, String> {
    //    List<ContractAgreementRevision> findAllByAgreementName(String agreementName);
}
