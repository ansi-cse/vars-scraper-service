package com.resdii.vars.services.postService.postFactory;

import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.services.postService.aloNhaDatService.AloNhaDatBDSDetailScraperServiceTemplateImpl;
import com.resdii.vars.services.postService.bdsComService.BDSComDetailServiceScraperTemplateImpl;
import com.resdii.vars.services.postService.muaBanService.MuaBanDetailServiceScraperTemplateImpl;
import com.resdii.vars.services.postService.nhaTotService.NhaTotDetailServiceScraperTemplateImpl;
import com.resdii.vars.services.postService.BDSDetailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class WebDetailPageScraperFactory {
    private AloNhaDatBDSDetailScraperServiceTemplateImpl aloNhaDatWebDetailPageScraperService;
    private BDSComDetailServiceScraperTemplateImpl bdsWebDetailPageScraperService;
    private MuaBanDetailServiceScraperTemplateImpl muaBanDetailPageScraperService;
    private NhaTotDetailServiceScraperTemplateImpl nhaTotDetailPageScraperService;

    public BDSDetailTemplate getWebDetailPageScraper(String baseUrl){
        BDSDetailTemplate webBaseScraper = null;
        switch (baseUrl) {
            case "https://alonhadat.com.vn/":
                webBaseScraper = aloNhaDatWebDetailPageScraperService;
                break;
            case "https://batdongsan.com.vn/":
                webBaseScraper = bdsWebDetailPageScraperService;
                break;
            case "https://muaban.net/":
                webBaseScraper = muaBanDetailPageScraperService;
                break;
            case "https://www.nhatot.com/":
                webBaseScraper = nhaTotDetailPageScraperService;
                break;
        }
        return webBaseScraper;
    }

    @Autowired
    public void setAloNhaDatWebDetailPageScraperService(AloNhaDatBDSDetailScraperServiceTemplateImpl<PostDocument> aloNhaDatWebDetailPageScraperService) {
        this.aloNhaDatWebDetailPageScraperService = aloNhaDatWebDetailPageScraperService;
    }
    @Autowired
    public void setBdsWebDetailPageScraperService(BDSComDetailServiceScraperTemplateImpl<PostDocument> bdsWebDetailPageScraperService) {
        this.bdsWebDetailPageScraperService = bdsWebDetailPageScraperService;
    }
    @Autowired
    public void setMuaBanDetailPageScraperService(MuaBanDetailServiceScraperTemplateImpl<PostDocument> muaBanDetailPageScraperService) {
        this.muaBanDetailPageScraperService = muaBanDetailPageScraperService;
    }
    @Autowired
    public void setNhaTotDetailPageScraperService(NhaTotDetailServiceScraperTemplateImpl<PostDocument> nhaTotDetailPageScraperService) {
        this.nhaTotDetailPageScraperService = nhaTotDetailPageScraperService;
    }
}
