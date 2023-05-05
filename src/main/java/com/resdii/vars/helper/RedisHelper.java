package com.resdii.vars.helper;

import com.resdii.vars.enums.PostStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisHelper {
    public RedisTemplate template;

    public void bdsDetailPageRedisHandle(Integer command, String url, String hashValue, PostStatus postStatus){
        switch (postStatus){
            case SUCCESS:
                template.opsForValue().set("SCRAPER:DETAIL:SUCCESS:"+command+":"+hashValue, command+"_"+url);
                break;
            case FAILED:
                template.opsForValue().set("SCRAPER:DETAIL:FAILED:"+command+":"+hashValue, command+"_"+url);
                break;
            case NOT_EXIST:
                template.opsForValue().set("SCRAPER:DETAIL:NOT_EXIST:"+command+":"+hashValue, command+"_"+url);
                break;
        }
        template.delete("SCRAPER:DETAIL:PROCESSING:"+command+":"+hashValue);

    }
    public void bdsDetailPageRedisHandleWithPrefix(String postType, String url,String prefix, String hashValue, PostStatus postStatus){
        template.delete("SCRAPER:"+postType+":"+prefix+":DETAIL:PROCESSING:"+hashValue);
        switch (postStatus){
            case SUCCESS:
                template.opsForValue().set("SCRAPER:"+postType+":"+prefix+":DETAIL:SUCCESS:"+hashValue, url);
                break;
            case FAILED:
                template.opsForValue().set("SCRAPER:"+postType+":"+prefix+":DETAIL:FAILED:"+hashValue, url);
                break;
            case NOT_EXIST:
                template.opsForValue().set("SCRAPER:"+postType+":"+prefix+":DETAIL:NOT_EXIST:"+hashValue, url);
                break;
        }
    }

    @Autowired
    public void setTemplate(RedisTemplate template) {
        this.template = template;
    }
}
