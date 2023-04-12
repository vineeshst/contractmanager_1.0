package com.manage.contract.repository;

import com.manage.contract.domain.ContractTemplate;
import java.util.Set;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContractTemplateRepository extends ReactiveMongoRepository<ContractTemplate, String> {
    Flux<ContractTemplate> findByTemplateNameIn(Set<String> templateNames);
    Mono<ContractTemplate> findByTemplateName(String templateName);

    @Query(
        value = "{}",
        fields = "{'_id' : 1, 'templateName' : 1, 'code' : 1, 'contractCategory' : 1,'contractType' :1, 'filePath' : 1" +
        ", 'language' : 1, 'secondaryLanguage' : 1, 'dateFormat' : 1, 'effectiveFrom' : 1, 'effectiveTo' : 1, 'status' : 1, 'isPrimary' : 1" +
        ", 'description' : 1, 'version' : 1, 'versionComments' : 1, 'team' : 1, 'updatedOn' : 1, 'createdBy' : 1, 'createdDate' : 1" +
        "}"
    )
    Flux<ContractTemplate> findAllShallowDetails();

    @Query(
        value = "{}",
        fields = "{'_id' : 1, 'templateName' : 1, 'code' : 1, 'contractCategory' : 1,'contractType' :1, 'filePath' : 1" +
        ", 'language' : 1, 'secondaryLanguage' : 1, 'dateFormat' : 1, 'effectiveFrom' : 1, 'effectiveTo' : 1, 'status' : 1, 'isPrimary' : 1" +
        ", 'description' : 1, 'version' : 1, 'versionComments' : 1, 'team' : 1, 'updatedOn' : 1, 'createdBy' : 1, 'createdDate' : 1" +
        "}"
    )
    Flux<ContractTemplate> findAllAndExcludeTemplateFile();
}
