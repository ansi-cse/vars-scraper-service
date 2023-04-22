package com.resdii.vars.services.postService.bdsComService;

import com.resdii.ms.common.category.AddressItem;
import com.resdii.ms.common.category.Category;
import com.resdii.ms.common.utils.AddressUtils;
import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.AutoCompleteDTO;
import com.resdii.vars.dto.PostAuthorDTO;
import com.resdii.vars.enums.PostStatus;
import com.resdii.vars.services.postService.BDSDetailTemplate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.resdii.vars.utils.CommonUtils.*;

@Service
public class BDSComDetailServiceScraperTemplateImpl<T extends PostDocument> extends BDSDetailTemplate<T> {

    @Override
    public T getLocationFromAddress(Elements address, T post){
        AutoCompleteDTO autoCompleteDTO=locationHelper.getLocation(post.getAddress());
        if(!Objects.isNull(autoCompleteDTO)){
            post.setLat(Double.parseDouble(autoCompleteDTO.getLat()));
            post.setLng(Double.parseDouble(autoCompleteDTO.getLng()));
        }
        return post;
    }

    @Override
    public T getRaw(Elements elements, String url, T post) {
        post.setRawHtml(elements.html());
        post.setUrl(url);
        return post;
    }

    @Override
    public PostStatus preHandleData(Document document) {
        return PostStatus.SUCCESS;
    }

    @Override
    public T getAuthorInformation(Elements docElements,T post) {
        PostAuthorDTO postAuthorDTO=new PostAuthorDTO();

        Elements elements = docElements.select(".re__tablet-contact-info.re__vertical-align-middle").select(".re__contact-name.js_contact-name");
        postAuthorDTO.setName(elements.last().attr("title"));

        elements = docElements.select(".re__btn.re__btn-cyan-solid--md.phone.js__phone").select(".phoneEvent.js__phone-event");
//        Map<String, String> inputForDecrypt=new HashMap<>();
//        inputForDecrypt.put("PhoneNumber", elements.last().attr("raw"));
//        String temp=phoneFeign.decryptPhone(inputForDecrypt).getBody();
//        String phoneNumber=temp;
        String phoneNumber=elements.select(".re__content span").get(0).text().toLowerCase().replace("sao chép", "").trim();
        postAuthorDTO.setPhone(categoryMapper.mapPhoneNumber(phoneNumber));
        post.setAuthor(postAuthorDTO);
        post.setPriority(categoryMapper.mapPriority("1"));
//        post.setWalletId(loginHelper.getUserInfoDTO().getWallet().getWalletId());
        return post;
    }

    @Override
    public T getTitle(Elements docElements, T post) {
        Elements elements = docElements.select(".re__pr-title.pr-title.js__pr-title");
        String title=elements.text();
        post.setTitle(title);
        return post;
    }

    @Override
    public T getDescription(Elements docElements, T post) {
        Elements elements = docElements.select(".re__section-body.re__detail-content.js__section-body.js__pr-description.js__tracking");
        if(elements.text().equals("")){
            post.setDescription(post.getTitle());
        }
        post.setDescription(elements.html());
        return post;
    }

    @Override
    public T getSquare(Elements docElements, T post) {
        String squareText =docElements.select("div.re__pr-specs-content-item:has(.re__icon-size)").select(".re__pr-specs-content-item-value").text();
        Float square=convertSquareTextToNumber(squareText,'.');
        post.setSquare(square);
        return post;
    }

    @Override
    public T getListImages(Elements docElements, T post, String baseUrl) {
        Elements elements = docElements.select(".re__media-thumb-item.js__media-thumbs-item").select("img");
        List <MultipartFile> listImage=new ArrayList<>();
        for (Element element : elements) {
            MultipartFile multipartFile=getMultipartFile(element.attr("data-src"));
            listImage.add(multipartFile);
        }
        if(listImage.size()>0){
            listImage.remove(0);
        }
//        post.setImages(listImage);
        return post;
    }

    @Override
    public T  getPrice(Elements docElements, T post) {
        String priceText =docElements.select("div.re__pr-specs-content-item:has(.re__icon-money)").select(".re__pr-specs-content-item-value").text();
        post.setPrice(convertPriceTextToNumber(priceText, post.getSquare(), '.'));
        return post;
    }

    @Override
    public T  getAddress(Elements docElements, T post) {
        Elements elements = docElements.select(".re__pr-short-description.js__pr-address");
        String address = elements.text();
        post.setAddress(address);

        String[] addressArray = address.split(",");
        String provinceText = "";
        String districtText = "";
        String wardText = "";
        List<String> test = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            if (addressArray.length > 3) {
                test.add(addressArray[addressArray.length - i].replaceFirst(" ", ""));
            } else {
                if ((addressArray.length - i) > 0) {
                    test.add(addressArray[addressArray.length - i].replaceFirst(" ", ""));
                }
                if ((addressArray.length - i) == 0) {
                    test.add(addressArray[addressArray.length - i]);
                }
                if ((addressArray.length - i) < 0) {
                    test.add("");
                }
            }
        }
        provinceText=test.get(0);
        districtText=test.get(1);
        wardText=test.get(2);

        provinceText=categoryMapper.mapNewProvince(provinceText);

        if(!GlobalConstant.cityListWithoutPrefix.contains(provinceText) && !GlobalConstant.cityListWithPrefix.contains(provinceText)){
            provinceText="Tỉnh "+ provinceText;
        }
        if(GlobalConstant.cityListWithPrefix.contains(provinceText)){
            provinceText="Thành phố "+ provinceText;
        }
        if(GlobalConstant.cityListWithPrefixTP.contains(provinceText)){
            provinceText="TP "+ provinceText;
        }

