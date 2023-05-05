package com.resdii.vars.services;

import com.resdii.vars.enums.PostStatus;
import org.jsoup.select.Elements;

public interface WebScraper<T>{
    PostStatus scrape(String url, String postType,String prefix, String api_key, T post);
    T extractData(String url , Elements docElements, String postType, T post);
    void saveDataToDB(T data, String tableName);
}
