package com.manage.contract.controller;

import com.manage.contract.domain.*;
import com.manage.contract.repository.UserTasksRepository;
import com.manage.contract.security.SecurityUtils;
import com.manage.contract.service.ContractService;
import com.manage.contract.service.MailService;
import com.manage.contract.service.dto.*;
import com.manage.contract.service.dto.ContractTypeForm;
import com.manage.contract.service.dto.ContractTypeStatus;
import com.manage.contract.service.dto.ContractTypeStatusDTO;
import com.manage.contract.service.dto.ResponseMessage;
import com.manage.contract.service.mapper.ContractTypeMapper;
import com.manage.contract.util.RandomString;
import com.manage.contract.util.UserTaskUtil;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "api/types")
public class ContractTypeController {

    private final Logger log = LoggerFactory.getLogger(ContractTypeController.class);

    private final MailService mailService;
    private final ContractService contractService;
    private final ContractTypeMapper contractTypeMapper;
    private final UserTasksRepository userTasksRepository;
    private final UserTaskUtil userTaskUtil;

    public ContractTypeController(
        ContractService contractService,
        ContractTypeMapper contractTypeMapper,
        UserTasksRepository userTasksRepository,
        MailService mailService,
        UserTaskUtil userTaskUtil
    ) {
        this.contractService = contractService;
        this.contractTypeMapper = contractTypeMapper;
        this.userTasksRepository = userTasksRepository;
        this.mailService = mailService;
        this.userTaskUtil = userTaskUtil;
    }

    @GetMapping("/{categoryId}")
    public Flux<ContractType> getContractTypesByCategory(@PathVariable("categoryId") String categoryId) {
        return contractService.getAllTypesByCategory(categoryId);
    }

    @GetMapping("/agreement/revisions/{contractTypeId}")
    public Mono<ContractTypeRevision> getContractTypeVersions(@PathVariable("contractTypeId") String ContractTypeId) {
        return contractService.getContractTypeRevisionById(ContractTypeId);
    }

    @PutMapping("/agreement/rollback")
    public Mono<ResponseEntity<ResponseMessage>> rollBackAgreement(@RequestBody ContractType contractType) {
        System.out.println(contractType.getID());
        Mono<ContractType> contractTypeMono = contractService.getTypeById(contractType.getID());
        Mono<ContractTypeRevision> contractTypeRevisionMono = contractService.getContractTypeRevisionById(contractType.getID());
        return Mono
            .zip(contractTypeMono, contractTypeRevisionMono)
            .flatMap(
                data -> {
                    ContractType contractType1 = data.getT1();
                    ContractTypeRevision contractTypeRevision = data.getT2();
                    ContractType rollBackContractType = null;
                    for (ContractType contractType2 : contractTypeRevision.contractTypeRevisions) {
                        if (contractType2.getVersion() == contractType.getVersion()) {
                            rollBackContractType = contractType2;
                            break;
                        }
                    }
                    if (rollBackContractType != null) {
                        ContractType finalRollBackContractType = rollBackContractType;
                        return addContractTypeVersion(contractType1)
                            .then(
                                logContractTypeHistory(
                                    contractType1,
                                    "ContractType version rolled back from " +
                                    contractType1.getVersion() +
                                    " to " +
                                    rollBackContractType.getVersion()
                                )
                                    .flatMap(
                                        contractType2 -> {
                                            //contractType2.incrementVersion();
                                            //finalRollBackContractType.setVersion(contractType2.getVersion());
                                            //finalRollBackContractType.setHistory(contractType2.getHistory());
                                            finalRollBackContractType.setUpdatedOn(Instant.now());
                                            return contractService.updateType(finalRollBackContractType);
                                        }
                                    )
                            )
                            .then(
                                Mono.just(
                                    new ResponseEntity<>(
                                        new ResponseMessage("ContractType Version is rolled back successfully"),
                                        HttpStatus.OK
                                    )
                                )
                            )
                            .onErrorReturn(
                                new ResponseEntity<>(
                                    new ResponseMessage("Could not roll back ContractType Version"),
                                    HttpStatus.EXPECTATION_FAILED
                                )
                            );
                    } else {
                        return Mono.just(
                            new ResponseEntity<>(
                                new ResponseMessage("Could not roll back ContractType Version"),
                                HttpStatus.EXPECTATION_FAILED
                            )
                        );
                    }
                }
            );
    }

    @GetMapping("/agreement")
    public Flux<ContractType> getContractTypes() {
        return contractService.getAllTypes();
    }

