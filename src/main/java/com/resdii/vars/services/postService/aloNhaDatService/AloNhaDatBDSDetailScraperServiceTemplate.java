package com.resdii.vars.services.postService.aloNhaDatService;

import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.enums.PostStatus;
import com.resdii.vars.services.postService.BDSDetailTemplate;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.resdii.vars.utils.CommonUtils.*;

@Component
public class AloNhaDatBDSDetailScraperServiceTemplate<T extends PostDocument> extends BDSDetailTemplate<T> {
    @Override
    public PostStatus preHandleData(Document document) {
        if(document.html().contains("Nội dung bạn định xem không tồn tại")){
            System.out.println("Nội dung bạn định xem không tồn tại");
            return PostStatus.NOT_EXIST;
        }
        if(document.html().contains("Tôi không phải người máy")){
            System.out.println("Tôi không phải người máy");
            return PostStatus.FAILED;
        }
        return PostStatus.SUCCESS;
    }
    @Override
    public T getRaw(Elements elements,String url, T post) {
        post.setRawHtml(elements.html());
        post.setUrl(url);
        return post;
    }
    @Override
    public T getAuthorInformation(Elements docElements, T post) {
        Elements name = docElements.select("div.contact-info").select("div.name");
        Elements phone = docElements.select("div.contact-info").select("div.fone").select("a");
        post.setAuthor(postMapper.mapAuthor(name.text(), phone.get(0).text()));
        return post ;
    }
    @Override
    public T getTitle(Elements docElements, T post) {
        Elements elements = docElements.select("div.title").select("h1");
        post.setTitle(elements.text());
        return post;
    }
    @Override
    public T getDescription(Elements docElements, T post) {
        Elements elements = docElements.select("div.detail");
        post.setDescription(elements.html());
        return post;
    }
    @Override
    public T getSquare(Elements docElements, T post) {
        Elements elements = docElements.select("span.square").select("span.value");
        post.setSquare(postMapper.mapSquare(post, elements.text(), ','));
        return post;
    }
    @Override
    public T getListImages(Elements docElements, T post, String baseURL) {
        Elements elements = docElements.select("img.limage");
        return (T) postMapper.mapImagesList(post, elements, baseURL);
    }
    @Override
    public T getPrice(Elements docElements, T post) {
        Elements elements = docElements.select("span.price").select("span.value");
        return (T) postMapper.mapPrice(post, elements.text(),',');
    }
    @Override
    public T getLocationFromAddress(Elements docElements, T post){
        return (T) postMapper.mapLocationFromAddress(post);
    }
    @Override
    public T getAddress(Elements docElements, T post) {
        Elements elements = docElements.select("div.address").select("span.value");
        return (T) postMapper.mapAddress(post, elements.text());
    }
    @Override
    public T getDate(Elements docElements, T post) {
        Elements elements = docElements.select("div.title").select("span.date");
        String[] dateText=elements.text().split(":");
        if(dateText.length>1){
            post.setPostDate(convertDateTextToDate(dateText[dateText.length-1].replaceFirst(" ", "")));
            post.setStartDate(convertDateTextToDate(dateText[dateText.length-1].replaceFirst(" ", "")));
        }else {
            post.setPostDate(convertDateTextToDate(dateText[dateText.length-1]));
            post.setStartDate(convertDateTextToDate(dateText[dateText.length-1]));
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime ldt = LocalDateTime.now().plusDays(60);
        post.setEndDate(format.format(ldt));
        return post;
    }
    @Override
    public T getThumbnail(Elements docElements, T post, String baseUrl) {
        return (T) postMapper.mapThumbnail(post);
    }
    @Override
    public T getExtraInformation(Elements docElements, T post, String postType) {
        Elements elements = docElements.select("div.moreinfor1").select("div.infor").select("table").select("td");
        String direction = elements.select("td").get(3).text();
        String phongan = elements.select("td").get(5).text();
        String duongdi = elements.select("td").get(9).text();
        String nhabep = elements.select("td").get(11).text();
        String phaply = elements.select("td").get(15).text();
        String santhuong = elements.select("td").get(17).text();
        String chieungang = elements.select("td").get(19).text();
        String solau = elements.select("td").get(21).text();
        String chodexehoi = elements.select("td").get(23).text();
        String sophongngu = elements.select("td").get(27).text();
        String chinhchu = elements.select("td").get(29).text();
        post.setDirection(categoryMapper.mapDirection(direction));
        post.setPostType(categoryMapper.mapPostType(postType));
        post= (T) postMapper.mapEntrance(post,duongdi, ',');
        post= (T) postMapper.mapLegalDoc(post,phaply);
        post.setFrontWidth(convertLengthTextToNumber(chieungang, ','));
        post.setFloor(convertTextToNumber(solau));
        post.setBedroom(convertTextToNumber(sophongngu));
        post.setPostBy(categoryMapper.mapRePostBy(chinhchu));
        // Furniture
        List<String> feature=new ArrayList<>();
        if(!nhabep.equals("---")){
            feature.add("Nhà bếp");
        }
        if(!santhuong.equals("---")){
            feature.add("Sân thượng");
        }
        if(!phongan.equals("---")){
            feature.add("Phòng ăn");
        }
        if(!chodexehoi.equals("---")){
            feature.add("Chỗ để xe hơi");
        }
        if(feature.size()!=0){
            post.setFurniture(feature);
        }
        return post;
    }
    @Override
    public T getTypeOfRealEstate(Elements docElements, T post, String postType) {
        Elements elements = docElements.select("div.moreinfor1").select("div.infor").select("table").select("td");
        return (T) postMapper.mapRealEstateType(post,elements.select("td").get(13).text(), postType);
    }
}
