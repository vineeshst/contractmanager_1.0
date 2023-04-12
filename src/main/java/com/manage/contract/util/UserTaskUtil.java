package com.manage.contract.util;

import com.manage.contract.domain.*;
import com.manage.contract.repository.UserRepository;
import com.manage.contract.repository.UserTasksRepository;
import com.manage.contract.security.SecurityUtils;
import com.manage.contract.service.ContractService;
import com.manage.contract.service.MailService;
import com.manage.contract.service.dto.ApprovalCriteria;
import com.manage.contract.service.dto.ContractTypeStatus;
import com.manage.contract.service.dto.IEntity;
import com.manage.contract.service.dto.TaskType;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class UserTaskUtil {

    @Autowired
    private UserTasksRepository userTasksRepository;

    @Autowired
    private ContractService contractService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    private final Logger log = LoggerFactory.getLogger(UserTaskUtil.class);

    public Mono<Void> sendEntityForApproval(IEntity entity, String message) {
        TaskType taskType = getTaskType(entity);

        if (entity.getApprover() == null || entity.getApprover().size() == 0) {
            log.debug(String.format("No approver found for the entity %s. Approval request for %s", entity.getEntityName(), taskType));
            return Mono.empty();
        }

        for (User user : entity.getApprover()) {
            UserTasks ut = new UserTasks();
            ut.setUser(user);
            ut.getTasks().add(new Task(taskType, entity.getEntityId(), entity.getEntityName(), message));
            addUserTask(ut).subscribe();
            mailService.sendEmail(
                user.getEmail(),
                "New " + taskType + " Review",
                "Please review the " + taskType + " " + entity.getEntityName(),
                true,
                false
            );
        }
        return Mono.empty();
    }

    public Mono<UserTasks> addUserTask(UserTasks userTasks) {
        return userTasksRepository
            .findByUserId(userTasks.getUser().getId())
            .flatMap(
                userTasks1 -> {
                    userTasks1.getTasks().add(userTasks.getTasks().get(0));
                    log.debug("Adding task to user.");
                    return userTasksRepository.save(userTasks1);
                }
            )
            .switchIfEmpty(userTasksRepository.insert(userTasks))
            .subscribeOn(Schedulers.parallel());
    }

    public Mono<EntityStatus> approve(IEntity entity) {
        return getUserId()
            .flatMap(
                user -> {
                    var approvalStatus = entity.getApprovalStatus();
                    approvalStatus.addApprovalStatus(new UserApprovalStatus(user.getId(), true));
                    return contractService
                        .updateEntity(entity)
                        .flatMap(ct -> removeAllTask(ct.getEntityId(), getTaskType(entity)))
                        .thenReturn(setContractTypeStatus(entity))
                        .doOnError(err -> log.error("Error: " + err));
                }
            );
    }

    public Mono<EntityStatus> reject(IEntity entity) {
        return getUserId()
            .flatMap(
                user -> {
                    entity.getApprovalStatus().getUserApprovalStatus().clear();
                    return contractService
                        .updateEntity(entity)
                        .flatMap(ct -> removeAllTask(ct.getEntityId(), getTaskType(entity)))
                        .thenReturn(EntityStatus.REJECTED)
                        .doOnError(err -> log.error("Error: " + err));
                }
            );
    }

    public Mono<Void> removeAllTask(String entityId, TaskType taskType) {
        log.debug(String.format("Enter removeAllTask for EntityId: %s, TaskType: %s", entityId, taskType));
        return userTasksRepository
            .findTasksByEntityIdAndType(entityId, taskType)
            .flatMap(
                userTasks -> {
                    userTasks
                        .getTasks()
                        .stream()
                        .filter(t -> Objects.equals(t.getEntityId(), entityId) && t.getType() == taskType)
                        .forEach(task -> task.setStatus(true));
                    return userTasksRepository.save(userTasks);
                }
            )
            .then();
    }

    private Mono<Void> removeOneTask(String login, String entityId, TaskType taskType) {
        return userTasksRepository
            .findByUserId(login)
            .flatMap(
                userTasks -> {
                    var task = userTasks
                        .getTasks()
                        .stream()
                        .filter(x -> x.getType() == taskType && x.getEntityId().equals(entityId))
                        .findFirst();
                    task.ifPresent(value -> value.setStatus(true));
                    return userTasksRepository.save(userTasks);
                }
            )
            .doOnError(err -> log.debug("Error in remove task " + err))
            .then();
    }

    private Mono<String> getUser() {
        return SecurityUtils.getCurrentUserLogin();
    }

    private Mono<User> getUserId() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(
                user -> {
                    log.debug(user);
                    return userRepository.findOneByLogin(user);
                }
            );
    }

    private EntityStatus setContractTypeStatus(IEntity entity) {
        var approvalStatus = entity.getApprovalStatus();

        if (approvalStatus.getApprovalCriteria() == ApprovalCriteria.ALL) {
            for (User user : entity.getApprover()) {
                if (
                    approvalStatus.getUserApprovalStatus().stream().noneMatch(y -> y.getUserId().equals(user.getId()) && y.isHasApproved())
                ) return EntityStatus.APPROVAL_PENDING;
            }
            return EntityStatus.APPROVED;
        } else if (approvalStatus.getApprovalCriteria() == ApprovalCriteria.ANY_ONE) {
            for (User user : entity.getApprover()) {
                if (
                    approvalStatus.getUserApprovalStatus().stream().anyMatch(y -> y.getUserId().equals(user.getId()) && y.isHasApproved())
                ) return EntityStatus.APPROVED;
            }
        }
        return EntityStatus.APPROVAL_PENDING;
    }

    private TaskType getTaskType(IEntity entity) {
        if (entity instanceof ContractType) {
            return TaskType.CONTRACT_TYPE_APPROVAL;
        } else if (entity instanceof ContractAgreement) {
            return TaskType.CONTRACT_AGREEMENT_APPROVAL;
        } else if (entity instanceof ContractClause) {
            return TaskType.CLAUSE_APPROVAL;
        } else if (entity instanceof ContractTemplate) {
            return TaskType.TEMPLATE_APPROVAL;
        }
        return null;
    }
}