        // Province
        AddressItem province= AddressUtils.getProvinceByName(provinceText);
        if(!Objects.isNull(province)){
            post.setProvince(new Category(province.getCode(), province.getName()));
        }
        // District
        Category category=categoryMapper.mapNewAddress(provinceText, districtText);
        if(Objects.isNull(category)){
            List<String> listDistrict=AddressUtils.getDistricts(province.getId()).stream().map(ele->ele.getName()).collect(Collectors.toList());
            String finalDistrictText = districtText;
            String districtWithPrefix = listDistrict.stream().filter(ele->ele.contains(finalDistrictText)).findFirst().get();
            districtText=districtWithPrefix;
            AddressItem district=AddressUtils.getDistrictByName(provinceText,districtWithPrefix);
            if(!Objects.isNull(district)){
                post.setDistrict(new Category(district.getCode(), district.getName()));
            }
        }else {
            post.setDistrict(category);
        }
        // Ward
        AddressItem wards= AddressUtils.getWardsByName(provinceText, districtText, wardText);
        if(!Objects.isNull(wards)){
            post.setWards(new Category(wards.getCode(), wards.getName()));
        }

        if(addressArray.length==2 && Objects.isNull(post.getDistrict())
                && districtText!="" && !districtText.toLowerCase().contains("quận")
                && !districtText.toLowerCase().contains("huyện") && !districtText.toLowerCase().contains("thành phố")
                && !districtText.toLowerCase().contains("thị xã")){
            post.setStreet(addressArray[0]);
        }
        if(addressArray.length==3 && Objects.isNull(post.getWards())
                && wardText!="" &&  !wardText.toLowerCase().contains("phường") && !wardText.toLowerCase().contains("xã") && !wardText.toLowerCase().contains("thị trấn")){
            post.setStreet(addressArray[0]);
        }
        if(addressArray.length>=4){
            if(wardText!="" &&  !wardText.toLowerCase().contains("phường") && !wardText.toLowerCase().contains("xã") && !wardText.toLowerCase().contains("thị trấn")){
                String[] street= Arrays.copyOfRange(addressArray, 0, addressArray.length-2);
                String streetText= String.join(",", street).trim();
                post.setStreet(streetText);
            }else{
                String[] street= Arrays.copyOfRange(addressArray, 0, addressArray.length-3);
                String streetText= String.join(",", street).trim();
                post.setStreet(streetText);
            }
        }
        return post;
    }

    @Override
    public T getDate(Elements docElements, T post) {
        Elements elements = docElements.select(".re__pr-short-info.re__pr-config.js__pr-config")
                .select(".re__pr-short-info-item.js__pr-config-item");
        Elements startDate=elements.first().select("span.value");
        Elements endDate=elements.get(1).select("span.value");
        post.setPostDate(startDate.text());
        post.setStartDate(startDate.text());
        post.setEndDate(endDate.text());
        return post;
    }

    @Override
    public T getThumbnail(Elements elements, T post, String baseUrl) {
        if(!Objects.isNull(post.getImages()) && post.getImages().size()!=0){
            post.setThumbnail(post.getImages().get(0));
            post.getImages().remove(0);
        }
        return post;
    }

    @Override
    public T getExtraInformation(Elements docElements, T post, Integer command) {
        Elements elements = docElements.select(".re__pr-specs-content.js__other-info");
        String direction = elements.select("div.re__pr-specs-content-item:has(.re__icon-front-view)").select(".re__pr-specs-content-item-value").text();
        String duongdi = elements.select("div.re__pr-specs-content-item:has(.re__icon-road)").select(".re__pr-specs-content-item-value").text();
        String phaply =elements.select("div.re__pr-specs-content-item:has(.re__icon-document)").select(".re__pr-specs-content-item-value").text();
        String chieungang =elements.select("div.re__pr-specs-content-item:has(.re__icon-home)").select(".re__pr-specs-content-item-value").text();
        String solau =elements.select("div.re__pr-specs-content-item:has(.re__icon-apartment)").select(".re__pr-specs-content-item-value").text();
        String sophongngu =elements.select("div.re__pr-specs-content-item:has(.re__icon-bedroom)").select(".re__pr-specs-content-item-value").text();
        String furniture =elements.select("div.re__pr-specs-content-item:has(.re__icon-interior)").select(".re__pr-specs-content-item-value").text();
        String toilet =elements.select("div.re__pr-specs-content-item:has(.re__icon-bath)").select(".re__pr-specs-content-item-value").text();
        post.setDirection(categoryMapper.mapDirection(direction));
        post.setPostType(categoryMapper.mapPostType(command));
        post.setEntrance(categoryMapper.mapEntrance(duongdi, '.'));
        post.setLegalDoc(categoryMapper.mapLegalDoc(phaply));
        post.setFrontWidth(convertLengthTextToNumber(chieungang, ','));
        post.setFloor(convertTextToNumber(solau));
        post.setBedroom(convertTextToNumber(sophongngu));
        post.setPostBy(categoryMapper.mapRePostBy("Môi giới"));
        post.setFurniture(Arrays.asList(furniture.split(",")));
        post.setBathroom(convertTextToNumber(toilet));
        return post;
    }


    @Override
    public T getTypeOfRealEstate(Elements docElements, T post, Integer command) {
        Elements elements = docElements.select(".pricing-insight--sub-title");
        String[] loaibdsText = elements.text().split("tại");
        String[] temp=loaibdsText[0].split(" ");
        Category category=categoryMapper.mapRealEstateType(GlobalConstant.commandMapToPostType.get(command)+"-"+String.join(" ", temp));
        post.setTypeRealEstate(category);
        return post;
    }

}