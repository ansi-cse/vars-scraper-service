package com.resdii.vars.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ANSI.
 */
@Component
public class MongoHelper {

    private MongoTemplate mongoTemplate;

    private MongoOperations mongoOperations;

    public <T> Page<T> readCollectionWithPagination(Class<T> className, String collectionName, int pageNumber, int pageSize){
        Query query = new Query();
        query.with(PageRequest.of(pageNumber, pageSize));
        long count = mongoOperations.count(query, className, collectionName);
        List<T> list = mongoOperations.find(query, className, collectionName);
        return new PageImpl<>(list, PageRequest.of(pageNumber, pageSize), count);
    }

    public <T> T save(T ele, String tableName){
        return mongoTemplate.save(ele, tableName);
    }

    public long count(String collectionName){
        Query query = new Query();
        return mongoTemplate.count(query, collectionName);
    }

    public int countPage(String collectionName, int pageSize){
        long numOfRecord=count(collectionName);
        return (int) Math.ceil(numOfRecord/Double.valueOf(pageSize));
    }

    @Autowired
    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
