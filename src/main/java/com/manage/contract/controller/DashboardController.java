package com.manage.contract.controller;

import com.manage.contract.domain.Task;
import com.manage.contract.domain.UserTasks;
import com.manage.contract.repository.UserRepository;
import com.manage.contract.repository.UserTasksRepository;
import com.manage.contract.security.SecurityUtils;
import com.manage.contract.service.ContractService;
import com.manage.contract.service.DashboardService;
import com.manage.contract.service.UserService;
import com.manage.contract.service.dto.Dashboard;
import com.manage.contract.service.dto.NotificationsDTO;
import com.manage.contract.service.dto.ResponseMessage;
import com.manage.contract.service.dto.TaskType;
import java.util.Collections;
import java.util.Comparator;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "api/dashboard")
public class DashboardController {

    private final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final UserTasksRepository userTasksRepository;

    private final UserRepository userRepository;

    private final ContractService contractService;

    private final DashboardService dashboardService;

    public DashboardController(
        UserTasksRepository userTasksRepository,
        UserRepository userRepository,
        ContractService contractService,
        DashboardService dashboardService
    ) {
        this.userTasksRepository = userTasksRepository;
        this.userRepository = userRepository;
        this.contractService = contractService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/my-tasks")
    public Flux<Task> getMyTasks() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(
                login -> {
                    if (ObjectId.isValid(login)) return userRepository.findOneByEmailIgnoreCase(login);
                    return userRepository.findOneByLogin(login);
                }
            )
            .flatMap(
                user -> {
                    if (!ObjectId.isValid(user.getId())) return userTasksRepository.findByUserLogin(user.getId());
                    return userTasksRepository.findByUserId(user.getId());
                }
            )
            .flatMapIterable(UserTasks::getTasks)
            .filter(x -> !x.getStatus());
    }

    @GetMapping("/count")
    public Mono<Dashboard> getCount() {
        Mono<Long> agreementsCountMono = contractService.getAllAgreements().count();
        Mono<Long> tasksCountMono = getMyTasks().count();
        Mono<Long> agreementsPendingApproval = getMyTasks().filter(task -> task.getType() == TaskType.CONTRACT_AGREEMENT_APPROVAL).count();
        var dashboard = new Dashboard();
        return Mono
            .zip(agreementsCountMono, tasksCountMono, agreementsPendingApproval)
            .doOnSuccess(
                tuple -> {
                    dashboard.setAgreementsCount(tuple.getT1());
                    dashboard.setTaskCount(tuple.getT2());
                    dashboard.setAgreementsPendingApprovalCount(tuple.getT3());
                }
            )
            .then(Mono.just(dashboard));
    }

    @GetMapping("/notifications")
    public Flux<NotificationsDTO> getNotifications() {
        return dashboardService.getNotifications().sort(Comparator.comparing(NotificationsDTO::getDate, Collections.reverseOrder()));
    }

    @PutMapping("/markRead/{id}")
    public Mono<ResponseEntity<String>> markNotificationAsRead(@PathVariable("id") String id) {
        if (id == null || id.length() == 0) return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Notification Id is null"));
        try {
            return dashboardService
                .markNotificationAsRead(id)
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Notification is marked read"));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL SERVER ERROR"));
        }
    }

    @PutMapping("/notifications/clear")
    public Mono<ResponseEntity<ResponseMessage>> clearNotifications() {
        try {
            return dashboardService
                .clearNotifications()
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Notification is cleared")));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("INTERNAL SERVER ERROR")));
        }
    }
}
