package com.resdii.vars.services.investorService;

import com.google.common.hash.Hashing;
import com.resdii.ms.common.utils.StringUtils;
import com.resdii.vars.dto.InvestorDTO;
import com.resdii.vars.dto.ProjectDTO;
import com.resdii.vars.dto.request.InvestorRequestDTO;
import com.resdii.vars.api.InvestorApiClient;
import com.resdii.vars.services.scraperWebservice.ScraperServiceCustomForBdsComImpl;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class InvestorServiceImpl implements InvestorService{
    RedisTemplate template;
    MongoTemplate mongoTemplate;
    InvestorApiClient investorApiClient;
    ScraperServiceCustomForBdsComImpl scraperServiceCustomForBdsComImpl;

    public void insertInvestor(String projectTable){
        List<ProjectDTO> listProjectDTO=mongoTemplate.findAll(ProjectDTO.class, projectTable);
        listProjectDTO.forEach(ele->{
            try{
                InvestorRequestDTO investorRequestDTO=new InvestorRequestDTO(ele.getInvestor());
                investorApiClient.investor(investorRequestDTO);
            }catch (Exception exception){
                exception.printStackTrace();
            }
        });
    }
    public void extractInvestor(String projectTable){
        Query query = new Query();
        query.addCriteria(Criteria.where("investorUrl").ne(""));
        List<String> listProjectDTO=mongoTemplate.findDistinct(query, "investorUrl",projectTable,String.class);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        listProjectDTO.forEach(ele->{
            executor.submit(()->{
                try{
                    String hashValue= Hashing.sha256().hashString(ele, StandardCharsets.UTF_8).toString();
                    if(Objects.isNull(template.opsForValue().get("SCRAPER:INVESTOR:"+hashValue))){
                        InvestorDTO investorDTO=new InvestorDTO();
                        Document document= scraperServiceCustomForBdsComImpl.loadInvestor(ele);
                        Element header=document.select(".re__ent-header").get(0);
                        investorDTO.setLogoUrl(header.select("img").attr("src"));
                        investorDTO.setInvestorName(header.select("h2").text());
                        Elements elements=document.select(".re__project-box-info .re__project-box-item");
                        investorDTO.setUrl(ele);
                        investorDTO.setRawHtml(document.html());
                        elements.forEach(info->{
                            if(info.select("label").text().equals("Địa chỉ")){
                                investorDTO.setInvestorAddress(info.select("span").text());
                            }
                            if(info.select("label").text().equals("Lĩnh vực chính")){
                                investorDTO.setInvestorPrimaryField(info.select("span").text());
                            }
                            if(info.select("label").text().equals("Lĩnh vực phụ")){
                                investorDTO.setInvestorSubField(info.select("span").text());
                            }
                            if(info.select("label").text().equals("Email")){
                                investorDTO.setInvestorEmail(info.select("span").text());
                            }
                            if(info.select("label").text().equals("Website")){
                                investorDTO.setInvestorWebsite(info.select("span").text());
                            }
                        });
                        if(!StringUtils.isNullOrEmpty(document.select(".phoneEvent").attr("raw"))){
                            investorDTO.setPhone(document.select(".phoneEvent").attr("raw"));
                        }
                        investorDTO.setInvestorDescription(document.select(".re__box-detail-content").html());
                        mongoTemplate.save(investorDTO, "investor-all");
                        template.opsForValue().set("SCRAPER:INVESTOR:"+hashValue, ele);
                    }
                }catch (Exception exception){
                    System.out.println(String.format("%s: %s", ele, exception.getMessage()));
                    exception.printStackTrace();
                }
            });
        });
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Autowired
    public void setInvestorFeign(InvestorApiClient investorApiClient) {
        this.investorApiClient = investorApiClient;
    }
    @Autowired
    public void setCustomForBdsCom(ScraperServiceCustomForBdsComImpl scraperServiceCustomForBdsComImpl) {
        this.scraperServiceCustomForBdsComImpl = scraperServiceCustomForBdsComImpl;
    }
    @Autowired
    public void setTemplate(RedisTemplate template) {
        this.template = template;
    }
}
