package com.resdii.vars.helper;

import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.services.scraperWebservice.scraperServiceFactory.ScraperServiceFactory;
import com.resdii.vars.services.WebBaseScraperImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperApiImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperByMeImpl;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LinksHelper extends WebBaseScraperImpl {
    private ApiKeyHelper apiKeyHelper;

    private ScraperServiceFactory scraperServiceFactory;

    public String getLinksPageIndex(String baseUrl, String postType, Integer index){
        switch (baseUrl){
            case "https://alonhadat.com.vn/":
                baseUrl= baseUrl+GlobalConstant.commandMapToLinkStringPrefix.get(baseUrl+GlobalConstant.commandMapToPostType.get(postType))+"/trang--"+index+".html";
                break;
            case "https://batdongsan.com.vn/":
                baseUrl=baseUrl.concat(GlobalConstant.commandMapToLinkStringPrefix.get(baseUrl+GlobalConstant.commandMapToPostType.get(postType))).concat("/p"+index);
                break;
//            case "https://muaban.net/":
//                baseUrl=baseUrl.concat(GlobalConstant.commandMapToLinkStringPrefix.get(baseUrl+GlobalConstant.commandMapToPostType.get(command))).concat("/p"+index);
//                break;
//            case "https://www.nhatot.com/":
//                baseUrl=baseUrl.concat(GlobalConstant.commandMapToLinkStringPrefix.get(baseUrl+GlobalConstant.commandMapToPostType.get(command))).concat("?page="+index);
//                break;
        }
        return baseUrl;
    };

    public int getMaxPageIndex(String baseUrl, String postType){
        int maxPageIndex=0;
        switch (baseUrl){
            case "https://alonhadat.com.vn/":
                setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceScraperApiImpl.class));
                String urlWithMaxPageIndex= getLinksPageIndex(baseUrl, postType, 1000000000);
                String aloHtml= loadPage(urlWithMaxPageIndex,  apiKeyHelper.getApiKeyForCheckPaging()).html();
                Elements elements=Jsoup.parse(aloHtml).select(".page").select("a");
                String href=elements.last().attr("href");
                String hrefParse=href.replace(".html","");
                String[] temp=hrefParse.split("-");
                maxPageIndex= Integer.parseInt(temp[temp.length-1]);
                break;
            case "https://batdongsan.com.vn/":
                setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceScraperByMeImpl.class));
                String urlWithPageIndex= getLinksPageIndex(baseUrl,postType, 1);
                String bdsHtml= loadPage(urlWithPageIndex,  apiKeyHelper.getApiKeyForCheckPaging()).html();
                Elements bdsElements=Jsoup.parse(bdsHtml).select(".re__pagination-number");
                String bdsHref=bdsElements.last().attr("href");
                String[] bdsTemp=bdsHref.split("/");
                maxPageIndex= Integer.parseInt(bdsTemp[bdsTemp.length-1].replace("p",""));
                break;
        }
        return maxPageIndex;
    }

    @Autowired
    public void setApiKeyHelper(ApiKeyHelper apiKeyHelper) {
        this.apiKeyHelper = apiKeyHelper;
    }

    @Autowired
    public void setScraperWebServiceFactory(ScraperServiceFactory scraperServiceFactory) {
        this.scraperServiceFactory = scraperServiceFactory;
    }
}
