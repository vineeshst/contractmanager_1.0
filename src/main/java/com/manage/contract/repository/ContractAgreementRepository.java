package com.manage.contract.repository;

import com.manage.contract.domain.ContractAgreement;
import com.manage.contract.domain.EntityStatus;
import java.util.Optional;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContractAgreementRepository extends ReactiveMongoRepository<ContractAgreement, String> {
    Mono<ContractAgreement> findByAgreementName(String agreementName);

    @Query(
        value = "{'agreementStatus': {$ne: 'DELETED'}}",
        fields = "{'_id' : 1, 'contractCategory' : 1, 'contractType' : 1, 'agreementName' : 1, 'agreementDescription' : 1, 'agreementCode' : 1, 'attributes' : 1" +
        ", 'agreementStatus' : 1, 'updatedOn' : 1, 'createdBy' : 1, 'createdDate' : 1, 'template' : 1, 'team' : 1, 'filePath' : 1" +
        ", 'signAgreementId' : 1, 'approverComment' : 1, 'version' : 1, 'auditLogs' : 1, 'currentUserPrivilege' : 1" +
        "}"
    )
    Flux<ContractAgreement> findAllAndExcludeAgreementFile();

    @Query(
        value = "{'agreementStatus': '?0'}",
        fields = "{'_id' : 1, 'contractCategory' : 1, 'contractType' : 1, 'agreementName' : 1, 'agreementDescription' : 1, 'agreementCode' : 1, 'attributes' : 1" +
        ", 'agreementStatus' : 1, 'updatedOn' : 1, 'createdBy' : 1, 'createdDate' : 1, 'template' : 1, 'team' : 1, 'filePath' : 1" +
        ", 'signAgreementId' : 1, 'approverComment' : 1, 'version' : 1, 'auditLogs' : 1, 'currentUserPrivilege' : 1" +
        "}"
    )
    Flux<ContractAgreement> findAllWithStatus(EntityStatus status);
}
