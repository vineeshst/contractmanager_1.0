package com.manage.contract.repository;

import com.manage.contract.domain.ContractClause;
import java.util.Set;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ContractClauseRepository extends ReactiveMongoRepository<ContractClause, String> {
    Flux<ContractClause> findByClauseNameIn(Set<String> templateNames);

    @Query(
        value = "{}",
        fields = "{'_id' : 1, 'clauseName' : 1, 'code' : 1, 'contractCategory' : 1,'contractType' :1, 'filePath' : 1" +
        ", 'language' : 1, 'status' : 1, 'description' : 1, 'isEditable' : 1, 'isDeviationAnalysis' : 1, 'isDependentClause' : 1" +
        ", 'version' : 1, 'versionComments' : 1, 'team' : 1, 'updatedOn' : 1, 'createdBy' : 1, 'createdDate' : 1" +
        "}"
    )
    Flux<ContractClause> findAllShallowDetails();
}
