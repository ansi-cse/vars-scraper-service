package com.resdii.vars.services.postService.muaBanService;

import com.google.common.collect.ImmutableMap;
import com.resdii.vars.api.LinksApiClient;
import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.PagingDTO;
import com.resdii.vars.dto.PagingItemDTO;
import com.resdii.vars.services.postService.BDSWebSubPageScraperImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class MuaBanSubPageScraperServiceBDSImpl extends BDSWebSubPageScraperImpl {
    private LinksApiClient linksApiClient;
    private final int limit=20;
    private final Map<Integer, Integer> mapCommandToSubCategoryId= ImmutableMap.of(
            0, 169,
            1, 46);
    private final int categoryId=33;

    public MuaBanSubPageScraperServiceBDSImpl() {
        setBaseUrl("https://muaban.net/");
    }

    @PostConstruct
    public void postConstructor(){
        setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceScraperApiImpl.class));
    }

    @Override
    public void getLinks(String postType, String baseUrl, int numOfPage) {
        String prefix = GlobalConstant.baseUrlToPrefix.get(baseUrl);
        int offset=0;
        while (true){
            PagingDTO pagingDTO= linksApiClient.getLinksByPageIndex(mapCommandToSubCategoryId.get(postType), categoryId, limit, offset).getBody();
            List<PagingItemDTO> listItems=pagingDTO.getItems();
            if(listItems.size()==0){
                break;
            }
            List<String> listLinks=listItems.stream().map(ele->baseUrl+ele.getUrl()).collect(Collectors.toList());
            offset=offset+limit;
            savedDetailLink(listLinks, postType, baseUrl, prefix);
        }
//        getLinksByPostTypeFailed(postType, baseUrl, prefix);
    }
    @Autowired
    public void setLinksClient(LinksApiClient linksApiClient) {
        this.linksApiClient = linksApiClient;
    }
}
