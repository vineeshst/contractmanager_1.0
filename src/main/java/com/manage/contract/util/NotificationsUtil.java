package com.manage.contract.util;

import com.manage.contract.domain.*;
import com.manage.contract.repository.NotificationRepository;
import com.manage.contract.repository.UserRepository;
import com.manage.contract.security.SecurityUtils;
import com.manage.contract.service.dto.Constants;
import com.manage.contract.service.dto.EntityType;
import com.manage.contract.service.dto.IEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NotificationsUtil {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    IEntity entity;

    Team team;

    TeamGroups teamGroups;

    private final Logger log = LoggerFactory.getLogger(NotificationsUtil.class);

    public Mono<Void> createNotification(IEntity entity, String type, String login) {
        log.debug(String.format("Creating notification for EntityId: %s, Type: %s", entity.getEntityId(), type));

        this.entity = entity;
        team = entity.getTeam();
        teamGroups = entity.getTeamGroups();
        List<User> users = new ArrayList<>();

        switch (type) {
            case Constants.NotificationType.CREATED:
            case Constants.NotificationType.MODIFIED:
                users.addAll(getAllUsers(Constants.EntityRoles.SECONDARY_OWNER, Constants.EntityRoles.OBSERVER));
                break;
            case Constants.NotificationType.SENT_FOR_APPROVAL:
                users.addAll(
                    getAllUsers(Constants.EntityRoles.SECONDARY_OWNER, Constants.EntityRoles.OBSERVER, Constants.EntityRoles.APPROVER)
                );
                break;
            case Constants.NotificationType.APPROVED:
            case Constants.NotificationType.REJECTED:
                users.add(team.getPrimaryOwner());
                users.addAll(
                    getAllUsers(Constants.EntityRoles.SECONDARY_OWNER, Constants.EntityRoles.OBSERVER, Constants.EntityRoles.APPROVER)
                );
                break;
            case Constants.NotificationType.SENT_FOR_SIGNATURE:
                users.addAll(
                    getAllUsers(
                        Constants.EntityRoles.SECONDARY_OWNER,
                        Constants.EntityRoles.OBSERVER,
                        Constants.EntityRoles.EXTERNAL_SIGNATORY
                    )
                );
                break;
            case Constants.NotificationType.SIGNED:
                users.add(team.getPrimaryOwner());
                users.addAll(getAllUsers(Constants.EntityRoles.SECONDARY_OWNER, Constants.EntityRoles.OBSERVER));
                break;
            default:
                return Mono.empty();
        }
        return addNotification(type, users, EntityType.AGREEMENT, login);
    }

    public Mono<Void> markEntityNotificationAsRead(String entityId) {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(
                login -> {
                    if (ObjectId.isValid(login)) return userRepository.findOneByEmailIgnoreCase(login);
                    return userRepository.findOneByLogin(login);
                }
            )
            .flatMap(
                user ->
                    notificationRepository
                        .findAllByEntityIdAndUserId(entityId, user.getId())
                        .flatMap(
                            notification -> {
                                notification.setRead(true);
                                return Flux.just(notification);
                            }
                        )
                        .collectList()
                        .flatMap(list -> notificationRepository.saveAll(list).then())
            );
    }

    private Mono<Void> addNotification(String type, List<User> users, EntityType entityType, String login) {
        log.debug(users.size() + " Notifications to be created.");

        List<Notification> notificationList = new ArrayList<>();

        return userRepository
            .findOneByLogin(login)
            .flatMap(
                u -> {
                    log.debug("Retrieved action user from db :" + u.getFirstName() + ' ' + u.getLastName());

                    users.removeIf(user -> Objects.equals(user.getId(), u.getId()));

                    users.forEach(
                        member -> {
                            log.debug("Adding notification for " + member.getFirstName() + " " + member.getLastName());

                            notificationList.add(
                                new Notification(
                                    type,
                                    member.getId(),
                                    u.getFirstName() + ' ' + u.getLastName(),
                                    entity.getEntityName(),
                                    entity.getEntityId(),
                                    entityType
                                )
                            );
                        }
                    );
                    return notificationRepository.saveAll(notificationList).then();
                }
            );
    }

    private List<User> getAllUsers(String... roles) {
        List<User> users = new ArrayList<>();

        for (String role : roles) {
            switch (role) {
                case Constants.EntityRoles.SECONDARY_OWNER:
                    {
                        if (team != null && team.getSecondaryOwner() != null) users.addAll(validateUser(team.getSecondaryOwner()));

                        if (teamGroups != null && teamGroups.getSecondaryOwner() != null) teamGroups
                            .getSecondaryOwner()
                            .forEach(userGroup -> validateUserGroup(userGroup, users));
                        break;
                    }
                case Constants.EntityRoles.APPROVER:
                    {
                        if (team != null && team.getApprover() != null) users.addAll(validateUser(team.getApprover()));

                        if (teamGroups != null && teamGroups.getApprover() != null) teamGroups
                            .getApprover()
                            .forEach(userGroup -> validateUserGroup(userGroup, users));
                        break;
                    }
                case Constants.EntityRoles.EXTERNAL_SIGNATORY:
                    {
                        if (team != null && team.getExternalSignatory() != null) users.addAll(validateUser(team.getExternalSignatory()));

                        if (teamGroups != null && teamGroups.getExternalSignatory() != null) teamGroups
                            .getExternalSignatory()
                            .forEach(userGroup -> validateUserGroup(userGroup, users));
                        break;
                    }
                case Constants.EntityRoles.OBSERVER:
                    {
                        if (team != null && team.getObserver() != null) users.addAll(validateUser(team.getObserver()));

                        if (teamGroups != null && teamGroups.getObserver() != null) teamGroups
                            .getObserver()
                            .forEach(userGroup -> validateUserGroup(userGroup, users));
                        break;
                    }
            }
        }
        return users;
    }

    List<User> validateUser(List<User> userList) {
        return userList.stream().filter(user -> entity.isNotificationEnabled(user.getId())).collect(Collectors.toList());
    }

    void validateUserGroup(UserGroup userGroup, List<User> userList) {
        log.debug(userGroup.getName() + "***********" + entity.isNotificationEnabled(userGroup.getId()));
        if (entity.isNotificationEnabled(userGroup.getId())) userList.addAll(userGroup.getMembers());
    }
}
