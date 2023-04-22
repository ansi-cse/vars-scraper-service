package com.resdii.vars.services;

import com.resdii.vars.enums.PostStatus;
import org.jsoup.select.Elements;

public interface WebScraper<T>{
    PostStatus scrape(String url, Integer command, String api_key, T post);
    T extractData(String url , Elements docElements, Integer command, T post);
    void saveDataToDB(T data, String tableName);
}
