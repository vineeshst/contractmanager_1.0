package com.manage.contract.repository;

import com.manage.contract.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CustomContractTypeRepository {

    @Autowired
    MongoTemplate mongoTemplate;
    //    public Flux<Task> getTaskByEntity(String entity){
    //        final Query query = new Query();
    //        //AggregationOperation filter = Aggregation.newAggregation(Aggregation.project("tasks").and(    ))
    //    }
}
