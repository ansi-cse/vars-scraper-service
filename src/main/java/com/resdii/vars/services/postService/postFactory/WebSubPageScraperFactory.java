package com.resdii.vars.services.postService.postFactory;

import com.resdii.vars.services.postService.BDSWebSubPageScraperImpl;
import com.resdii.vars.services.postService.aloNhaDatService.AloNhaDatSubPageScraperServiceBDSImpl;
import com.resdii.vars.services.postService.bdsComService.BDSSubPageScraperServiceBDSImpl;
import com.resdii.vars.services.postService.muaBanService.MuaBanSubPageScraperServiceBDSImpl;
import com.resdii.vars.services.postService.nhaTotService.NhaTotSubPageScraperServiceBDSImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class WebSubPageScraperFactory {
    AloNhaDatSubPageScraperServiceBDSImpl aloNhaDatSubPageScraperService;
    BDSSubPageScraperServiceBDSImpl bdsSubPageScraperService;
    MuaBanSubPageScraperServiceBDSImpl muaBanSubPageScraperService;
    NhaTotSubPageScraperServiceBDSImpl nhaTotSubPageScraperService;

    public BDSWebSubPageScraperImpl getSubPageScraper(String baseUrl){
        BDSWebSubPageScraperImpl webSubPageScraper = null;
        switch (baseUrl) {
            case "https://alonhadat.com.vn/":
                webSubPageScraper = aloNhaDatSubPageScraperService;
                break;
            case "https://batdongsan.com.vn/":
                webSubPageScraper = bdsSubPageScraperService;
                break;
            case "https://muaban.net/":
                webSubPageScraper = muaBanSubPageScraperService;
                break;
            case "https://www.nhatot.com/":
                webSubPageScraper = nhaTotSubPageScraperService;
                break;
        }
        return webSubPageScraper;
    }

    @Autowired
    public void setAloNhaDatSubPageScraperService(AloNhaDatSubPageScraperServiceBDSImpl aloNhaDatSubPageScraperService) {this.aloNhaDatSubPageScraperService = aloNhaDatSubPageScraperService;}
    @Autowired
    public void setBdsSubPageScraperService(BDSSubPageScraperServiceBDSImpl bdsSubPageScraperService) {this.bdsSubPageScraperService = bdsSubPageScraperService;}
    @Autowired
    public void setMuaBanSubPageScraperService(MuaBanSubPageScraperServiceBDSImpl muaBanSubPageScraperService) {this.muaBanSubPageScraperService = muaBanSubPageScraperService;}
    @Autowired
    public void setNhaTotSubPageScraperService(NhaTotSubPageScraperServiceBDSImpl nhaTotSubPageScraperService) {this.nhaTotSubPageScraperService = nhaTotSubPageScraperService;}
}
