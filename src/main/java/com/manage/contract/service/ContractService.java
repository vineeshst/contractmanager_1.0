package com.manage.contract.service;

import com.manage.contract.domain.*;
import com.manage.contract.service.dto.IEntity;
import java.util.Set;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContractService {
    //    public ContractAgreement createContract(CreateContract createContract);
    public Mono<ContractCategory> addCategory(ContractCategory contractCategory);

    public Mono<ContractCategory> getCategoryById(String categoryId);

    public Flux<ContractCategory> getAllCategories();

    public Mono<ContractType> addType(ContractType contractType);

    public Mono<ContractType> updateType(ContractType contractType);

    public Flux<ContractType> getAllTypesByCategory(String categoryId);

    public Mono<ContractType> getTypeById(String typeId);

    public Mono<ContractType> getTypeByName(String typeName);

    public Mono<ContractAgreement> addAgreement(ContractAgreement contractAgreement);

    public Mono<ContractAgreement> updateAgreement(ContractAgreement contractAgreement);

    public Flux<ContractAgreement> getAllAgreements();

    public Flux<ContractAgreement> getAllAgreementsByStatus(EntityStatus status);

    public Mono<ContractAgreement> getAgreementByName(String agreementName);

    public Mono<ContractAgreement> getAgreementById(String agreementId);

    public Mono<Void> deleteAgreement(ContractAgreement contractAgreement);

    public Mono<MasterData> addMasterData(MasterData masterData);

    public Mono<MasterData> updateMasterData(MasterData masterData);

    public Mono<Boolean> addTemplate(ContractTemplate contractTemplate);

    public Mono<ContractTemplate> updateTemplate(ContractTemplate contractTemplate);

    public Flux<ContractTemplate> findByTemplateNamesIn(Set<String> templateNames);

    public Mono<ContractTemplate> getTemplateByName(String templateName);

    public Mono<ContractTemplate> getTemplateById(String templateId);

    public Flux<ContractTemplate> getAllTemplates();

    public Mono<Boolean> addClause(ContractClause contractClause);

    public Mono<ContractClause> updateClause(ContractClause contractClause);

    public Mono<ContractClause> getClauseById(String clauseId);

    public Flux<ContractClause> getAllClauses();

    public Mono<ContractTypeRevision> getContractTypeRevisionById(String revisionId);

    public Mono<ContractTypeRevision> addContractTypeRevision(ContractTypeRevision contractTypeRevision);

    public Mono<ContractTypeRevision> updateContractTypeRevision(ContractTypeRevision contractTypeRevision);

    public Mono<ContractTemplateRevision> getContractTemplateRevisionById(String revisionId);

    public Mono<ContractTemplateRevision> getMinifiedTemplateRevisionById(String revisionId);

    public Mono<ContractTemplateRevision> addContractTemplateRevision(ContractTemplateRevision contractTemplateRevision);

    public Mono<ContractTemplateRevision> updateContractTemplateRevision(ContractTemplateRevision contractTemplateRevision);

    public Mono<ContractClauseRevision> getContractClauseRevisionById(String revisionId);

    public Mono<ContractClauseRevision> getMinifiedClauseRevisionById(String revisionId);

    public Mono<ContractClauseRevision> addContractClauseRevision(ContractClauseRevision contractClauseRevision);

    public Mono<ContractClauseRevision> updateContractClauseRevision(ContractClauseRevision contractClauseRevision);

    public Mono<ContractAgreementRevision> getContractAgreementRevisionById(String revisionId);

    public Mono<ContractAgreementRevision> getMinifiedAgreementRevisionById(String revisionId);

    public Mono<ContractAgreementRevision> addContractAgreementRevision(ContractAgreementRevision contractAgreementRevision);

    public Mono<ContractAgreementRevision> updateContractAgreementRevision(ContractAgreementRevision contractAgreementRevision);

    public Flux<ApiService> getApiServices();

    public Mono<ApiService> getApiService(String serviceName);

    public Mono<ApiService> addApiService(ApiService apiService);

    public Mono<ApiService> updateApiService(ApiService apiService);

    public boolean deleteApiService(String serviceName);

    Flux<ContractType> getAllTypes();
    Mono<AttributesMetaData> getAttributesMetaData();

    public Mono<AttributesMetaData> updateAttributesMetaData(AttributesMetaData attributesMetaData);

    Mono<SecurityGroup> createSecurityGroup(SecurityGroup securityGroup);

    Flux<SecurityGroup> getSecurityGroups();

    Mono<SecurityGroup> getSecurityGroups(String securityGroupId);

    Mono<MetaPrivileges> getMetaPrivileges();

    Mono<SecurityGroup> updateSecurityGroup(SecurityGroup securityGroup);

    Flux<UserRole> getMetaUserRoles();

    Mono<UserRole> updateMetaUserRole(UserRole userRole);

    Mono<UserGroup> createUserGroup(UserGroup userGroup);

    Mono<UserGroup> getUserGroup(String userGroupId);

    Mono<UserGroup> updateUserGroup(String userGroupId, UserGroup userGroup);

    Flux<UserGroup> getUserGroups();

    <T extends IEntity> Mono<T> updateEntity(T entity);

    Mono<Boolean> deleteAgreementByStatus(String agreementId);
}