    @PutMapping("/agreement")
    public Mono<ResponseEntity<ResponseMessage>> updateType(@RequestBody ContractTypeForm contractTypeForm) {
        try {
            Mono<ContractCategory> categoryMono = contractService.getCategoryById(contractTypeForm.getContractCategory());
            Mono<ContractType> contractTypeMono = contractService.getTypeByName(contractTypeForm.getContractTypeName());
            return Mono
                .zip(categoryMono, contractTypeMono)
                .flatMap(
                    tuple -> {
                        ContractType contractType = tuple.getT2();
                        ContractCategory contractCategory = tuple.getT1();
                        Mono<ContractTypeRevision> addTypeVersionMono = addContractTypeVersion(contractType);
                        return Mono
                            .from(addTypeVersionMono)
                            .flatMap(
                                contractType1 -> handleContractTypeWorkflow(contractType, new ContractTypeStatusDTO(EntityStatus.DRAFT))
                            )
                            .flatMap(
                                contractType2 -> {
                                    ContractType contractType3 = contractTypeMapper.contractTypeFormToContractType(
                                        contractTypeForm,
                                        contractType
                                    );
                                    contractType3.setContractCategory(contractCategory);
                                    return contractService.updateType(contractType3);
                                }
                            )
                            .then(
                                Mono.just(new ResponseEntity<>(new ResponseMessage("Contract Type updated successfully"), HttpStatus.OK))
                            );
                    }
                );
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not update Contract Type"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    private Mono<ContractTypeRevision> addContractTypeVersion(ContractType contractType) {
        return contractService
            .getContractTypeRevisionById(contractType.getID())
            .flatMap(
                contractTypeRevision -> {
                    //            contractTypeRevision.setId(contractType.getID());
                    contractTypeRevision.setVersion(contractType.getVersion());
                    contractTypeRevision.setName(contractType.getName());
                    contractTypeRevision.addContractTypeRevisions(contractType);
                    return contractService.updateContractTypeRevision(contractTypeRevision);
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
                        return contractService.addContractTypeRevision(contractTypeRevision);
                    }
                )
            );
    }

    @PostMapping("/agreement")
    public Mono<ResponseEntity<ResponseMessage>> addType(@RequestBody ContractTypeForm contractTypeForm) {
        try {
            Mono<ContractCategory> categoryMono = contractService.getCategoryById(contractTypeForm.getContractCategory());

            ContractType contractType = new ContractType();
            //        contractType.setContractCategory(contractCategory);
            contractType.setName(contractTypeForm.getContractTypeName());
            contractType.setCode(new RandomString(12).nextString());
            contractType.setDescription(contractTypeForm.getDescription());
            contractType.setAllowThirdPartyPaper(contractTypeForm.isAllowThirdPartyPaper());
            contractType.setAllowClauseAssembly(contractTypeForm.isAllowClauseAssembly());
            contractType.setQrCode(contractTypeForm.isQrCode());
            contractType.setAllowCopyWithAssociations(contractTypeForm.isAllowCopyWithAssociations());
            contractType.setTwoColumnAttributeLayout(contractTypeForm.isTwoColumnAttributeLayout());
            contractType.setEnableCollaboration(contractTypeForm.isEnableCollaboration());
            contractType.setEnableAutoSupersede(contractTypeForm.isEnableAutoSupersede());
            contractType.setExpandDropdownOnMouseHover(contractTypeForm.isExpandDropdownOnMouseHover());

            //Add Attribute key to Attribute meta data
            //setAttributeKeys(contractTypeForm);
            contractType.setAttributes(contractTypeForm.getAttributes());
            contractType.setAssociations(contractTypeForm.getAssociations());
            contractType.setVersion(0);
            contractType.setUpdatedOn(Instant.now());
            contractType.setCreatedBy(contractTypeForm.getCreatedBy());
            contractType.setTeam(contractTypeForm.getTeam());
            contractType.setStatus(contractTypeForm.getStatus());
            contractType.setDisplayPreference(contractTypeForm.getDisplayPreference());
            contractType.setTeamGroups(contractTypeForm.getTeamGroups());
            //contractType.setHistory(new EventHistory("Contract Type Created", SecurityUtils.getCurrentUserLogin().get(), Instant.now()));
            Mono<ContractType> addTypeMono = Mono
                .from(categoryMono)
                .flatMap(
                    category -> {
                        contractType.setContractCategory(category);
                        return Mono
                            .from(handleContractTypeWorkflow(contractType, new ContractTypeStatusDTO(EntityStatus.CREATED)))
                            .then(contractService.addType(contractType));
                    }
                );

            return Mono
                .from(addTypeMono)
                .flatMap(
                    addedType -> Mono.just(new ResponseEntity<>(new ResponseMessage("Contract Type created successfully"), HttpStatus.OK))
                )
                .onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not create Contract Type"), HttpStatus.EXPECTATION_FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(new ResponseMessage("Could not create Contract Type"), HttpStatus.EXPECTATION_FAILED));
        }
    }

