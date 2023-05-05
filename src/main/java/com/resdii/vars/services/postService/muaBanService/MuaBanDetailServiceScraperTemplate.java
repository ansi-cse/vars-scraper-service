package com.resdii.vars.services.postService.muaBanService;

import com.resdii.ms.common.category.AddressItem;
import com.resdii.ms.common.category.Category;
import com.resdii.ms.common.utils.AddressUtils;
import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.dto.AutoCompleteDTO;
import com.resdii.vars.dto.PostAuthorDTO;
import com.resdii.vars.enums.PostStatus;
import com.resdii.vars.services.postService.BDSDetailTemplate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.resdii.vars.utils.CommonUtils.*;
import static com.resdii.vars.utils.CommonUtils.convertDateTextToDate;

@Service
public class MuaBanDetailServiceScraperTemplate<T extends PostDocument> extends BDSDetailTemplate<T> {
    private String thongtincoban=".sc-6orc5o-18.ctYwuR";
    private String title=".sc-6orc5o-12.xgVAA";
    private String description=".sc-6orc5o-13.fqPvRS";
    private String thongtinnguoidang=".sc-lohvv8-4.fbIXMV";
    private String sdt=".sc-lohvv8-11.jUzypH";
    private String typeOfRealEstate=".sc-6orc5o-19.cILoPc";
    private String dientichdat=".sc-6orc5o-19.iZCOCD";
    private String dientichsudung=".sc-6orc5o-19.bgODQb";
    private String toilet=".sc-6orc5o-19.tUPBp";
    private String bedroom=".sc-6orc5o-19.cZKKR";
    private String floor=".sc-6orc5o-19.PgjzW";
    private String legal=".sc-6orc5o-19.jbOCYX";
    private String huongcua=".sc-6orc5o-19.cIFoYP";
    private String listImageClass=".slick-track";

    @Override
    public PostStatus preHandleData(Document document) {
        return PostStatus.SUCCESS;
    }

    @Override
    public T getRaw(Elements elements, String url, T post) {
        post.setRawHtml(elements.html());
        post.setUrl(url);
        return post;
    }

    @Override
    public  T getLocationFromAddress(Elements address, T post){
        AutoCompleteDTO autoCompleteDTO=locationHelper.getLocation(post.getAddress());
        if(!Objects.isNull(autoCompleteDTO)){
            post.setLat(Double.parseDouble(autoCompleteDTO.getLat()));
            post.setLng(Double.parseDouble(autoCompleteDTO.getLng()));
        }
        return post;
    }

    @Override
    public T getAuthorInformation(Elements docElements, T post) {
        PostAuthorDTO postAuthorDTO=new PostAuthorDTO();
        Elements name=docElements.select(thongtinnguoidang);
        postAuthorDTO.setName(name.text());
        Elements phone=docElements.select(sdt);
        postAuthorDTO.setPhone(phone.text());
        post.setAuthor(postAuthorDTO);
        post.setPriority(categoryMapper.mapPriority("1"));
        post.setWalletId(loginHelper.getUserInfoDTO().getWallet().getWalletId());
        return post;
    }

    @Override
    public  T getTitle(Elements docElements, T post) {
        Elements elements = docElements.select(title).select("h1");
        String title=elements.text();
        post.setTitle(title);
        return post;
    }

    @Override
    public  T getDescription(Elements docElements, T post) {
        Elements elements = docElements.select(description);
        if(elements.text().equals("")){
            post.setDescription(post.getTitle());
        }
        post.setDescription(elements.html());
        return post;
    }

    @Override
    public T getSquare(Elements docElements, T post) {
        Elements elementsDientichdat = docElements.select(dientichdat);
        Elements elementsDientichsudung = docElements.select(dientichsudung);
        if(elementsDientichdat.size()!=0){
            Float square=convertSquareTextToNumber(
                    elementsDientichdat.select("span").last().text().split(" ")[0],
                    '.');
            post.setSquare(square);
            return post;
        }
        if(elementsDientichsudung.size()!=0){
            Float square=convertSquareTextToNumber(
                    elementsDientichsudung.select("span").last().text().split(" ")[0],
                    '.');
            post.setSquare(square);
            return post;
        }
        return post;
    }

    @Override
    public  T getListImages(Elements docElements, T post, String baseUrl) {
        Elements elements = docElements.select(listImageClass).select("img");
        List <MultipartFile> listImage=new ArrayList<>();
        for (Element element : elements) {
            String listImg = element.attr("data-src");
            if(listImg.equals("")){
                listImg = element.attr("src");
            }
            listImage.add(getMultipartFile(listImg));
        }
        if(listImage.size()>0){
            listImage.remove(0);
        }
//        post.setImages(listImage);
        return post;
    }

    @Override
    public T getPrice(Elements docElements, T post) {
        Elements elements = docElements.select(title).select(".price");
        post.setPrice(convertPriceTextToNumber(elements.text(), post.getSquare(),',' ));
        return post;
    }

    @Override
    public T getAddress(Elements docElements, T post) {
        // Address, Province, District, Ward
        Elements elements = docElements.select(title).select(".address");
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
            AddressItem district=AddressUtils.getDistrictByName(provinceText,districtText);
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
        Elements elements = docElements.select(title).select(".date");
        String[] dateText=elements.text().split("-")[0].split(":");
        post.setPostDate(convertDateTextToDate(dateText[dateText.length-1].trim()));
        post.setStartDate(convertDateTextToDate(dateText[dateText.length-1].trim()));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime ldt = LocalDateTime.now().plusDays(60);
        post.setEndDate(format.format(ldt));
        return post;
    }

    @Override
    public T getThumbnail(Elements elements, T post, String baseUrl) {
//        if(Objects.isNull(post.getImages()) && post.getImages().size()>0){
//            post.setThumbnail(post.getImages().get(0));
//        }
        return post;
    }

    @Override
    public T getExtraInformation(Elements docElements, T post, String postType) {
        post.setPostType(categoryMapper.mapPostType(postType));
        Elements toiletElement=docElements.select(toilet);
        if(Objects.isNull(toiletElement)){
            post.setBathroom(Integer.parseInt(toiletElement
                    .select("span").last().text().replace("WC","").trim()));
        }
        Elements floorElement=docElements.select(floor);
        if(floorElement.size()!=0){
            post.setFloor(Integer.parseInt(floorElement.select("span").last().text()));
        }
        Elements bedElement=docElements.select(bedroom);
        if(bedElement.size()!=0){
            post.setBedroom(Integer.parseInt(bedElement
                    .select("span").last().text().replace("phòng","").trim()));
        }
        Elements legalElement=docElements.select(legal);
        if(legalElement.size()!=0){
            post.setLegalDoc(categoryMapper.mapLegalDoc(legalElement.select("span").last().text()));

        }
        Elements huongcuaElement=docElements.select(huongcua);
        if(huongcuaElement.size()!=0){
            post.setDirection(categoryMapper.mapDirection(huongcuaElement.select("span").last().text()));

        }
        post.setPostBy(categoryMapper.mapRePostBy("Môi giới"));
        return post;
    }

    @Override
    public T getTypeOfRealEstate(Elements docElements, T post, String postType) {
        Elements elements = docElements.select(typeOfRealEstate).select("span");
        String loaiBds = elements.last().text();
        post.setTypeRealEstate(categoryMapper.mapRealEstateType(loaiBds, postType));
        return post;
    }
}
