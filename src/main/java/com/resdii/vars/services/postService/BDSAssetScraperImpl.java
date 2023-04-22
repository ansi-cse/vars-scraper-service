package com.resdii.vars.services.postService;

import com.resdii.vars.api.PostApiClient;
import com.resdii.vars.helper.LocationHelper;
import com.resdii.vars.helper.LoginHelper;
import com.resdii.vars.mapper.CategoryMapper;
import com.resdii.vars.services.postService.postFactory.PostMapperFactory;
import com.resdii.vars.services.WebBaseScraperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class BDSAssetScraperImpl extends WebBaseScraperImpl {
    protected PostApiClient postApiClient;
    protected LoginHelper loginHelper;
    protected LocationHelper locationHelper;
    protected CategoryMapper categoryMapper;
    protected PostMapperFactory postMapperFactory;
    protected MongoTemplate mongoTemplate;

    @Autowired
    public void setPostClient(PostApiClient postApiClient) {this.postApiClient = postApiClient;}
    @Autowired
    public void setLoginHelper(LoginHelper loginHelper) {this.loginHelper = loginHelper;}
    @Autowired
    public void setLocationHelper(LocationHelper locationHelper) {this.locationHelper = locationHelper;}
    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {this.categoryMapper = categoryMapper;}
    @Autowired
    public void setPostMapperFactory(PostMapperFactory postMapperFactory) {this.postMapperFactory = postMapperFactory;}
    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {this.mongoTemplate = mongoTemplate;}
}
