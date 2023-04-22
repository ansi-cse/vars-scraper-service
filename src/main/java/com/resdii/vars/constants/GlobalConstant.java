package com.resdii.vars.constants;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class GlobalConstant {

    private GlobalConstant() {}

    public static final int DEFAULT_PAGE = 1;

    public static final int DEFAULT_PAGE_SIZE = 10;

    // ScraperAPI constant
    public static final String hitCurrentPlan="You've hit the request limit for your current plan";

    public static final String failedLoadPage="Failed to load page";

    public static final String notExist="Nội dung bạn định xem không tồn tại";

    public static final ImmutableList<String> cityListWithoutPrefix = ImmutableList.of("Đà Nẵng", "Hà Nội", "Hồ Chí Minh", "Huế");
    public static final ImmutableList<String> cityListWithPrefix = ImmutableList.of("Hải Phòng", "Cần Thơ");
    public static final ImmutableList<String> cityListWithPrefixTP = ImmutableList.of("Hồ Chí Minh");

    public static final Map<String, String> commandMapToLinkStringPrefix =
            ImmutableMap.<String, String>builder()
                    .put("https://alonhadat.com.vn/bds-ban", "nha-dat/can-ban")
                    .put("https://alonhadat.com.vn/bds-cho-thue", "nha-dat/cho-thue")
                    .put("https://batdongsan.com.vn/bds-ban", "nha-dat-ban")
                    .put("https://batdongsan.com.vn/bds-cho-thue", "nha-dat-cho-thue")
                    .put("https://batdongsan.com.vn/du-an", "du-an-bat-dong-san")
                    .put("https://muaban.net/bds-ban","bat-dong-san/ban-nha-dat-chung-cu")
                    .put("https://muaban.net/bds-cho-thue","bat-dong-san/cho-thue-nha-dat")
                    .build();
    public static final Map<Integer, String> commandMapToPostType =
            ImmutableMap.of(
                    0, "bds-ban",
                    1, "bds-cho-thue",
                    2, "du-an"
            );
    public static final Map<String, String> baseUrlToPrefix =
            ImmutableMap.of(
                    "https://alonhadat.com.vn/", "ALO",
                    "https://batdongsan.com.vn/", "BDS",
                    "https://muaban.net/", "MUABAN",
                    "https://www.nhatot.com/", "NHATOT"
            );
    public static final Map<String, String> baseUrlToClassNameForGetLinks =
            ImmutableMap.of(
                    "https://alonhadat.com.vn/", ".ct_title",
                    "https://batdongsan.com.vn/", ".re__card-full",
                    "https://muaban.net/", "muaban",
                    "https://www.nhatot.com/",".ListAds_ListAds__rEu_9.col-xs-12.no-padding"
            );
}
