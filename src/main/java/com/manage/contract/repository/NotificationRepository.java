package com.manage.contract.repository;

import com.manage.contract.domain.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
    Flux<Notification> findAllByUserId(String userId);

    Mono<Notification> findByIdAndUserId(String id, String userId);

    Flux<Notification> findAllByEntityIdAndUserId(String entityId, String userId);
}
