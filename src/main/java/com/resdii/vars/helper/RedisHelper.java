package com.resdii.vars.helper;

import com.resdii.vars.enums.PostStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisHelper {
    public RedisTemplate template;

    public void bdsDetailPageRedisHandle(Integer command, String url, String hashValue, PostStatus postStatus, boolean isHandleFailedCase){
        switch (postStatus){
            case SUCCESS:
                template.opsForValue().set("SCRAPER:DETAIL:SUCCESS:"+command+":"+hashValue, command+"_"+url);
                template.delete("SCRAPER:DETAIL:PROCESSING:"+command+":"+hashValue);
                break;
            case FAILED:
                if(isHandleFailedCase){
                    template.opsForValue().set("SCRAPER:DETAIL:TRY:"+command+":"+hashValue, command+"_"+url);
                }else{
                    template.opsForValue().set("SCRAPER:DETAIL:FAILED:"+command+":"+hashValue, command+"_"+url);
                }
                template.delete("SCRAPER:DETAIL:PROCESSING:"+command+":"+hashValue);
                break;
            case NOT_EXIST:
                template.opsForValue().set("SCRAPER:DETAIL:NOT_EXIST:"+command+":"+hashValue, command+"_"+url);
                template.delete("SCRAPER:DETAIL:PROCESSING:"+command+":"+hashValue);
                break;
        }
    }
    public void bdsDetailPageRedisHandleWithPrefix(Integer command, String url,String prefix, String hashValue, PostStatus postStatus, boolean isHandleFailedCase){
        switch (postStatus){
            case SUCCESS:
                template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+command+":"+hashValue, command+"_"+url);
                template.delete("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue);
                break;
            case FAILED:
                if(isHandleFailedCase){
                    template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:TRY:"+command+":"+hashValue, command+"_"+url);
                }else{
                    template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:FAILED:"+command+":"+hashValue, command+"_"+url);
                }
                template.delete("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue);
                break;
            case NOT_EXIST:
                template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:NOT_EXIST:"+command+":"+hashValue, command+"_"+url);
                template.delete("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue);
                break;
        }
    }

    @Autowired
    public void setTemplate(RedisTemplate template) {
        this.template = template;
    }
}
