package com.resdii.vars.services.postService.postFactory;

import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.mapper.PostDocumentMapper;
import com.resdii.vars.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class PostMapperFactory {
    PostDocumentMapper postDocumentMapper;

    public <T extends PostDocument> PostMapper<T> getPostMapper(Class<?> c) {
        PostMapper postMapper=null;
        if (c == PostDocument.class) {
            postMapper=postDocumentMapper;
        }
        return postMapper;
    }

    @Autowired
    public void setPostDocumentMapper(PostDocumentMapper postDocumentMapper) {
        this.postDocumentMapper = postDocumentMapper;
    }
}