    @GetMapping("/agreement/{contractTypeId}")
    public Mono<ContractType> getContractType(@PathVariable("contractTypeId") String contractTypeId) {
        return contractService
            .getTypeById(contractTypeId)
            .flatMap(
                contractType ->
                    getEntityRole(contractType.getTeam())
                        .flatMap(
                            role -> {
                                contractType.setEntityRole(role);
                                return Mono.just(contractType);
                            }
                        )
            );
    }

    // @PutMapping("agreement/{contractTypeId}")
    // public Mono<ResponseEntity<ResponseMessage>> updateContractTypeStatus(
    //     @PathVariable("contractTypeId") String contractTypeId,
    //     @RequestBody ContractTypeStatusDTO status
    // ) {
    //     try {
    //         return contractService
    //             .getTypeById(contractTypeId)
    //             .flatMap(
    //                 contractType -> {
    //                     contractType.setStatus(status.getStatus());
    //                     return handleContractTypeWorkflow(contractType, status)
    //                         .then(
    //                             contractService
    //                                 .updateType(contractType)
    //                                 .flatMap(
    //                                     updatedType ->
    //                                         Mono.just(
    //                                             new ResponseEntity<>(
    //                                                 new ResponseMessage("Contract Type Status updated successfully"),
    //                                                 HttpStatus.OK
    //                                             )
    //                                         )
    //                                 )
    //                                 .onErrorReturn(
    //                                     new ResponseEntity<>(
    //                                         new ResponseMessage("Could not update Contract Type Status"),
    //                                         HttpStatus.EXPECTATION_FAILED
    //                                     )
    //                                 )
    //                         );
    //                 }
    //             );
    //         //            ContractType contractType = contractService.getTypeById(contractTypeId).block();
    //         //            if(contractType==null)
    //         //                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Contract Type Not Found"));
    //         //            if(!HandleContractTypeWorkflow(contractType,status))
    //         //                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Could not Update Contract Type"));
    //         //            contractType.setStatus(status.getStatus());
    //         //            contractService.updateType(contractType);
    //         //            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Status Updated Successfully"));
    //     } catch (Exception e) {
    //         return Mono.just(
    //             new ResponseEntity<>(new ResponseMessage("Could not update Contract Type Status"), HttpStatus.EXPECTATION_FAILED)
    //         );
    //     }
    // }

