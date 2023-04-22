package com.resdii.vars.services.brokerService;

import com.google.common.hash.Hashing;
import com.resdii.vars.dto.BrokerDTO;
import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.enums.BrokerType;
import com.resdii.vars.api.ScraperByMeApiClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ANSI.
 */

@Service
public class BrokerPageBdsComImpl implements BrokerPage {

    String baseUrl="https://batdongsan.com.vn/nha-moi-gioi/";
    RedisTemplate redisTemplate;
    MongoTemplate mongoTemplate;
    ScraperByMeApiClient scraperByMeApiClient;
    BrokerDetailBdsComImpl brokerDetailBdsComImpl;

    @Override
    public void getLinks() {
//        getLinksForEnterprise();
        getLinksForPersonal();
    }
    @Override
    public void getDetail(String type) {
        Set<String> listBrokerPending=redisTemplate.keys("SCRAPER:BDS:BROKER:"+type+":DETAIL:PENDING:*");
        List<String> listBrokerUrlPending = redisTemplate.opsForValue().multiGet(listBrokerPending);
        listBrokerUrlPending.forEach(ele->{
            String hashValue=Hashing.sha256().hashString(ele, StandardCharsets.UTF_8).toString();
            redisTemplate.delete("SCRAPER:BDS:BROKER:"+type+":DETAIL:PENDING:"+hashValue);
            redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+type+":DETAIL:PROCESSING:"+hashValue, ele);
            try {
                Document document=Jsoup.parse(scraperByMeApiClient.getHtml(baseUrl+ele).getBody());
                Document.OutputSettings outputSettings = new Document.OutputSettings();
                outputSettings.prettyPrint(false);
                BrokerDTO brokerDTO=getDetailBroker(ele, document, type);
                mongoTemplate.save(brokerDTO, "broker");
                redisTemplate.delete("SCRAPER:BDS:BROKER:"+type+":DETAIL:PROCESSING:"+hashValue);
                redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+type+":DETAIL:SUCCESS:"+hashValue, ele);
            }catch (Exception exception){
                System.out.println(String.format("%s: %s", ele, exception.getMessage()));
                redisTemplate.delete("SCRAPER:BDS:BROKER:"+type+":DETAIL:PROCESSING:"+hashValue);
                redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+type+":DETAIL:FAILED:"+hashValue, ele);
            }
        });
    }

