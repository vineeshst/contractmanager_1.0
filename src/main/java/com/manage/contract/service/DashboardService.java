package com.manage.contract.service;

import com.manage.contract.domain.Notification;
import com.manage.contract.repository.NotificationRepository;
import com.manage.contract.repository.UserRepository;
import com.manage.contract.security.SecurityUtils;
import com.manage.contract.service.dto.NotificationsDTO;
import com.manage.contract.service.mapper.NotificationMapper;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DashboardService {

    UserRepository userRepository;

    NotificationRepository notificationRepository;

    DashboardService(UserRepository userRepository, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public Flux<NotificationsDTO> getNotifications() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(
                login -> {
                    if (ObjectId.isValid(login)) return userRepository.findOneByEmailIgnoreCase(login);
                    return userRepository.findOneByLogin(login);
                }
            )
            .flatMapMany(
                user ->
                    Flux.from(notificationRepository.findAllByUserId(user.getId())).map(NotificationMapper::notificationsToNotificationsDTO)
            );
    }

    public Mono<Void> markNotificationAsRead(String id) {
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
                        .findByIdAndUserId(id, user.getId())
                        .flatMap(
                            notification -> {
                                notification.setRead(true);
                                return notificationRepository.save(notification).then();
                            }
                        )
            );
    }

    public Mono<Void> clearNotifications() {
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
                    List<Notification> notificationList = new ArrayList<>();
                    return notificationRepository
                        .findAllByUserId(user.getId())
                        .collectList()
                        .flatMap(notifications -> notificationRepository.deleteAll(notifications).then());
                }
            );
    }
}
