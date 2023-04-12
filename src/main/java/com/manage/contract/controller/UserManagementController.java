package com.manage.contract.controller;

import com.manage.contract.domain.MetaPrivileges;
import com.manage.contract.domain.SecurityGroup;
import com.manage.contract.domain.UserGroup;
import com.manage.contract.domain.UserRole;
import com.manage.contract.service.ContractService;
import com.manage.contract.service.dto.ResponseMessage;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/user-management")
public class UserManagementController {

    private ContractService contractService;

    UserManagementController(ContractService contractService) {
        this.contractService = contractService;
    }

    //region Security Group
    @PostMapping("/security-group/create")
    public Mono<ResponseEntity<SecurityGroup>> createSecurityGroup(@RequestBody SecurityGroup securityGroup) {
        return contractService
            .createSecurityGroup(securityGroup)
            .flatMap(sg -> Mono.just(new ResponseEntity<>(sg, HttpStatus.OK)))
            .onErrorReturn(new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED));
    }

    @PutMapping("/security-group/update")
    public Mono<ResponseEntity<ResponseMessage>> updateSecurityGroup(@RequestBody SecurityGroup securityGroup) {
        return contractService
            .updateSecurityGroup(securityGroup)
            .flatMap(sg -> Mono.just(new ResponseEntity<>(new ResponseMessage(sg.getName() + " is updated successfully!"), HttpStatus.OK)))
            .onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not Update Security Group"), HttpStatus.EXPECTATION_FAILED));
    }

    @GetMapping("/security-group/get-security-groups")
    public ResponseEntity<Flux<SecurityGroup>> getSecurityGroups() {
        return ResponseEntity.status(HttpStatus.OK).body(contractService.getSecurityGroups());
    }

    @GetMapping("/security-group/get-security-group/{securityGroupId}")
    public ResponseEntity<Mono<SecurityGroup>> getSecurityGroup(@PathVariable("securityGroupId") String securityGroupId) {
        return ResponseEntity.status(HttpStatus.OK).body(contractService.getSecurityGroups(securityGroupId));
    }

    @GetMapping("/security-group/get-meta-privileges")
    public ResponseEntity<Mono<MetaPrivileges>> getMetaPrivileges() {
        return ResponseEntity.status(HttpStatus.OK).body(contractService.getMetaPrivileges());
    }

    //endregion

    //region User Groups
    @GetMapping("/user-group/get-meta-roles")
    public ResponseEntity<Flux<UserRole>> getMetaUserRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(contractService.getMetaUserRoles());
    }

    @PutMapping("/user-group/update-meta-roles")
    public Mono<ResponseEntity<ResponseMessage>> updateMetaUserRole(@RequestBody UserRole userRole) {
        return contractService
            .updateMetaUserRole(userRole)
            .flatMap(
                userRole1 ->
                    Mono.just(
                        new ResponseEntity<>(new ResponseMessage(userRole1.getDisplayName() + " is updated successfully!"), HttpStatus.OK)
                    )
            )
            .onErrorReturn(new ResponseEntity<>(new ResponseMessage("Could not update User Role"), HttpStatus.EXPECTATION_FAILED));
    }

    @PostMapping("/user-group/create")
    public Mono<ResponseEntity<UserGroup>> createUserGroup(@RequestBody UserGroup userGroup) {
        return contractService
            .createUserGroup(userGroup)
            .flatMap(userGroup1 -> Mono.just(new ResponseEntity<>(userGroup1, HttpStatus.OK)))
            .onErrorReturn(new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED));
    }

    @GetMapping("/user-group/get-user-group/{userGroupId}")
    public ResponseEntity<Mono<UserGroup>> getUserGroup(@PathVariable("userGroupId") String userGroupId) {
        return ResponseEntity.status(HttpStatus.OK).body(contractService.getUserGroup(userGroupId));
    }

    @PutMapping("/user-group/update-user-group/{userGroupId}")
    public Mono<ResponseEntity<ResponseMessage>> updateUserGroup(
        @PathVariable("userGroupId") String userGroupId,
        @RequestBody UserGroup userGroup
    ) {
        if (userGroup == null || userGroup.getId() == null || !userGroup.getId().equalsIgnoreCase(userGroupId)) return Mono.just(
            new ResponseEntity<>(new ResponseMessage("User Group update failed!"), HttpStatus.EXPECTATION_FAILED)
        );

        return contractService
            .updateUserGroup(userGroupId, userGroup)
            .flatMap(
                userGroup1 ->
                    Mono.just(new ResponseEntity<>(new ResponseMessage(userGroup1.getName() + " is updated successfully!"), HttpStatus.OK))
            );
    }

    @GetMapping("/user-group/get-user-groups")
    public ResponseEntity<Flux<UserGroup>> getUserGroups() {
        return ResponseEntity.status(HttpStatus.OK).body(contractService.getUserGroups());
    }
    //endregion
}