//    @Override
//    public void update(String type) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("name").is("").and("type").is(type));
//        List<BrokerDTO> listDataToUpdate= mongoTemplate.find(query, BrokerDTO.class, "broker");
//        listDataToUpdate.forEach(ele->{
//            String hashValue=Hashing.sha256().hashString(ele.getUrl(), StandardCharsets.UTF_8).toString();
//            redisTemplate.delete("SCRAPER:BDS:BROKER:"+type+":DETAIL:SUCCESS:"+hashValue);
//            redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+type+":DETAIL:PROCESSING:"+hashValue, ele.getUrl());
//            try{
//                Document document=Jsoup.parse(scraperByMeApiClient.getHtml(baseUrl+ele.getUrl()).getBody());
//                Document.OutputSettings outputSettings = new Document.OutputSettings();
//                outputSettings.prettyPrint(false);
//                if(document.html().contains("Just a moment...") || document.html().contains("Không tìm thấy liên kết")){
//                    redisTemplate.delete("SCRAPER:BDS:BROKER:"+type+":DETAIL:PROCESSING:"+hashValue);
//                    redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+type+":DETAIL:FAILED:"+hashValue, ele.getUrl());
//                    return;
//                }
//                BrokerDTO brokerDTO=getDetailBroker(ele.getUrl(), document, type);
//                ele.setRawHtml(brokerDTO.getRawHtml());
//                ele.setAddress(brokerDTO.getAddress());
//                ele.setName(brokerDTO.getName());
//                ele.setDescription(brokerDTO.getDescription());
//                ele.setEmail(brokerDTO.getEmail());
//                ele.setLogoUrl(brokerDTO.getLogoUrl());
//                ele.setPhone(brokerDTO.getPhone());
//                mongoTemplate.save(ele, "broker");
//                redisTemplate.delete("SCRAPER:BDS:BROKER:"+type+":DETAIL:PROCESSING:"+hashValue);
//                redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+type+":DETAIL:SUCCESS:"+hashValue, ele.getUrl());
//            }catch (Exception exception){
//                System.out.println(String.format("%s: %s", ele.getUrl(), exception.getMessage()));
//                redisTemplate.delete("SCRAPER:BDS:BROKER:"+type+":DETAIL:PROCESSING:"+hashValue);
//                redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+type+":DETAIL:FAILED:"+hashValue, ele.getUrl());
//            }
//        });
//
//    }

    public void getLinksForEnterprise() {
        int numOfPage=countPage();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 1; i <= numOfPage; i++) {
            int pageIndex=i;
            executor.submit(()->{
                String pageUrl=pageIndex(pageIndex);
                if(redisTemplate.keys("SCRAPER:BDS:BROKER:"+BrokerType.ETP.value+":PAGE:*:"+pageIndex).size()!=0){
                    return;
                }
                try{
                    Document pageHtml= Jsoup.parse(scraperByMeApiClient.getHtmlForBdsComEnterpriseBrokerPage(pageUrl).getBody());
                    if(pageHtml.html().contains("Rất tiếc!") || pageHtml.html().contains("Just a moment...")){
                        redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+BrokerType.ETP.value+":PAGE:FAILED:"+pageIndex, pageUrl);
                        return;
                    }
                    if(pageHtml.select(".re__tab-box--sm.re__tab-box--actived").text().equals("Cá nhân môi giới")){
                        redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+BrokerType.ETP.value+":PAGE:FAILED:"+pageIndex, pageUrl);
                        return;
                    }
                    Elements elements=pageHtml.select(".re__broker-item");
                    AtomicReference<Integer> numOfSuccess= new AtomicReference<>(0);
                    elements.forEach(ele->{
                        String href=ele.select(".re__broker-info .re__link-se").attr("href");
                        if(saveLink(href, BrokerType.ETP.value)){
                            numOfSuccess.getAndSet(numOfSuccess.get() + 1);
                        }
                    });
                    redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+BrokerType.ETP.value+":PAGE:SUCCESS:"+pageIndex, pageUrl);
                }catch (Exception exception){
                    redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+BrokerType.ETP.value+":PAGE:FAILED:"+pageIndex, pageUrl);
                }
            });
        }
    }
    public void getLinksForPersonal() {
        String pageInit=pageIndex(1);
        Document document= Jsoup.parse(scraperByMeApiClient.getHtmlForBdsComPersonalBrokerPage(pageInit).getBody());
        Elements page=document.select(".re__pagination-number");
        int numOfPage=Integer.parseInt(page.last().text());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int i = 1; i <= numOfPage; i++) {
            int pageIndex=i;
            executor.submit(()->{
                String pageUrl=pageIndex(pageIndex);
                if(redisTemplate.keys("SCRAPER:BDS:BROKER:"+BrokerType.PER.value+":PAGE:*:"+pageIndex).size()!=0){
                    return;
                }
                try {
                    Document pageHtml= Jsoup.parse(scraperByMeApiClient.getHtmlForBdsComPersonalBrokerPage(pageUrl).getBody());
                    if(pageHtml.html().contains("Rất tiếc!") || pageHtml.html().contains("Just a moment...")){
                        redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+BrokerType.PER.value+":PAGE:FAILED:"+pageIndex, pageUrl);
                        return;
                    }
                    Elements elements=pageHtml.select(".re__broker-item");
                    if(pageHtml.select(".re__tab-box--sm.re__tab-box--actived").text().equals("Công ty môi giới")){
                        redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+BrokerType.PER.value+":PAGE:FAILED:"+pageIndex, pageUrl);
                        return;
                    }
                    elements.forEach(ele->{
                        String href=ele.select(".re__broker-info .re__link-se").attr("href");
                        saveLink(href, BrokerType.PER.value);
                    });
                    redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+BrokerType.PER.value+":PAGE:SUCCESS:"+pageIndex, pageUrl);
                }catch (Exception exception){
                    redisTemplate.opsForValue().set("SCRAPER:BDS:BROKER:"+BrokerType.PER.value+":PAGE:FAILED:"+pageIndex, pageUrl);
                }
            });
        }
    }
    public BrokerDTO getDetailBroker(String url, Document document, String brokerType) {
        BrokerDTO brokerDTO=new BrokerDTO();
        brokerDTO.setUrl(url);
        brokerDTO.setRawHtml(document.html());
        brokerDTO.setName(brokerDetailBdsComImpl.getName(document));
        brokerDTO.setDescription(brokerDetailBdsComImpl.getDescription(document));
        brokerDTO.setPhone(brokerDetailBdsComImpl.getPhone(document));
        brokerDTO.setAddress(brokerDetailBdsComImpl.getAddress(document));
        brokerDTO.setEmail(brokerDetailBdsComImpl.getEmail(document));
        brokerDTO.setLogoUrl(brokerDetailBdsComImpl.getLogoUrl(document));
        brokerDTO.setType(brokerType);
        return brokerDTO;
    }
    public boolean saveLink(String href, String brokerType){
        String hashValueHref=Hashing.sha256().hashString(href, StandardCharsets.UTF_8).toString();
        String keyForRedis="SCRAPER:BDS:BROKER:"+brokerType+":DETAIL:PENDING:"+hashValueHref;
        if(redisTemplate.keys("SCRAPER:BDS:BROKER:"+brokerType+":DETAIL:*:"+hashValueHref).size()==0){
            redisTemplate.opsForValue().set(keyForRedis, href);
            return true;
        }
        return false;
    }
    public String pageIndex(int index) {
        return baseUrl+"p"+index;
    }
    public int countPage() {
        String pageUrl=pageIndex(1);
        Document document= Jsoup.parse(scraperByMeApiClient.getHtmlForBdsComEnterpriseBrokerPage(pageUrl).getBody());
        Elements elements=document.select(".re__pagination-number");
        return Integer.parseInt(elements.last().text());
    }

    @Autowired
    public void setScraperByMeFeign(ScraperByMeApiClient scraperByMeApiClient) {
        this.scraperByMeApiClient = scraperByMeApiClient;
    }
    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Autowired
    public void setBdsComBrokerDetail(BrokerDetailBdsComImpl brokerDetailBdsComImpl) {this.brokerDetailBdsComImpl = brokerDetailBdsComImpl;}
    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {this.mongoTemplate = mongoTemplate;}
}
