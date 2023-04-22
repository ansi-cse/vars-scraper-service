package com.resdii.vars.services.postService;

import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.api.GetImageWithoutWaterMaskUrlApiClient;
import com.resdii.vars.helper.ApiKeyHelper;
import com.resdii.vars.services.postService.postFactory.WebSubPageScraperFactory;
import com.resdii.vars.helper.MongoHelper;
import com.resdii.vars.mapper.CategoryMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PostService {
    private ApiKeyHelper apiKeyHelper;
    private WebSubPageScraperFactory webSubPageScraperFactory;

    protected MongoHelper mongoHelper;

    private CategoryMapper categoryMapper;

    private GetImageWithoutWaterMaskUrlApiClient getImageWithoutWaterMaskUrlApiClient;


    @Value("${url-for-scraper.url}")
    private String[] listOfUrl;
    private int numOfThread=0;

    public void runGetLinks(String baseUrl) {
        try {
            GlobalConstant.commandMapToPostType.forEach((commandKey, name)->{
                if(!commandKey.equals(2)){
                    webSubPageScraperFactory.getSubPageScraper(baseUrl).getLinksByPostType(commandKey, baseUrl, GlobalConstant.baseUrlToPrefix.get(baseUrl));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void runGetLinksFailed(String baseUrl) {
        try {
            GlobalConstant.commandMapToPostType.forEach((commandKey, name)->{
                if(!commandKey.equals(2)){
                    webSubPageScraperFactory.getSubPageScraper(baseUrl).getLinksByPostTypeFailed(commandKey, baseUrl, GlobalConstant.baseUrlToPrefix.get(baseUrl));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void runGetDetail(int numOfItems, int numOfThreadPerKey, String baseUrl) {
        int lengthOfListKeys=apiKeyHelper.getApi_keys().length;
        try {
            if(numOfThreadPerKey==0){
                numOfThread=lengthOfListKeys*5;
            }else{
                numOfThread=lengthOfListKeys*numOfThreadPerKey;
            }
            if(numOfItems==0){
                numOfItems=lengthOfListKeys*5000;
            }
            BDSWebSubPageScraperImpl bdsWebSubPageScraper=webSubPageScraperFactory.getSubPageScraper(baseUrl);
            bdsWebSubPageScraper.getDetailPage(numOfItems, numOfThread, GlobalConstant.baseUrlToPrefix.get(baseUrl));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void runFailedCase(int numOfItems, int numOfThreadPerKey, String baseUrl){
        try{
            if(numOfThreadPerKey==0){
                numOfThread=apiKeyHelper.getApi_keys().length*5;
            }else{
                numOfThread=apiKeyHelper.getApi_keys().length*numOfThreadPerKey;
            }
            if(numOfItems==0){
                numOfItems=apiKeyHelper.getApi_keys().length*5000;
            }
            webSubPageScraperFactory.getSubPageScraper(baseUrl).runFailedCase(numOfItems, numOfThread);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void runTryCase(int numOfItems, int numOfThreadPerKey, String baseUrl){
        try{
            if(numOfThreadPerKey==0){
                numOfThread=apiKeyHelper.getApi_keys().length*5;
            }else{
                numOfThread=apiKeyHelper.getApi_keys().length*numOfThreadPerKey;
            }
            if(numOfItems==0){
                numOfItems=apiKeyHelper.getApi_keys().length*5000;
            }
            webSubPageScraperFactory.getSubPageScraper(baseUrl).runTryCase(numOfItems, numOfThread);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updatePost(){
        List<String> listTable=new ArrayList<>();
//        listTable.add("post");
//        listTable.add("post-23-3-2023");
        listTable.add("post-2023-03-23");
//        listTable.add("post-2023-03-24");
//        listTable.add("post-2023-03-31");
//        listTable.add("post-2023-04-01");
//        listTable.add("post-2023-04-02");
//        listTable.add("post-2023-04-03");
//        listTable.add("post-2023-04-04");
//        listTable.add("post-2023-04-05");
//        listTable.add("post-2023-04-06");
//        listTable.add("post-2023-04-07");
//        listTable.add("post-2023-04-10");

        List<String> MAExclude=new ArrayList<>();
        MAExclude.add("Shop, kiot, quán");
        MAExclude.add("Đất thổ cư, đất ở");
        MAExclude.add("Phòng trọ, nhà trọ");
        MAExclude.add("Nhà mặt tiền");
        MAExclude.add("Nhà trong hẻm");
        ExecutorService executor = Executors.newFixedThreadPool(20);
        listTable.forEach(tableName->{
            long numOfPage=mongoHelper.countPage(tableName, 20);
            for (int i = 0; i < numOfPage; i++) {
                int pageIndex=i;
                executor.submit(()->{
                    Page<PostDocument> pageOfPost= mongoHelper.readCollectionWithPagination(PostDocument.class, tableName,pageIndex,20);
                    List<PostDocument> list=pageOfPost.getContent();
                    list.forEach(ele->{
                        try{
                            String title=ele.getTitle().toLowerCase();
                            if((title.contains("sang nhượng") || title.contains("nhượng quyền") || title.contains("chuyển nhượng"))&&!MAExclude.contains(ele.getRawRealEstate())){
                                Double price=ele.getPrice();
                                if(!Objects.isNull(price) && price>=10000000000.0){
                                    ele.setTypeRealEstate(categoryMapper.mapRealEstateType("M&A-"+ele.getRawRealEstate()));
                                }
                            }else{
                                ele.setTypeRealEstate(categoryMapper.mapRealEstateType(ele.getPostType().getCode()+"-"+ele.getRawRealEstate()));
                            }
                            mongoHelper.save(ele, tableName+"-v1");
                        }catch (Exception exception){
                            System.out.println(String.format("%s: %s", ele, exception.getMessage()));
                        }
                    });
                });
            }
        });
    }

    @SneakyThrows
    public void runTestCase(String url, String baseUrl){
        webSubPageScraperFactory.getSubPageScraper(baseUrl).runTestCase(url, baseUrl);
    }

    @Autowired
    public void setApiKeyHelper(ApiKeyHelper apiKeyHelper) {this.apiKeyHelper = apiKeyHelper;}

    @Autowired
    public void setWebSubPageScraperFactory(WebSubPageScraperFactory webSubPageScraperFactory) {this.webSubPageScraperFactory = webSubPageScraperFactory;}

    @Autowired
    public void setMongoHelper(MongoHelper mongoHelper) {
        this.mongoHelper = mongoHelper;
    }

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Autowired
    public void setGetImageWithoutWaterMaskUrl(GetImageWithoutWaterMaskUrlApiClient getImageWithoutWaterMaskUrlApiClient) {this.getImageWithoutWaterMaskUrlApiClient = getImageWithoutWaterMaskUrlApiClient;}
}
