package com.resdii.vars.mapper;

import com.resdii.ms.common.category.AddressItem;
import com.resdii.ms.common.category.Category;
import com.resdii.ms.common.utils.AddressUtils;
import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.AutoCompleteDTO;
import com.resdii.vars.dto.PostAuthorDTO;
import com.resdii.vars.dto.PostInfoDTO;
import com.resdii.vars.helper.LocationHelper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.resdii.vars.utils.CommonUtils.*;

public class PostMapper<T extends PostInfoDTO> {
    CategoryMapper categoryMapper;
    LocationHelper locationHelper;

    public T mapRealEstateType(T post, String name, String postType) {
        post.setTypeRealEstate(categoryMapper.mapRealEstateType(name, postType));
        return post;
    }

    public T mapLegalDoc(T post, String name) {
        post.setLegalDoc(categoryMapper.mapLegalDoc(name));
        return post;
    }

    public T mapEntrance(T post, String name, Character sperator){
        post.setEntrance(categoryMapper.mapEntrance(name, sperator));
        return post;
    };

    public T mapAddress(T post, String address){
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
    };

    public T mapLocationFromAddress(T post){
        AutoCompleteDTO autoCompleteDTO=locationHelper.getLocation(post.getAddress());
        if(!Objects.isNull(autoCompleteDTO)){
            post.setLat(Double.parseDouble(autoCompleteDTO.getLat()));
            post.setLng(Double.parseDouble(autoCompleteDTO.getLng()));
        }
        return post;
    }

    public Float mapSquare(T post, String squareText, Character sperator){
        Float square=convertSquareTextToNumber(squareText, sperator);
        return square;
    };

    public T mapPrice(T post, String priceText, Character sperator){
        post.setPrice(convertPriceTextToNumber(priceText , post.getSquare(),sperator));
        return post;
    };

    public PostAuthorDTO mapAuthor(String name, String phone){
        PostAuthorDTO postAuthorDTO=new PostAuthorDTO();
        postAuthorDTO.setName(name);
        postAuthorDTO.setPhone(categoryMapper.mapPhoneNumber(phone));
        return postAuthorDTO;
    };

    public T mapImagesList(T post, Elements elements, String baseUrl){
        List <String> listImage=new ArrayList<>();
        for (Element element : elements) {
            String imgItem = baseUrl + element.attr("src");
            listImage.add(imgItem);
        }
        post.setImageUrls(listImage);
        return post;
    }

    public T mapThumbnail(T post) {
        try {
            if(!Objects.isNull(post.getImageUrls())&&post.getImageUrls().size()>0){
                post.setThumbnailUrl(post.getImageUrls().get(0));
                post.getImageUrls().remove(0);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return post;
    }

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }
    @Autowired
    public void setLocationHelper(LocationHelper locationHelper) {this.locationHelper = locationHelper;}
}
