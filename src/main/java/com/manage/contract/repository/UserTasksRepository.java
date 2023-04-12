package com.manage.contract.repository;

import com.manage.contract.domain.Task;
import com.manage.contract.domain.UserTasks;
import com.manage.contract.service.dto.TaskType;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserTasksRepository extends ReactiveMongoRepository<UserTasks, String> {
    @Query("{'user.$id': ObjectId('?0')}")
    Mono<UserTasks> findByUserId(String userId);

    @Query("{'user.$id': '?0'}")
    Mono<UserTasks> findByUserLogin(String login);

    @Query("{'tasks.entityId': '?0', 'tasks.type': '?1'}")
    Flux<UserTasks> findTasksByEntityIdAndType(String entityId, TaskType taskType);
}
