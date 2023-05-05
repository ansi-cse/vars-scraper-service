package com.resdii.vars.services.projectService;

import com.resdii.ms.common.category.AddressItem;
import com.resdii.ms.common.utils.AddressUtils;
import com.resdii.ms.common.utils.StringUtils;
import com.resdii.vars.dto.LocationDTO;
import com.resdii.vars.dto.ProjectDTO;
import com.resdii.vars.enums.PostStatus;
import com.resdii.vars.services.scraperWebservice.scraperServiceFactory.ScraperServiceFactory;
import com.resdii.vars.services.WebBaseScraperImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperByMeImpl;
import com.resdii.vars.services.WebScraper;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.resdii.vars.utils.CommonUtils.getImage;

@Service
public class WebScraperProjectImpl<T extends ProjectDTO>  extends WebBaseScraperImpl implements WebScraper<T> {
    protected MongoTemplate mongoTemplate;
    protected ScraperServiceFactory scraperServiceFactory;
    private String baseurl;

    public WebScraperProjectImpl() {
        setBaseurl("https://batdongsan.com.vn/");
    }

    @PostConstruct
    public void postConstructor(){
        setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceScraperByMeImpl.class));
    }

    @Override
    public PostStatus scrape(String url, String postType,String prefix,  String api_key, T post) {
        try{
            System.out.println(url);
            Document document=loadPage(url, api_key);
            post=extractData(url, parsePage(document), postType, post);
            saveDataToDB(post, "project-all");
        }catch (Exception e){
            System.out.println(String.format("%s: %s", url, e.getMessage()));
            return PostStatus.FAILED;
        }
        return PostStatus.SUCCESS;
    }
    @Override
    public T extractData(String url, Elements docElements, String postType, T post) {
        post.setUrl(url);
        post.setRawHtml(docElements.html());
        post.setProjectName(docElements.select(".re__project-name").text());
        String breadCum=docElements.select(".re__project-breadcrumb").text();
        String[] breadCumList=breadCum.split("/");
        post.setProjectType(breadCumList[breadCumList.length-1].replace(post.getProjectName(),"").trim());
        post.setAddress(docElements.select(".re__project-address").text().replace(". Xem bản đồ", ""));
        String status=docElements.select(".re__project-album-container").select(".re__prj-tag-info").text();
        post.setStatus(status);
        Map<String, String> detailInformation=new HashMap<>();
        docElements.select(".re__project-box-item").stream().forEach(ele->{
            String key=ele.select("label").text();
            String value=ele.select("span").text();
            detailInformation.put(key, value);
        });
        post.setDetailInformation(detailInformation);
        post.setInvestor(docElements.select(".re__inves-title").text());

        Elements descriptionImageUrlsList=docElements.select("#tab-info").select("img");
        descriptionImageUrlsList.forEach(ele->{
            String destinationFile ="project/images/"+ FilenameUtils.getName(ele.attr("src"));
            try{
                getImage(ele.attr("src"), destinationFile);
            }catch (Exception e){
                post.setLoadImageFailed(true);
            }
            ele.removeAttr("src");
            ele.attr("src",destinationFile);
        });
        String description=docElements.select("#tab-info").html();
        //replace image
        post.setDescription(description);

        Elements facilities=docElements.select(".re__project-toogle.re__prj-facilities").select("ul li");
        List<String> listFacilities=new ArrayList<>();
        facilities.forEach(ele->{
            listFacilities.add(ele.text());
        });
        post.setFacilities(listFacilities);

        String price=post.getDetailInformation().get("Giá");
        if(!StringUtils.isNullOrEmpty(price)){
            post.setPrice(price);
        }else {
            post.setPrice("đang cập nhật");
        }

        Elements projectInfoDetail=docElements.select(".re__project-info-details.re__clearfix > .re__project-info-details__wrapper");
        projectInfoDetail.forEach(ele->{
            String value=ele.select(".re__project-info-details__value").text();
            String unit=ele.select(".re__project-info-details__unit").text();
            if(unit.equals("căn hộ")){
                post.setNumOfApartments(value.replace(",", ""));
            }else{
                String[] temp=value.split(" ");
                Float square=Float.valueOf(temp[0].replace(",",""));
                if(unit.equals("ha")){
                    post.setSquare(String.valueOf(square*10000));
                }
                if(unit.equals("m²")){
                    post.setSquare(String.valueOf(square));
                }
            }
        });

        Elements scriptElements = docElements.tagName("script");
        JSONObject paramsMap = new JSONObject();
        for (Element element : scriptElements) {
            if (element.data().contains("paramsMap")) {
                Pattern pattern = Pattern.compile("var\\sparamsMap\\s=\\s\\{[.\\'\\n\\\\\\s\\\"\\:\\w\\-\\.\\,\\/\\t\\n\\rÀÁÂÃÈÉÊẾÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêếìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]*\\}");
                Matcher matcher = pattern.matcher(element.data());
                if (matcher.find()) {
                    paramsMap=new JSONObject(matcher.group().replace("var paramsMap = ",""));
                } else {
                    System.err.println("No match found!");
                }
                break;
            }
        }
        try{
            LocationDTO locationDTO=new LocationDTO();
            locationDTO.setLat(paramsMap.get("latitude").toString());
            locationDTO.setLng(paramsMap.get("longitude").toString());
            post.setLocation(locationDTO);
        }catch (Exception exception){
            System.out.println("not found location");
        }

        try{
            String groundDrawingUrl=docElements.select("#design-overview img").attr("src");
            String groundDrawingUrlDestination="project/images/"+FilenameUtils.getName(groundDrawingUrl);
            getImage(groundDrawingUrl, groundDrawingUrlDestination);
            post.setGroundDrawingUrl(groundDrawingUrlDestination);
        }catch (Exception e){
            System.err.println("groundDrawingUrl");
        }
        return post;
    }
    @Override
    public void saveDataToDB(T data, String tableName) {
        mongoTemplate.save(data, tableName);
    }
    public void updateProjectDTO(String projectTable){
        List<ProjectDTO> listToUpdate=mongoTemplate.findAll(ProjectDTO.class, projectTable);
        listToUpdate.forEach(ele->{
            System.out.println(ele.getUrl());
            Document document=Jsoup.parse(ele.getRawHtml());
            ele.setInvestorUrl(getInvestorUrl(document));
            ele.setProgress(getProgress(document));
            ele.setProgressImages(getProgressImages(document));
            ele.setLegalDoc(getLegalDoc((T) ele));
            ele.setScale(getScale((T) ele));
            ele.setNumOfHouse(getNumOfHouse((T) ele));
            ele.setListImage(getListMedia(document,"#project-photos .js__project-media.re__project-album__media.re__project-album__media--photo"));
            ele.setListVideo(getListMedia(document, "#project-photos .re__project-album__media.re__project-album__media--video.js__video-embed"));
            ele.setListImageInDetail(getListImage(document,"#tab-info"));
            ele.setListImageDetailGroundDrawing(ListImageDetailGroundDrawing(document,".js__prj-design-content.re__prj-design-content li"));
            ele.setProjectType(getProjectType(document));
            ele.setProvince(getProvince(document));
            ele.setDistrict(getDistrict(document,(T) ele));
            ele.setWards(getWards(document, (T) ele));
            ele=parsePrice(document, (T) ele);
            ele.setGroundDrawingUrl(getGroundDrawingUrl(document));
            ele.setStatus(getStatus(document));
            ele.setExtraStatus(getExtraStatus(document));
            mongoTemplate.save(ele, projectTable);
//            String squareText=ele.getDetailInformation().get("Diện tích");
//            if(!StringUtils.isNullOrEmpty(squareText)){
//                String[] temp=squareText.split(" ");
//                Float square=Float.valueOf(temp[0].replace(",",""));
//                if(temp[1].equals("ha")){
//                    ele.setSquare(String.valueOf(square*10000));
//                }else{
//                    ele.setSquare(String.valueOf(square));
//                }
//                mongoTemplate.save(ele, "project-"+java.time.LocalDate.now());
//            }
        });
    }
    public String getGroundDrawingUrl(Document docElements){
        try{
           return docElements.select("#design-overview img").attr("src");
        }catch (Exception exception){
            return "";
        }
    }
    public String getStatus(Document document){
        try{
            String status=document.select(".re__project-album-container").select(".re__prj-tag-info label").text();
            return status;
        }catch (Exception exception){
            return "đang cập nhật";
        }
    }
    public String getExtraStatus(Document document){
        try{
            String status=document.select(".re__project-album-container").select(".re__prj-tag-info .re__prj-tag-ext").text();
            return status;
        }catch (Exception exception){
            return "đang cập nhật";
        }
    }
    public String getProjectType(Document docElements){
        String projectName=docElements.select(".re__project-name").text();
        String projectType="";
        try{
            String breadCum=docElements.select(".re__project-breadcrumb").text();
            String[] breadCumList=breadCum.split("/");
            projectType=breadCumList[breadCumList.length-1].replace(projectName,"").trim();
        }catch (Exception exception){
            return projectType;
        }
        return projectType;
    }
    public T parsePrice(Document docElements, T data){
        String[] parsePrice=data.getPrice().split(" ");
        try{
            if(parsePrice.length==4){
                data.setMaxPrice(parsePrice[2]);
                data.setMinPrice(parsePrice[0]);
                data.setPriceUnit(parsePrice[3]);
                data.setPrice("");
            }
            if(parsePrice.length==2){
                data.setPrice(parsePrice[0]);
                data.setPriceUnit(parsePrice[1]);
            }
        }catch (Exception exception){
            return data;
        }
        return data;
    }
    public String getProvince(Document docElements){
        String province="";
        try{
            String breadCum=docElements.select(".re__project-breadcrumb").text();
            String[] breadCumList=breadCum.split("/");
            province=breadCumList[1].trim();
            if(province.equalsIgnoreCase("bà rịa vũng tàu")){
                return "Tỉnh Bà Rịa - Vũng Tàu";
            }
            if(province.equalsIgnoreCase("hòa bình")){
                return "Tỉnh Hòa Bình";
            }
            for (int i = 0; i < AddressUtils.getProvinces().size(); i++) {
                AddressItem addressItemProvince=AddressUtils.getProvinces().get(i);
                String temp=addressItemProvince.getName().toLowerCase();
                if(temp.contains(province.toLowerCase())){
                    return addressItemProvince.getName();
                }
            }
        }catch (Exception exception){
            return "";
        }
        return "";
    }
    public String getDistrict(Document docElements, T data){
        String district="";
        try{
            String breadCum=docElements.select(".re__project-breadcrumb").text();
            String[] breadCumList=breadCum.split("/");
            district=breadCumList[2].trim();
            for (int j = 0; j < AddressUtils.getDistricts(AddressUtils.getProvinceByName(data.getProvince()).getId()).size(); j++) {
                AddressItem addressItemDistrict=AddressUtils.getDistricts(AddressUtils.getProvinceByName(data.getProvince()).getId()).get(j);
                if(addressItemDistrict.getName().toLowerCase().contains(district.toLowerCase())){
                    return addressItemDistrict.getName();
                }
            }
        }catch (Exception exception){
            return "";
        }
        return "";
    }
    public String getWards(Document docElements,T data){
        String wards="";
        try{
            String[] address=docElements.select(".re__project-address").text().split(",");
            wards=address[address.length-3]
                    .toLowerCase()
                    .replace("phường","")
                    .replace("xã","")
                    .replace("thị trấn","").trim();
            String province= data.getProvince();
            String provinceId="";
            String districtId="";

            AddressItem addressItemProvince=AddressUtils.getProvinceByName(data.getProvince());
            province=addressItemProvince.getName();
            provinceId=addressItemProvince.getId();
            AddressItem addressItemDistrict=AddressUtils.getDistrictByName(province,data.getDistrict());
            districtId=addressItemDistrict.getId();
            for (int i = 0; i < AddressUtils.getWards(provinceId, districtId).size(); i++) {
                AddressItem addressItemWard=AddressUtils.getWards(provinceId, districtId).get(i);
                if(addressItemWard.getName().toLowerCase().contains(wards.toLowerCase())){
                    return address[address.length-3].trim();
                }
            }
        }catch (Exception exception){
            return "";
        }
        return "";
    }
    public String getLegalDoc(T data){
        String legalDoc="";
        try{
            legalDoc=data.getDetailInformation().get("Pháp lý");
        }catch (Exception exception){
            return legalDoc;
        }
        return legalDoc;
    }
    public String getScale(T data){
        String scale="";
        try{
            scale=data.getDetailInformation().get("Quy mô");
        }catch (Exception exception){
            return scale;
        }
        return scale;
    }
    public String getNumOfHouse(T data){
        String numOfHouse="";
        try{
            numOfHouse=data.getDetailInformation().get("Số tòa");
        }catch (Exception exception){
            return numOfHouse;
        }
        return numOfHouse;
    }
    public String getProgress(Document rawHtml){
        String progress="";
        try{
            Element element=rawHtml.select(".re__project-main-content").select("h3:contains(Tiến độ)").get(0);
            while (element.nextElementSibling().tagName().equals("p")){
                progress =progress.concat(element.nextElementSibling().text());
                element=element.nextElementSibling();
            }
        }catch (Exception exception){
            return progress;
        }
        return progress;
    }
    public String getInvestorUrl(Document rawHtml){
        String investorUrl="";
        try{
            investorUrl=rawHtml.select(".re__inves-info-link a").attr("href");
        }catch (Exception exception){
            return investorUrl;
        }
        return investorUrl;
    }
    public List<String> getProgressImages(Document rawHtml){
        List<String> listImageOfProgress=new ArrayList<>();

        try{
            Element element=rawHtml.select(".re__project-main-content").select("h3:contains(Tiến độ)").get(0);
            while (element.nextElementSibling().tagName().equals("p")){
                element=element.nextElementSibling();
            }
            while (element.nextElementSibling().tagName().equals("figure")){
                listImageOfProgress.add(element.nextElementSibling().select("img").attr("src"));
                element=element.nextElementSibling();
            }
        }catch (Exception exception){
            return listImageOfProgress;
        }
        return listImageOfProgress;
    }
    public List<String> getListImage(Document rawHtml, String cssSelector){
        List<String> listImage=new ArrayList<>();
        try{
            rawHtml.select(cssSelector).select("img").forEach(ele->{
                listImage.add(ele.attr("src"));
            });
        }catch (Exception exception){
            return listImage;
        }
        return listImage;
    }
    public List<String> ListImageDetailGroundDrawing(Document rawHtml, String cssSelector){
        List<String> listImage=new ArrayList<>();
        try{
            rawHtml.select(cssSelector).forEach(ele->{
                listImage.add(ele.attr("data-image-src"));
            });
        }catch (Exception exception){
            return listImage;
        }
        return listImage;
    }
    public List<String> getListMedia(Document rawHtml, String cssSelector){
        List<String> listImage=new ArrayList<>();
        try{
            rawHtml.select(cssSelector).forEach(ele->{
                listImage.add(ele.attr("href"));
            });
        }catch (Exception exception){
            return listImage;
        }
        return listImage;
    }
    public void concatFile(){
        List<ProjectDTO> listToUpdate28=mongoTemplate.findAll(ProjectDTO.class, "project-2023-03-28");
        List<ProjectDTO> listToUpdate29=mongoTemplate.findAll(ProjectDTO.class, "project-2023-03-29");
        List<ProjectDTO> listToUpdate30=mongoTemplate.findAll(ProjectDTO.class, "project-2023-03-30");
        listToUpdate28.forEach(ele->{
            mongoTemplate.save(ele, "project-all");
        });
        listToUpdate29.forEach(ele->{
            mongoTemplate.save(ele, "project-all");
        });
        listToUpdate30.forEach(ele->{
            mongoTemplate.save(ele, "project-all");
        });
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {this.mongoTemplate = mongoTemplate;}
    @Autowired
    public void setScraperWebServiceFactory(ScraperServiceFactory scraperServiceFactory) {this.scraperServiceFactory = scraperServiceFactory;}
    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }
}
