package com.resdii.vars.services.postService;

import com.resdii.vars.enums.PostStatus;
import com.resdii.vars.mapper.PostMapper;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

// Template
public abstract class BDSDetailTemplate<T> extends BDSAssetScraperImpl {
    public PostMapper postMapper;

    public abstract PostStatus preHandleData(Document document);
    public abstract T getRaw(Elements elements, String url, T post);
    public abstract T getTitle(Elements elements, T post);
    public abstract T getDescription(Elements elements, T post);
    public abstract T getSquare(Elements elements, T post);
    public abstract T getPrice(Elements elements, T post);
    public abstract T getAddress(Elements elements, T post);
    public abstract T getDate(Elements elements, T post);
    public abstract T getTypeOfRealEstate(Elements elements, T post, Integer command);
    public abstract T getExtraInformation(Elements elements, T post, Integer command);
    public abstract T getAuthorInformation(Elements elements, T post);
    public abstract T getLocationFromAddress(Elements address, T post);
    public abstract T getListImages(Elements elements, T post, String baseUrl);
    public abstract T getThumbnail(Elements elements, T post, String baseUrl);

    public void setPostMapper(PostMapper postMapper) {
        this.postMapper = postMapper;
    }
}
