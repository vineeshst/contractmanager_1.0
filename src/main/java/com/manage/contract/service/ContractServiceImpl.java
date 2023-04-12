package com.manage.contract.service;

import com.manage.contract.domain.*;
import com.manage.contract.repository.*;
import com.manage.contract.security.SecurityUtils;
import com.manage.contract.service.dto.*;
import com.manage.contract.web.rest.AccountResource;
import java.time.Instant;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ContractServiceImpl implements ContractService {

    private ContractCategoryRepository contractCategoryRepository;

    private ContractTypeRevisionRepository contractTypeRevisionRepository;

    private ContractTypeRepository contractTypeRepository;

    private ContractTemplateRevisionRepository contractTemplateRevisionRepository;

    private ContractClauseRevisionRepository contractClauseRevisionRepository;

    private ContractAgreementRevisionRepository contractAgreementRevisionRepository;

    private ContractTemplateRepository contractTemplateRepository;

    private ContractClauseRepository contractClauseRepository;

    private ContractAgreementRepository contractAgreementRepository;

    private ApiServiceRepository apiServiceRepository;

    private MasterDataRepository masterDataRepository;

    private AttributesMetaDataRepository attributesMetaDataRepository;

    private SecurityGroupRepository securityGroupRepository;

    private MetaSecurityPrivilegesRepository metaSecurityPrivilegesRepository;

    private UserSecurityMappingRepository userSecurityMappingRepository;

    private final UserService userService;

    private final MetaUserRoleRepository userRoleRepository;

    private final UserGroupRepository userGroupRepository;

    private final Logger log = LoggerFactory.getLogger(ContractServiceImpl.class);

    public ContractServiceImpl(
        ContractCategoryRepository contractCategoryRepository,
        ContractTypeRevisionRepository contractTypeRevisionRepository,
        ContractTemplateRevisionRepository contractTemplateRevisionRepository,
        ContractClauseRevisionRepository contractClauseRevisionRepository,
        ContractTypeRepository contractTypeRepository,
        ContractTemplateRepository contractTemplateRepository,
        ContractClauseRepository contractClauseRepository,
        ContractAgreementRevisionRepository contractAgreementRevisionRepository,
        ContractAgreementRepository contractAgreementRepository,
        ApiServiceRepository apiServiceRepository,
        MasterDataRepository masterDataRepository,
        AttributesMetaDataRepository attributesMetaDataRepository,
        SecurityGroupRepository securityGroupRepository,
        MetaSecurityPrivilegesRepository metaSecurityPrivilegesRepository,
        UserService userService,
        MetaUserRoleRepository userRoleRepository,
        UserGroupRepository userGroupRepository,
        UserSecurityMappingRepository userSecurityMappingRepository
    ) {
        this.contractCategoryRepository = contractCategoryRepository;
        this.contractTypeRevisionRepository = contractTypeRevisionRepository;
        this.contractTemplateRevisionRepository = contractTemplateRevisionRepository;
        this.contractClauseRevisionRepository = contractClauseRevisionRepository;
        this.contractAgreementRevisionRepository = contractAgreementRevisionRepository;
        this.contractTypeRepository = contractTypeRepository;
        this.contractTemplateRepository = contractTemplateRepository;
        this.contractClauseRepository = contractClauseRepository;
        this.contractAgreementRepository = contractAgreementRepository;
        this.apiServiceRepository = apiServiceRepository;
        this.masterDataRepository = masterDataRepository;
        this.attributesMetaDataRepository = attributesMetaDataRepository;
        this.securityGroupRepository = securityGroupRepository;
        this.metaSecurityPrivilegesRepository = metaSecurityPrivilegesRepository;
        this.userService = userService;
        this.userRoleRepository = userRoleRepository;
        this.userGroupRepository = userGroupRepository;
        this.userSecurityMappingRepository = userSecurityMappingRepository;
    }

    //    @Override
    //    public Mono<Boolean> addClause(ContractClause contractClause) {
    //        Mono<ContractType> contractTypeMono = contractTypeRepository.findByName(contractClause.getContractType().getName());
    //        return Mono.from(contractTypeMono).flatMap(contractType ->{
    //            Mono<ContractTypeRevision> addTypeVersionMono = addContractTypeVersion(contractType);
    //            contractType.getClauses().add(contractClause);
    //            contractType.incrementVersion();
    //            contractType.setUpdatedOn(Instant.now());
    //            return Mono.zip(updateType(contractType),addTypeVersionMono,contractClauseRepository.insert(contractClause))
    //                .flatMap(tuple-> Mono.just(true)).onErrorReturn(false);
    //        });
    //    }

    @Override
    public Mono<Boolean> addTemplate(ContractTemplate contractTemplate) {
        Mono<ContractType> contractTypeMono = contractTypeRepository.findById(contractTemplate.getContractType().getID());
        Mono<ContractTemplate> contractTemplateMono = contractTemplateRepository.insert(contractTemplate);
        return Mono
            .zip(contractTypeMono, contractTemplateMono)
            .flatMap(
                data -> {
                    ContractType contractType = data.getT1();
                    ContractTemplate contractTemplate1 = data.getT2();
                    Mono<ContractTypeRevision> addTypeVersionMono = addContractTypeVersion(contractType);
                    Mono.from(addTypeVersionMono).subscribe();
                    ContractTemplateForm contractTemplateForm = new ContractTemplateForm();
                    contractTemplateForm.setId(contractTemplate1.getID());
                    contractTemplateForm.setTemplateName(contractTemplate1.getTemplateName());
                    //            contractTemplateForm.setFilePath(contractTemplate1.getFilePath());
                    contractType.getTemplates().add(contractTemplateForm);
                    contractType.incrementVersion();
                    contractType.setUpdatedOn(Instant.now());
                    return Mono.from(updateType(contractType)).flatMap(contractType1 -> Mono.just(true)).onErrorReturn(false);
                }
            );
    }

    @Override
    public Mono<ContractTemplate> updateTemplate(ContractTemplate contractTemplate) {
        return contractTemplateRepository.save(contractTemplate);
    }

    @Override
    public Mono<Boolean> addClause(ContractClause contractClause) {
        Mono<ContractType> contractTypeMono = contractTypeRepository.findById(contractClause.getContractType().getID());
        Mono<ContractClause> contractClauseMono = contractClauseRepository.insert(contractClause);
        return Mono
            .zip(contractTypeMono, contractClauseMono)
            .flatMap(
                data -> {
                    ContractType contractType = data.getT1();
                    ContractClause contractClause1 = data.getT2();
                    Mono<ContractTypeRevision> addTypeVersionMono = addContractTypeVersion(contractType);
                    Mono.from(addTypeVersionMono).subscribe();
                    ContractClauseForm contractClauseForm = new ContractClauseForm();
                    contractClauseForm.setId(contractClause.getID());
                    contractClauseForm.setClauseName(contractClause1.getClauseName());
                    //            contractClauseForm.setFilePath(contractClause1.getFilePath());
                    contractType.getClauses().add(contractClauseForm);
                    contractType.incrementVersion();
                    contractType.setUpdatedOn(Instant.now());
                    return Mono.from(updateType(contractType)).flatMap(contractType1 -> Mono.just(true)).onErrorReturn(false);
                }
            );
    }

    @Override
    public Mono<ContractClause> updateClause(ContractClause contractClause) {
        return contractClauseRepository.save(contractClause);
    }

    @Override
    public Mono<ContractClause> getClauseById(String clauseId) {
        return contractClauseRepository.findById(clauseId);
    }

    @Override
    public Flux<ContractClause> getAllClauses() {
        return contractClauseRepository.findAllShallowDetails();
    }

    @Override
    public Mono<ContractCategory> addCategory(ContractCategory contractCategory) {
        return contractCategoryRepository.insert(contractCategory);
    }

    @Override
    public Mono<ContractCategory> getCategoryById(String categoryId) {
        return contractCategoryRepository.findById(categoryId);
        //        if (contractCategoryRepository.findById(categoryId).isPresent())
        //              return contractCategoryRepository.findById(categoryId).get();
        //        else
        //            return null;
    }

    @Override
    public Flux<ContractCategory> getAllCategories() {
        return contractCategoryRepository.findAll();
        //        contractCategoryRepository
        //            .findAll(Example.of(new Account(null, "owner", null)));
        //        return contractCategoryRepository.findAll();
    }

    @Override
    public Mono<ContractType> addType(ContractType contractType) {
        return contractTypeRepository.insert(contractType);
    }

    @Override
    public Mono<ContractAgreement> addAgreement(ContractAgreement contractAgreement) {
        return contractAgreementRepository.insert(contractAgreement);
    }

    @Override
    public Mono<ContractAgreement> updateAgreement(ContractAgreement contractAgreement) {
        return contractAgreementRepository.save(contractAgreement);
    }

    @Override
    public Flux<ContractAgreement> getAllAgreements() {
        Mono<User> userMono = userService.getUserWithAuthorities();
        Flux<ContractAgreement> userAgreementFlux = contractAgreementRepository.findAllAndExcludeAgreementFile();
        return userAgreementFlux
            .flatMap(
                contractAgreement -> {
                    var name = contractAgreement.getContractType().getName();

                    return userMono.map(
                        user -> {
                            var privilegeOptional = user
                                .getSecurityGroup()
                                .getPrivilege()
                                .stream()
                                .filter(p -> p.getName().equals(name))
                                .findFirst();
                            privilegeOptional.ifPresent(privilege -> contractAgreement.setCurrentUserPrivilege(privilege.getPrivilege()));
                            return contractAgreement;
                        }
                    );
                }
            )
            .filter(agreement -> agreement.getCurrentUserPrivilege() != Privileges.NONE);
    }

    @Override
    public Flux<ContractAgreement> getAllAgreementsByStatus(EntityStatus status) {
        Mono<User> userMono = userService.getUserWithAuthorities();
        Flux<ContractAgreement> userAgreementFlux = contractAgreementRepository.findAllWithStatus(status);
        return userAgreementFlux
            .flatMap(
                contractAgreement -> {
                    var name = contractAgreement.getContractType().getName();

                    return userMono.map(
                        user -> {
                            var privilegeOptional = user
                                .getSecurityGroup()
                                .getPrivilege()
                                .stream()
                                .filter(p -> p.getName().equals(name))
                                .findFirst();
                            privilegeOptional.ifPresent(privilege -> contractAgreement.setCurrentUserPrivilege(privilege.getPrivilege()));
                            return contractAgreement;
                        }
                    );
                }
            )
            .filter(agreement -> agreement.getCurrentUserPrivilege() != Privileges.NONE);
    }

    @Override
    public Mono<ContractAgreement> getAgreementByName(String agreementName) {
        return contractAgreementRepository.findByAgreementName(agreementName);
        //        if (contractAgreementRepository.findByAgreementName(agreementName).isPresent())
        //            return contractAgreementRepository.findByAgreementName(agreementName).get();
        //        else
        //            return null;
    }

    @Override
    public Mono<ContractAgreement> getAgreementById(String agreementId) {
        return contractAgreementRepository.findById(agreementId);
    }

    @Override
    public Mono<Void> deleteAgreement(ContractAgreement contractAgreement) {
        return contractAgreementRepository.delete(contractAgreement);
    }

    @Override
    public Mono<MasterData> addMasterData(MasterData masterData) {
        return masterDataRepository.insert(masterData);
    }

    @Override
    public Mono<MasterData> updateMasterData(MasterData masterData) {
        return masterDataRepository.save(masterData);
    }

    @Override
    public Flux<ContractTemplate> findByTemplateNamesIn(Set<String> templateNames) {
        return contractTemplateRepository.findByTemplateNameIn(templateNames);
    }

    @Override
    public Mono<ContractTemplate> getTemplateByName(String templateName) {
        return contractTemplateRepository.findByTemplateName(templateName);
    }

    @Override
    public Mono<ContractTemplate> getTemplateById(String templateId) {
        return contractTemplateRepository.findById(templateId);
    }

    @Override
    public Flux<ContractTemplate> getAllTemplates() {
        return contractTemplateRepository.findAllShallowDetails();
    }

    @Override
    public Mono<ContractType> updateType(ContractType contractType) {
        try {
            return contractTypeRepository.save(contractType);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    @Override
    public Flux<ContractType> getAllTypesByCategory(String categoryId) {
        return contractTypeRepository.findAllByContractCategory(categoryId);
    }

    @Override
    public Mono<ContractType> getTypeById(String typeId) {
        return contractTypeRepository.findById(typeId);
    }

    @Override
    public Mono<ContractType> getTypeByName(String typeName) {
        return contractTypeRepository.findByName(typeName);
        //        if (contractTypeRepository.findByName(typeName).isPresent())
        //             return contractTypeRepository.findByName(typeName).get();
        //        else
        //            return null;
    }

    @Override
    public Flux<ContractType> getAllTypes() {
        return contractTypeRepository.findAll().filter(x -> x.getStatus() != EntityStatus.DELETED);
    }

    @Override
    public Mono<AttributesMetaData> getAttributesMetaData() {
        //        if (attributesMetaDataRepository.findAll().stream().findFirst().isPresent())
        //            return attributesMetaDataRepository.findAll().stream().findFirst().get();
        //        else
        //            return null;

        return attributesMetaDataRepository.findAll().next();
    }

    @Override
    public Mono<AttributesMetaData> updateAttributesMetaData(AttributesMetaData attributesMetaData) {
        return attributesMetaDataRepository.save(attributesMetaData);
    }

    //    @Override
    //    public Mono<SecurityGroup> createSecurityGroup(SecurityGroup securityGroup) {
    //
    //        return securityGroupRepository.insert(securityGroup).map(
    //            securityGroup1 ->
    //                Flux
    //                    .fromIterable(securityGroup1.getMembers())
    //                        .flatMap(user ->
    //                            userSecurityMappingRepository
    //                                .insert(new UserSecurityMapping(securityGroup1, user)))
    //                    .subscribe())
    //                .then(Mono.just(securityGroup));
    //    }

    @Override
    public Mono<SecurityGroup> createSecurityGroup(SecurityGroup securityGroup) {
        return securityGroupRepository.save(securityGroup);
    }

    @Override
    public Flux<SecurityGroup> getSecurityGroups() {
        return securityGroupRepository.findAll();
    }

    @Override
    public Mono<SecurityGroup> getSecurityGroups(String securityGroupId) {
        return securityGroupRepository
            .findById(securityGroupId)
            .flatMap(
                securityGroup -> {
                    var privilege = securityGroup.getPrivilege();
                    return getMetaPrivileges()
                        .flatMap(
                            metaPrivileges -> {
                                metaPrivileges
                                    .getContractTypes()
                                    .forEach(
                                        contractType -> {
                                            if (privilege.stream().noneMatch(x -> Objects.equals(x.getName(), contractType.getName()))) {
                                                Privilege privilege1 = new Privilege();
                                                privilege1.setName(contractType.getName());
                                                privilege1.setPrivilege(Privileges.NONE);
                                                privilege.add(privilege1);
                                            }
                                        }
                                    );
                                return Mono.just(securityGroup);
                            }
                        );
                }
            );
    }

    @Override
    public Mono<MetaPrivileges> getMetaPrivileges() {
        return metaSecurityPrivilegesRepository
            .findTopByOrderByDateDesc()
            .map(
                metaPrivileges ->
                    Flux
                        .fromIterable(Arrays.asList(metaPrivileges.getPrivilege()))
                        .sort(Comparator.comparing(Privilege::getName))
                        .collectList()
                        .map(
                            sorted -> {
                                metaPrivileges.setPrivilege(sorted.toArray(new Privilege[0]));
                                return metaPrivileges;
                            }
                        )
                        .block()
            )
            .flatMap(
                metaPrivileges -> {
                    return Flux
                        .from(contractTypeRepository.findAll())
                        .collectList()
                        .doOnNext(metaPrivileges::setContractTypes)
                        .then(Mono.just(metaPrivileges));
                }
            );
    }

    @Override
    public Mono<SecurityGroup> updateSecurityGroup(SecurityGroup securityGroup) {
        var securityGroupOp = securityGroupRepository.findById(securityGroup.getId());

        return Mono
            .from(securityGroupOp)
            .flatMap(
                _securityGroup -> {
                    _securityGroup.setName(securityGroup.getName());
                    _securityGroup.setDescription(securityGroup.getDescription());
                    _securityGroup.setKpi(securityGroup.getKpi());
                    _securityGroup.setPrivilege(securityGroup.getPrivilege());
                    _securityGroup.setReport(securityGroup.getReport());
                    _securityGroup.setMembers(securityGroup.getMembers());
                    return securityGroupRepository.save(_securityGroup);
                }
            );
    }

    @Override
    public Flux<UserRole> getMetaUserRoles() {
        return userRoleRepository.findAll();
    }

    @Override
    public Mono<UserRole> updateMetaUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    @Override
    public Mono<UserGroup> createUserGroup(UserGroup userGroup) {
        return getCurrentUserName()
            .flatMap(
                name -> {
                    userGroup.setCreatedBy(name);
                    return userGroupRepository.insert(userGroup);
                }
            );
    }

    @Override
    public Mono<UserGroup> getUserGroup(String userGroupId) {
        return userGroupRepository.findById(userGroupId);
    }

    @Override
    public Mono<UserGroup> updateUserGroup(String userGroupId, UserGroup userGroup) {
        return userGroupRepository.findById(userGroupId).flatMap(userGroup1 -> userGroupRepository.save((userGroup)));
    }

    @Override
    public Flux<UserGroup> getUserGroups() {
        return userGroupRepository.findAll();
    }

    @Override
    public <T extends IEntity> Mono<T> updateEntity(T entity) {
        if (entity instanceof ContractType) {
            return (Mono<T>) contractTypeRepository.save((ContractType) entity);
        } else if (entity instanceof ContractAgreement) {
            return (Mono<T>) contractAgreementRepository.save((ContractAgreement) entity);
        } else if (entity instanceof ContractClause) {
            return (Mono<T>) contractClauseRepository.save((ContractClause) entity);
        } else if (entity instanceof ContractTemplate) {
            return (Mono<T>) contractTemplateRepository.save((ContractTemplate) entity);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> deleteAgreementByStatus(String agreementId) {
        return this.contractAgreementRepository.findById(agreementId)
            .flatMap(
                contractAgreement -> {
                    contractAgreement.setAgreementStatus(EntityStatus.DELETED);
                    return this.contractAgreementRepository.save(contractAgreement);
                }
            )
            .thenReturn(true);
    }

    public Mono<ContractTypeRevision> addContractTypeRevision(ContractTypeRevision contractTypeRevision) {
        return contractTypeRevisionRepository.save(contractTypeRevision);
    }

    @Override
    public Mono<ContractTypeRevision> updateContractTypeRevision(ContractTypeRevision contractTypeRevision) {
        return contractTypeRevisionRepository.save(contractTypeRevision);
    }

    @Override
    public Mono<ContractTypeRevision> getContractTypeRevisionById(String revisionId) {
        return contractTypeRevisionRepository.findById(revisionId);
    }

    public Mono<ContractTemplateRevision> addContractTemplateRevision(ContractTemplateRevision contractTemplateRevision) {
        return contractTemplateRevisionRepository.insert(contractTemplateRevision);
    }

    @Override
    public Mono<ContractTemplateRevision> updateContractTemplateRevision(ContractTemplateRevision contractTemplateRevision) {
        return contractTemplateRevisionRepository.save(contractTemplateRevision);
    }

    @Override
    public Mono<ContractTemplateRevision> getContractTemplateRevisionById(String revisionId) {
        return contractTemplateRevisionRepository.findById(revisionId);
    }

    @Override
    public Mono<ContractTemplateRevision> getMinifiedTemplateRevisionById(String revisionId) {
        return contractTemplateRevisionRepository
            .findById(revisionId)
            .flatMap(
                contractTemplateRevision -> {
                    for (ContractTemplate contractTemplate : contractTemplateRevision.contractTemplateRevisions) {
                        contractTemplate.setTemplateFile(null);
                    }
                    return Mono.just(contractTemplateRevision);
                }
            );
    }

    public Mono<ContractClauseRevision> addContractClauseRevision(ContractClauseRevision contractClauseRevision) {
        return contractClauseRevisionRepository.insert(contractClauseRevision);
    }

    @Override
    public Mono<ContractClauseRevision> updateContractClauseRevision(ContractClauseRevision contractClauseRevision) {
        return contractClauseRevisionRepository.save(contractClauseRevision);
    }

    @Override
    public Mono<ContractClauseRevision> getContractClauseRevisionById(String revisionId) {
        return contractClauseRevisionRepository.findById(revisionId);
    }

    @Override
    public Mono<ContractClauseRevision> getMinifiedClauseRevisionById(String revisionId) {
        return contractClauseRevisionRepository
            .findById(revisionId)
            .flatMap(
                contractClauseRevision -> {
                    for (ContractClause contractClause : contractClauseRevision.contractClauseRevisions) {
                        contractClause.setClauseFile(null);
                    }
                    return Mono.just(contractClauseRevision);
                }
            );
    }

    public Mono<ContractAgreementRevision> addContractAgreementRevision(ContractAgreementRevision contractAgreementRevision) {
        return contractAgreementRevisionRepository.insert(contractAgreementRevision);
    }

    @Override
    public Mono<ContractAgreementRevision> updateContractAgreementRevision(ContractAgreementRevision contractAgreementRevision) {
        return contractAgreementRevisionRepository.save(contractAgreementRevision);
    }

    @Override
    public Mono<ContractAgreementRevision> getContractAgreementRevisionById(String revisionId) {
        return contractAgreementRevisionRepository.findById(revisionId);
    }

    @Override
    public Mono<ContractAgreementRevision> getMinifiedAgreementRevisionById(String revisionId) {
        return contractAgreementRevisionRepository
            .findById(revisionId)
            .flatMap(
                contractAgreementRevision -> {
                    for (ContractAgreement contractAgreement : contractAgreementRevision.contractAgreementRevisions) {
                        contractAgreement.setAgreementFile(null);
                    }
                    return Mono.just(contractAgreementRevision);
                }
            );
    }

    @Override
    public Mono<ApiService> getApiService(String serviceName) {
        return apiServiceRepository.findByServiceName(serviceName);
    }

    public Flux<ApiService> getApiServices() {
        return apiServiceRepository.findAll();
    }

    @Override
    public Mono<ApiService> addApiService(ApiService apiService) {
        return apiServiceRepository.insert(apiService);
    }

    @Override
    public Mono<ApiService> updateApiService(ApiService apiService) {
        return apiServiceRepository.save(apiService);
    }

    @Override
    public boolean deleteApiService(String serviceName) {
        //        if (apiServiceRepository.findByServiceName(serviceName).isPresent()){
        //            apiServiceRepository.delete(apiServiceRepository.findByServiceName(serviceName).get());
        //            return true;
        //        }
        if (apiServiceRepository.delete(apiServiceRepository.findByServiceName(serviceName).block()).block() != null) return true;
        return false;
    }

    public Mono<ContractTypeRevision> addContractTypeVersion(ContractType contractType) {
        return getContractTypeRevisionById(contractType.getID())
            .flatMap(
                contractTypeRevision -> {
                    contractTypeRevision.setId(contractType.getID());
                    contractTypeRevision.setVersion(contractType.getVersion());
                    contractTypeRevision.setName(contractType.getName());
                    contractTypeRevision.addContractTypeRevisions(contractType);
                    return updateContractTypeRevision(contractTypeRevision);
                }
            )
            .switchIfEmpty(
                Mono.defer(
                    () -> {
                        ContractTypeRevision contractTypeRevision = new ContractTypeRevision(
                            contractType.getID(),
                            contractType.getVersion(),
                            contractType.getName()
                        );
                        contractTypeRevision.addContractTypeRevisions(contractType);
                        return addContractTypeRevision(contractTypeRevision);
                    }
                )
            );
    }

    private Mono<String> getCurrentUserName() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(
                login ->
                    userService
                        .getUserWithAuthoritiesByLogin(login)
                        .flatMap(user -> Mono.just(user.getFirstName() + " " + user.getLastName()))
            );
    }
}