    @PutMapping("agreement/{contractTypeId}")
    public Mono<ResponseEntity<EntityStatus>> updateContractTypeStatus(
        @PathVariable("contractTypeId") String contractTypeId,
        @RequestBody ContractTypeStatusDTO status
    ) {
        try {
            Mono<ContractType> contractTypeMono = contractService.getTypeById(contractTypeId);

            return Mono
                .from(contractTypeMono)
                .flatMap(
                    contractType ->
                        Mono.from(
                            handleContractTypeWorkflow(contractType, status)
                                .flatMap(
                                    typeStatus -> {
                                        contractType.setStatus(typeStatus);
                                        return contractService
                                            .updateType(contractType)
                                            .thenReturn(new ResponseEntity<>(typeStatus, HttpStatus.OK));
                                    }
                                )
                        )
                )
                .onErrorReturn(new ResponseEntity<>(EntityStatus.ERROR, HttpStatus.EXPECTATION_FAILED));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>(EntityStatus.ERROR, HttpStatus.EXPECTATION_FAILED));
        }
    }

    private Mono<EntityStatus> handleContractTypeWorkflow(ContractType contractType, ContractTypeStatusDTO statusDto) {
        switch (statusDto.status) {
            case CREATED:
                return logContractTypeHistory(contractType, "Contract Type Created").thenReturn(EntityStatus.CREATED);
            case APPROVED:
                return userTaskUtil
                    .approve(contractType)
                    .flatMap(
                        contractTypeStatus ->
                            Mono.from(logContractTypeHistory(contractType, "Contract Type Approved")).thenReturn(contractTypeStatus)
                    );
            case APPROVAL_PENDING:
                userTaskUtil.sendEntityForApproval(contractType, "Please approve the Contract Type");
                return logContractTypeHistory(contractType, "Contract Type Sent for Approval " + statusDto.message)
                    .thenReturn(EntityStatus.APPROVAL_PENDING);
            case DRAFT:
                //userTaskUtil.removeAllTask(contractType.getID(), TaskType.CONTRACT_TYPE_APPROVAL).subscribe();
                return logContractTypeHistory(contractType, "Contract Type Modified").thenReturn(EntityStatus.DRAFT);
            case REJECTED:
                return userTaskUtil
                    .reject(contractType)
                    .flatMap(
                        contractTypeStatus ->
                            Mono.from(logContractTypeHistory(contractType, "Contract Type Rejected")).thenReturn(contractTypeStatus)
                    );
            case DELETED:
                return Mono.just(EntityStatus.DELETED);
            default:
                return Mono.just(EntityStatus.DRAFT);
        }
    }

    private Mono<UserTasks> addUserTask(UserTasks userTasks) {
        return userTasksRepository
            .findByUserId(userTasks.getUser().getId())
            .flatMap(
                userTasks1 -> {
                    userTasks1.getTasks().add(userTasks.getTasks().get(0));
                    return userTasksRepository.save(userTasks1);
                }
            )
            .switchIfEmpty(userTasksRepository.insert(userTasks));
    }

    private void setAttributeKeys(ContractTypeForm contractTypeForm) {
        List<Map<String, Object>> attributesMetaData = contractTypeForm.getAttributesMetaData();
        for (int i = 0; i < attributesMetaData.size(); i++) {
            Map<String, Object> attributesMetaMap = attributesMetaData.get(i);
            for (Map.Entry<String, Object> attributesEntry : attributesMetaMap.entrySet()) {
                Map<String, String> attributeMap = (Map<String, String>) attributesEntry.getValue();
                attributeMap.put(
                    "attribute_key",
                    (contractTypeForm.getContractTypeName() + "_" + attributesEntry.getKey()).replaceAll(" ", "_").toLowerCase()
                );
            }
        }
    }

    private Mono<ContractType> logContractTypeHistory(ContractType contractType, String message) {
        return getUser()
            .flatMap(
                user -> Mono.just(contractType.getHistory().add(new EventHistory(message, user, Instant.now()))).thenReturn(contractType)
            );
    }

    //    private Mono<ContractTypeRevision> addContractTypeVersion(ContractType contractType) {
    //        return contractService
    //            .getContractTypeRevisionById(contractType.getID())
    //            .flatMap(
    //                contractTypeRevision -> {
    //                    contractTypeRevision.setVersion(contractType.getVersion());
    //                    contractTypeRevision.setName(contractType.getName());
    //                    contractTypeRevision.addContractTypeRevisions(contractType);
    //                    return contractService.updateContractTypeRevision(contractTypeRevision);
    //                }
    //            )
    //            .switchIfEmpty(
    //                Mono.defer(
    //                    () -> {
    //                        ContractTypeRevision contractTypeRevision = new ContractTypeRevision(
    //                            contractType.getID(),
    //                            contractType.getVersion(),
    //                            contractType.getName()
    //                        );
    //                        contractTypeRevision.addContractTypeRevisions(contractType);
    //                        return contractService.addContractTypeRevision(contractTypeRevision);
    //                    }
    //                )
    //            );
    //    }

    private Mono<String> getUser() {
        return SecurityUtils.getCurrentUserLogin();
    }

    private Mono<String> getEntityRole(Team team) {
        return getUser()
            .flatMap(
                login -> {
                    if (team.getPrimaryOwner().getLogin().equals(login)) return Mono.just(Constants.EntityRoles.PRIMARY_OWNER);
                    if (
                        team.getApprover() != null && team.getApprover().stream().anyMatch(user -> user.getLogin().equals(login))
                    ) return Mono.just(Constants.EntityRoles.APPROVER);
                    if (
                        team.getObserver() != null && team.getObserver().stream().anyMatch(user -> user.getLogin().equals(login))
                    ) return Mono.just(Constants.EntityRoles.OBSERVER);
                    if (
                        team.getExternalReviewer() != null &&
                        team.getExternalReviewer().stream().anyMatch(user -> user.getLogin().equals(login))
                    ) return Mono.just(Constants.EntityRoles.EXTERNAL_REVIEWER);
                    if (
                        team.getContractAdmin() != null && team.getContractAdmin().stream().anyMatch(user -> user.getLogin().equals(login))
                    ) return Mono.just(Constants.EntityRoles.CONTRACT_ADMIN);
                    if (
                        team.getSecondaryOwner() != null &&
                        team.getSecondaryOwner().stream().anyMatch(user -> user.getLogin().equals(login))
                    ) return Mono.just(Constants.EntityRoles.SECONDARY_OWNER);
                    return Mono.just("NONE");
                }
            );
    }
}
//    private Mono<Void> addUserTask(TaskType type, String entityType, User targetUser){
//
//    }
