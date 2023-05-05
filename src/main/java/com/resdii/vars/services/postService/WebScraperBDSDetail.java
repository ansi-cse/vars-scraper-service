package com.resdii.vars.services.postService;

import com.resdii.vars.enums.PostStatus;
import com.resdii.vars.services.WebScraper;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
@Scope("prototype")
public class WebScraperBDSDetail<T> extends BDSSupportScraper implements WebScraper<T> {
    protected BDSDetailTemplate bdsDetailTemplate;

    @Override
    public PostStatus scrape(String url, String postType,String prefix, String apiKey, T post) {
        try {
           bdsDetailTemplate.setPostMapper(postMapperFactory.getPostMapper(post.getClass()));
           System.out.println(url);
           Document document = loadPage(url, apiKey);
           Document.OutputSettings outputSettings = new Document.OutputSettings();
           outputSettings.prettyPrint(false);
           document.outputSettings(outputSettings);

           PostStatus preHandleDataForScraperStatus = scraperService
                   .preHandleDataForParser(document);
           if (!preHandleDataForScraperStatus.equals(PostStatus.SUCCESS)) {
               return preHandleDataForScraperStatus;
           }
           PostStatus preHandleDataStatus = bdsDetailTemplate.preHandleData(document);
           if (!preHandleDataStatus.equals(PostStatus.SUCCESS)) {
               return preHandleDataStatus;
           }

           T data = extractData(url, parsePage(document), postType, post);

           saveDataToDB(data, "post-"+postType+"-"+prefix+"-"+ java.time.LocalDate.now());

           return PostStatus.SUCCESS;
       }catch (Exception e) {
           e.printStackTrace();
           System.out.println(String.format("%s: %s", url, e.getMessage()));
           return PostStatus.FAILED;
       }
    }
    @Override
    @SneakyThrows
    public T extractData(String url, Elements docElements, String postType, T post) {
        // Base url
        URL toBasUrl = new URL(url);
        String baseUrl = toBasUrl.getProtocol() + "://" + toBasUrl.getHost() + "/";
        // Raw html
        post=(T) bdsDetailTemplate.getRaw(docElements,url, post);
        // Title
        post=(T) bdsDetailTemplate.getTitle(docElements, post);
        // Description
        post=(T) bdsDetailTemplate.getDescription(docElements, post);
        // Square
        post=(T) bdsDetailTemplate.getSquare(docElements, post);
        // Address, province, district, wards
        post=(T) bdsDetailTemplate.getAddress(docElements, post);
        // Date
        post=(T) bdsDetailTemplate.getDate(docElements, post);
        // Type of real estate
        post=(T) bdsDetailTemplate.getTypeOfRealEstate(docElements, post, postType);
        // Extra information
        post=(T) bdsDetailTemplate.getExtraInformation(docElements, post, postType);
        // Post author information
        post=(T) bdsDetailTemplate.getAuthorInformation(docElements, post);
        // Price
        post=(T) bdsDetailTemplate.getPrice(docElements, post);
        // Lat, lng
        post=(T) bdsDetailTemplate.getLocationFromAddress(docElements, post);
        // List image
        post=(T) bdsDetailTemplate.getListImages(docElements, post, baseUrl);
        // Thumbnail
        post=(T) bdsDetailTemplate.getThumbnail(docElements, post, baseUrl);

        return post;
    }
    @Override
    public void saveDataToDB(T data, String tableName) {
        mongoTemplate.save(data, tableName);
    }

    public void setBdsDetailPageTemplate(BDSDetailTemplate bdsDetailTemplate) {
        this.bdsDetailTemplate = bdsDetailTemplate;
    }
}
