package com.resdii.vars.services.postService.nhaTotService;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.resdii.vars.utils.CommonUtils.*;

@Service
public class NhaTotDetailServiceScraperTemplate<T extends PostDocument> extends BDSDetailTemplate<T> {

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
        postAuthorDTO.setName(docElements.select(".SellerProfile_flexDiv__IEgQl").attr("role","presentation").text());
        post.setAuthor(postAuthorDTO);
        post.setPriority(categoryMapper.mapPriority("1"));
        post.setWalletId(loginHelper.getUserInfoDTO().getWallet().getWalletId());
        return post;
    }

    @Override
    public T getTitle(Elements docElements, T post) {
        Elements elements = docElements.select("span[itemprop=name]");
        String title=elements.text();
        post.setTitle(title);
        return post;
    }
    @Override
    public T getDescription(Elements docElements, T post) {
        Elements elements = docElements.select(".styles_adBody__vGW74");
        if(elements.text().equals("")){
            post.setDescription(post.getTitle());
        }
        post.setDescription(elements.html());
        return post;
    }
    @Override
    public T getSquare(Elements docElements, T post) {
        Elements elements = docElements.select("span[itemprop=size]");
        Float square=convertSquareTextToNumber(elements.text(), '.');
        post.setSquare(square);
        return post;
    }
    @Override
    public T getListImages(Elements docElements, T post, String baseUrl) {
        Elements elements = docElements.select("slick-track").select("img");
        List<MultipartFile> listImage=new ArrayList<>();
        for (Element element : elements) {
            String listImg = element.attr("src");
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
        Elements elements = docElements.select("span[itemprop=price_m2]");
        post.setPrice(convertPriceTextToNumber(elements.text(), post.getSquare(), ',' ));
        return post;
    }
    @Override
    public T getAddress(Elements elements, T post) {
        return null;
    }
    @Override
    public T getDate(Elements elements, T post) {
        return null;
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
        post.setFrontWidth(Float.parseFloat(docElements.select("span[itemprop=width]").text()));
        post.setBathroom(Integer.parseInt(docElements.select("span[itemprop=toilets]").text()));
        post.setFloor(Integer.parseInt(docElements.select("span[itemprop=floors]").text()));
        post.setBedroom(Integer.parseInt(docElements.select("span[itemprop=rooms]").text()));
        post.setLegalDoc(categoryMapper.mapLegalDoc(docElements.select("span[itemprop=property_legal_document]").text()));
//        postRequestDTO.setDirection(categoryMapper.mapDirection(docElements.select("span[itemprop=balconydirection]").text()));
        post.setFurniture(Arrays.asList(docElements.select("span[itemprop=furnishing_sell]").text().split(",")));
        return post;
    }

    @Override
    public T getTypeOfRealEstate(Elements docElements, T post, String postType) {
        Elements land_type = docElements.select("span[itemprop=land_type]");
        post.setTypeRealEstate(categoryMapper.mapRealEstateType("ban-Phòng trọ, nhà trọ", postType));
        if(Objects.isNull(land_type)){
            post.setTypeRealEstate(categoryMapper.mapRealEstateType(land_type.text(), postType));
        }
        Elements apartment_type = docElements.select("span[itemprop=apartment_type]");
        if(Objects.isNull(apartment_type)){
            post.setTypeRealEstate(categoryMapper.mapRealEstateType(land_type.text(), postType));
        }
        Elements commercial_type = docElements.select("span[itemprop=commercial_type]");
        if(Objects.isNull(commercial_type)){
            post.setTypeRealEstate(categoryMapper.mapRealEstateType(land_type.text(), postType));
        }
        Elements house_type = docElements.select("span[itemprop=house_type]");
        if(Objects.isNull(house_type)){
            post.setTypeRealEstate(categoryMapper.mapRealEstateType(land_type.text(), postType));
        }
        return post;
    }
}
