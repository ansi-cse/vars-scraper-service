package com.resdii.vars.services.postService;

import com.resdii.ms.common.category.NoodevCategory;
import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.enums.PostType;
import com.resdii.vars.helper.ApiKeyHelper;
import com.resdii.vars.services.postService.postFactory.WebSubPageScraperFactory;
import com.resdii.vars.helper.MongoHelper;
import com.resdii.vars.mapper.CategoryMapper;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperByMeImpl;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PostService {
    private ApiKeyHelper apiKeyHelper;
    private WebSubPageScraperFactory webSubPageScraperFactory;
    private MongoHelper mongoHelper;
    private CategoryMapper categoryMapper;
    private int numOfThread=0;


    public void runGetLinks(String baseUrl, String postType, int numOfPage) {
        try {
            BDSWebSubPageScraperImpl bdsWebSubPageScraper=webSubPageScraperFactory.getSubPageScraper(baseUrl);
            bdsWebSubPageScraper.getLinks(postType, baseUrl, numOfPage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void runGetDetail(String baseUrl, String postType, int numOfItems, int numOfThreadPerKey) {
        int lengthOfListKeys=apiKeyHelper.getApiKeys().length;
        try {
            numOfThread=(numOfThreadPerKey==-1)? lengthOfListKeys*5:  lengthOfListKeys*numOfThreadPerKey;
            if(numOfItems==-1){
                numOfItems=lengthOfListKeys*5000;
            }
            BDSWebSubPageScraperImpl bdsWebSubPageScraper=webSubPageScraperFactory.getSubPageScraper(baseUrl);
            bdsWebSubPageScraper.getDetailPage(GlobalConstant.baseUrlToPrefix.get(baseUrl), postType, numOfItems, numOfThread );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Not use
    public void runGetLinksFailed(String baseUrl) {
//        try {
//            GlobalConstant.commandMapToPostType.forEach((commandKey, name)->{
//                if(!commandKey.equals(2)){
//                    webSubPageScraperFactory.getSubPageScraper(baseUrl).getLinksByPostTypeFailed(commandKey, baseUrl, GlobalConstant.baseUrlToPrefix.get(baseUrl));
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public void runFailedCase(int numOfItems, int numOfThreadPerKey, String baseUrl){
//        try{
//            if(numOfThreadPerKey==0){
//                numOfThread=apiKeyHelper.getApiKeys().length*5;
//            }else{
//                numOfThread=apiKeyHelper.getApiKeys().length*numOfThreadPerKey;
//            }
//            if(numOfItems==0){
//                numOfItems=apiKeyHelper.getApiKeys().length*5000;
//            }
//            webSubPageScraperFactory.getSubPageScraper(baseUrl).runFailedCase(numOfItems, numOfThread);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public void runTryCase(int numOfItems, int numOfThreadPerKey, String baseUrl){
//        try{
//            if(numOfThreadPerKey==0){
//                numOfThread=apiKeyHelper.getApiKeys().length*5;
//            }else{
//                numOfThread=apiKeyHelper.getApiKeys().length*numOfThreadPerKey;
//            }
//            if(numOfItems==0){
//                numOfItems=apiKeyHelper.getApiKeys().length*5000;
//            }
//            webSubPageScraperFactory.getSubPageScraper(baseUrl).runTryCase(numOfItems, numOfThread);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public void postMAFilter(){
        List<String> listTable=new ArrayList<>();
        listTable.add("post-2023-04-07-v1");
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
                            if(ele.getTypeRealEstate().getName().contains("M&A")){
                                ele.setPostType(NoodevCategory.getInstance().getPostType().get("m-a"));
                                mongoHelper.save(ele, tableName+"-MA");
                            }
                        }catch (Exception exception){
                            System.out.println(String.format("%s: %s", ele, exception.getMessage()));
                        }
                    });
                });
            }
        });
    }

    public void updatePost(){
        List<String> listTable=new ArrayList<>();
//        listTable.add("post");
//        listTable.add("post-23-3-2023");
//        listTable.add("post-2023-03-23");
        listTable.add("post-2023-03-31-v1");
//        listTable.add("post-2023-03-31");
//        listTable.add("post-2023-04-01");
//        listTable.add("post-2023-04-02");
//        listTable.add("post-2023-04-03");
//        listTable.add("post-2023-04-04");
//        listTable.add("post-2023-04-05");
//        listTable.add("post-2023-04-06");
//        listTable.add("post-2023-04-07");
//        listTable.add("post-2023-04-10");
//        List<String> MAExclude=new ArrayList<>();
//        MAExclude.add("Shop, kiot, quán");
//        MAExclude.add("Đất thổ cư, đất ở");
//        MAExclude.add("Phòng trọ, nhà trọ");
//        MAExclude.add("Nhà mặt tiền");
//        MAExclude.add("Nhà trong hẻm");
        int numOfThread=20;
        int pageSize=20;
        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        listTable.forEach(tableName->{
            for (int i = 0; i < numOfThread; i++) {
                int pageIndex=i;
                executor.submit(()->{
                    int index=0;
                    int page;
                    while (true){
                        page=pageIndex+ index*numOfThread;
                        System.out.println(page);
                        Page<PostDocument> pageOfPost= mongoHelper.readCollectionWithPagination(PostDocument.class, tableName, page, pageSize);
                        if(pageOfPost.isEmpty()){
                            break;
                        }
                        List<PostDocument> list=pageOfPost.getContent();
                        list.forEach(ele->{
                            try{
//                            String title=ele.getTitle().toLowerCase();
//                            if((title.contains("sang nhượng") || title.contains("nhượng quyền") || title.contains("chuyển nhượng"))&&!MAExclude.contains(ele.getRawRealEstate())){
//                                Double price=ele.getPrice();
//                                if(!Objects.isNull(price) && price>=10000000000.0){
//                                    ele.setTypeRealEstate(categoryMapper.mapRealEstateType("M&A-"+ele.getRawRealEstate()));
//                                }
//                            }else{
//                                ele.setTypeRealEstate(categoryMapper.mapRealEstateType(ele.getPostType().getCode()+"-"+ele.getRawRealEstate()));
//                            }
                                System.out.println(ele.getTitle());
                                mongoHelper.save(ele, tableName);
                            }catch (Exception exception){
                                System.out.println(String.format("%s: %s", ele, exception.getMessage()));
                            }
                        });
                        index=index+1;
                    }
                });
            }
        });
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
}
