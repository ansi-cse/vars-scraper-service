package com.resdii.vars.mapper;

import com.resdii.ms.common.category.AddressItem;
import com.resdii.ms.common.category.Category;
import com.resdii.ms.common.category.NoodevCategory;
import com.resdii.ms.common.utils.AddressUtils;
import com.resdii.ms.common.utils.StringUtils;
import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.CategoryDTO;
import com.resdii.vars.api.CategoryApiClient;
import com.resdii.vars.enums.PostType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CategoryMapper {
    private Map<String, Category> postTypeCat;
    private Map<String, Category> realEstateTypeCat;
    private Map<String, Category> legalDocCat;
    private Map<String, Category> entranceCat;
    private Map<String, Category> rePostByCat;
    private Map<String, Category> postPriorityCat;
    private Map<String, Category> postDirectionCat;
    private Map<String, CategoryDTO> investor;
    private Map<String, CategoryDTO> projectType;

    public void initCategories(){
        Map<String, Category> postType=NoodevCategory.getInstance().getPostType();

        postTypeCat=new HashMap<>();
        postTypeCat.put(PostType.BDS_SALE.name(), postType.get(GlobalConstant.commandMapToPostType.get(PostType.BDS_SALE.name())));
        postTypeCat.put(PostType.BDS_RENT.name(), postType.get(GlobalConstant.commandMapToPostType.get(PostType.BDS_RENT.name())));

        Map<String, Category> realEstateType = NoodevCategory.getInstance().getRealEstateType();
        realEstateTypeCat=new HashMap<>();
        // Alo nha dat
        realEstateTypeCat.put("bds-ban-Biệt thự, nhà liền kề", realEstateType.get("109")); // BĐS Biệt thự nghỉ dưỡng
        realEstateTypeCat.put("bds-ban-Nhà trong hẻm", realEstateType.get("112")); // BĐS Nhà trong hẻm
        realEstateTypeCat.put("bds-ban-Nhà mặt tiền", realEstateType.get("6")); // BĐS Nhà mặt phố
        realEstateTypeCat.put("bds-ban-Căn hộ chung cư", realEstateType.get("18")); // BĐS Căn hộ/Chung cư
        realEstateTypeCat.put("bds-ban-Phòng trọ, nhà trọ", realEstateType.get("62")); // BĐS khác
        realEstateTypeCat.put("bds-ban-Văn phòng", realEstateType.get("62")); // BĐS khác
        realEstateTypeCat.put("bds-ban-Kho, xưởng", realEstateType.get("61")); // BĐS Nhà xưởng/nhà kho
        realEstateTypeCat.put("bds-ban-Nhà hàng, khách sạn", realEstateType.get("22")); // Khách sạn
        realEstateTypeCat.put("bds-ban-Shop, kiot, quán", realEstateType.get("62")); // Bất động sản khác
        realEstateTypeCat.put("bds-ban-Trang trại", realEstateType.get("60")); // BĐS Trang trại/Nhà vườn
        realEstateTypeCat.put("bds-ban-Mặt bằng", realEstateType.get("62")); // Bất động sản khác
        realEstateTypeCat.put("bds-ban-Đất thổ cư, đất ở", realEstateType.get("110")); // BĐS Đất nền riêng lẻ
        realEstateTypeCat.put("bds-ban-Đất nền, liền kề, đất dự án", realEstateType.get("21")); // BĐS Đất nền Dự án
        realEstateTypeCat.put("bds-ban-Đất nông, lâm nghiệp", realEstateType.get("62")); //  Bất động sản khác
        realEstateTypeCat.put("bds-ban-Các loại khác", realEstateType.get("62"));  // Bất động sản khác
        realEstateTypeCat.put("bds-ban----", realEstateType.get("62")); // Bất động sản khác

        realEstateTypeCat.put("bds-cho-thue-Biệt thự, nhà liền kề", realEstateType.get("85")); //  Biệt thự, nhà liền kề
        realEstateTypeCat.put("bds-cho-thue-Nhà trong hẻm", realEstateType.get("30")); // BĐS khác
        realEstateTypeCat.put("bds-cho-thue-Nhà mặt tiền", realEstateType.get("15")); // BĐS Nhà mặt phố
        realEstateTypeCat.put("bds-cho-thue-Căn hộ chung cư", realEstateType.get("40")); // BĐS Căn hộ/Chung cư
        realEstateTypeCat.put("bds-cho-thue-Phòng trọ, nhà trọ", realEstateType.get("26")); // BĐS Nhà trọ/Phòng trọ
        realEstateTypeCat.put("bds-cho-thue-Văn phòng", realEstateType.get("27")); // BĐS Văn phòng/Toà nhà văn phòng
        realEstateTypeCat.put("bds-cho-thue-Kho, xưởng", realEstateType.get("115")); // BĐS Nhà xưởng/nhà kho
        realEstateTypeCat.put("bds-cho-thue-Nhà hàng, khách sạn", realEstateType.get("30")); // BĐS khác
        realEstateTypeCat.put("bds-cho-thue-Shop, kiot, quán", realEstateType.get("30")); //  BĐS khác
        realEstateTypeCat.put("bds-cho-thue-Trang trại", realEstateType.get("116")); // BĐS Trang trại/Nhà vườn
        realEstateTypeCat.put("bds-cho-thue-Mặt bằng", realEstateType.get("121")); // BĐS Đất nền riêng lẻ
        realEstateTypeCat.put("bds-cho-thue-Đất thổ cư, đất ở", realEstateType.get("121")); // BĐS Đất nền riêng lẻ
        realEstateTypeCat.put("bds-cho-thue-Đất nền, liền kề, đất dự án", realEstateType.get("120")); // BĐS Đất nền Dự án
        realEstateTypeCat.put("bds-cho-thue-Đất nông, lâm nghiệp", realEstateType.get("30")); // BĐS khác
        realEstateTypeCat.put("bds-cho-thue-Các loại khác", realEstateType.get("30")); // Bất động sản khác
        realEstateTypeCat.put("bds-cho-thue----", realEstateType.get("30")); // Bất động sản khác

        // bds.com
        realEstateTypeCat.put("bds-ban-Bán căn hộ chung cư", realEstateType.get("18")); // BĐS Căn hộ/Chung cư
        realEstateTypeCat.put("bds-ban-Bán nhà riêng", realEstateType.get("6")); // BĐS Nhà mặt phố
        realEstateTypeCat.put("bds-ban-Bán nhà biệt thự, liền kề", realEstateType.get("109")); // BĐS Biệt thự nghỉ dưỡng
        realEstateTypeCat.put("bds-ban-Bán nhà mặt phố", realEstateType.get("6")); // BĐS Nhà mặt phố
        realEstateTypeCat.put("bds-ban-Bán shophouse, nhà phố thương mại", realEstateType.get("107")); // BĐS Shophouse
        realEstateTypeCat.put("bds-ban-Bán đất nền dự án", realEstateType.get("21")); // BĐS Đất nền Dự án
        realEstateTypeCat.put("bds-ban-Bán đất", realEstateType.get("110")); // BĐS Đất nền riêng lẻ
        realEstateTypeCat.put("bds-ban-Bán trang trại, khu nghỉ dưỡng", realEstateType.get("60")); // BĐS Trang trại/Nhà vườn
        realEstateTypeCat.put("bds-ban-Bán condotel", realEstateType.get("19")); // BĐS Condotel
        realEstateTypeCat.put("bds-ban-Bán kho, nhà xưởng", realEstateType.get("61")); // BĐS Nhà xưởng/nhà kho
        realEstateTypeCat.put("bds-ban-Bán loại bất động sản khác", realEstateType.get("62")); // BĐS khác

        realEstateTypeCat.put("bds-cho-thue-Cho thuê căn hộ chung cư", realEstateType.get("40")); // BĐS Căn hộ/Chung cư
        realEstateTypeCat.put("bds-cho-thue-Cho thuê nhà riêng", realEstateType.get("15")); // BĐS Nhà mặt phố
        realEstateTypeCat.put("bds-cho-thue-Cho thuê nhà biệt thự, liền kề", realEstateType.get("85")); // Biệt thự, nhà liền kề
        realEstateTypeCat.put("bds-cho-thue-Cho thuê nhà mặt phố", realEstateType.get("15")); // BĐS Nhà mặt phố
        realEstateTypeCat.put("bds-cho-thue-Cho thuê shophouse, nhà phố thương mại", realEstateType.get("117")); // BĐS Shophouse
        realEstateTypeCat.put("bds-cho-thue-Cho thuê nhà trọ, phòng trọ", realEstateType.get("26")); // BĐS Nhà trọ/Phòng trọ
        realEstateTypeCat.put("bds-cho-thue-Cho thuê văn phòng", realEstateType.get("27")); // BĐS Văn phòng/Toà nhà văn phòng
        realEstateTypeCat.put("bds-cho-thue-Cho thuê, sang nhượng cửa hàng, ki ốt", realEstateType.get("30")); // Bất động sản khác
        realEstateTypeCat.put("bds-cho-thue-Cho thuê kho, nhà xưởng, đất", realEstateType.get("115")); // BĐS Nhà xưởng/nhà kho
        realEstateTypeCat.put("bds-cho-thue-Cho thuê loại bất động sản khác", realEstateType.get("30")); // Bất động sản khác

        // M&A
        realEstateTypeCat.put("M&A-Đất nông, lâm nghiệp", realEstateType.get("90")); // M&A Dự án khác
        realEstateTypeCat.put("M&A-Nhà hàng, khách sạn", realEstateType.get("100")); // M&A Dự án Khách sạn
        realEstateTypeCat.put("M&A-Văn phòng", realEstateType.get("94")); // M&A Dự án Toà nhà văn phòng
        realEstateTypeCat.put("M&A-Căn hộ chung cư", realEstateType.get("103")); // M&A Dự án Căn hộ/Chung cư
        realEstateTypeCat.put("M&A-Mặt bằng", realEstateType.get("90")); // M&A Dự án khác
        realEstateTypeCat.put("M&A-Kho, xưởng", realEstateType.get("90")); // M&A Dự án khác
        realEstateTypeCat.put("M&A-Đất nền, liền kề, đất dự án", realEstateType.get("102")); // M&A Dự án Đất nền
        realEstateTypeCat.put("M&A-Các loại khác", realEstateType.get("90")); // M&A Dự án khác
        realEstateTypeCat.put("M&A-Trang trại", realEstateType.get("90")); // M&A Dự án khác
        realEstateTypeCat.put("M&A-Biệt thự, nhà liền kề", realEstateType.get("98")); // M&A Dự án Biệt thự nghỉ dưỡng
//        mua ban
////        realEstateTypeCat.put("ban-Nhà mặt tiền", realEstateType.get("6")); // Nhà mặt phố
//        realEstateTypeCat.put("bds-ban-Nhà hẻm, ngõ", realEstateType.get("7")); // Nhà mặt phố
//        realEstateTypeCat.put("bds-ban-Biệt thự, Villa", realEstateType.get("17")); // Biệt thự, liền kề
//        realEstateTypeCat.put("bds-ban-Chung cư", realEstateType.get("18")); // Căn hộ, chung cư
//        realEstateTypeCat.put("bds-ban-Penthouse", realEstateType.get("18")); // Căn hộ, chung cư
//        realEstateTypeCat.put("bds-ban-Căn hộ dịch vụ, mini", realEstateType.get("18")); // Căn hộ, chung cư
//        realEstateTypeCat.put("bds-ban-Tập thể, cư xá", realEstateType.get("18")); // Căn hộ, chung cư
//        realEstateTypeCat.put("bds-ban-Officetel", realEstateType.get("18")); // Căn hộ, chung cư
//        realEstateTypeCat.put("bds-ban-Đất nông nghiệp, kho bãi", realEstateType.get("20")); // Đất
//        realEstateTypeCat.put("bds-ban-Đất dự án, Khu dân cư", realEstateType.get("21")); // Đất nền dự án
//        realEstateTypeCat.put("bds-ban-Đất thổ cư", realEstateType.get("20")); // Đất
//
//        realEstateTypeCat.put("bds-cho-thue-Biệt thự, Villa", realEstateType.get("14")); // Bất động sản khác
////        realEstateTypeCat.put("bds-cho-thue-Nhà mặt tiền", realEstateType.get("30")); // Bất động sản khác
//        realEstateTypeCat.put("bds-cho-thue-Nhà hẻm, ngõ", realEstateType.get("14")); // Bất động sản khác
//        realEstateTypeCat.put("bds-cho-thue-Chung cư", realEstateType.get("40")); // Bất động sản khác
//        realEstateTypeCat.put("bds-cho-thue-Penthouse", realEstateType.get("40")); // Bất động sản khác
//        realEstateTypeCat.put("bds-cho-thue-Khách sạn, Căn hộ dịch vụ", realEstateType.get("40")); // Bất động sản khác
//        realEstateTypeCat.put("bds-cho-thue-Tập thể, cư xá", realEstateType.get("40")); // Bất động sản khác
//        realEstateTypeCat.put("bds-cho-thue-Nhà trọ, Phòng trọ", realEstateType.get("26")); // Nhà trọ, phòng trọ thuê
////        realEstateTypeCat.put("bds-cho-thue-Văn phòng", realEstateType.get("30")); // Bất động sản khác
//        realEstateTypeCat.put("bds-cho-thue-Mặt bằng kinh doanh", realEstateType.get("87")); // Mặt bằng
//        realEstateTypeCat.put("bds-cho-thue-Cửa hàng, shophouse", realEstateType.get("28")); // Cửa hàng, kiot
//        realEstateTypeCat.put("bds-cho-thue-Officetel", realEstateType.get("30")); // Bất động sản khác
//        realEstateTypeCat.put("bds-cho-thue-Nhà xưởng, nhà kho", realEstateType.get("29")); // Kho, nhà xưởng, đất, nhà hàng
//        realEstateTypeCat.put("bds-cho-thue-Bãi để xe", realEstateType.get("30")); // Bất động sản khác
//        realEstateTypeCat.put("bds-cho-thue-Đất trống", realEstateType.get("86")); // Kho, nhà xưởng, đất, nhà hàng

//        nha tot
//        realEstateTypeCat.put("bds-ban-Nhà mặt phố, mặt tiền", realEstateType.get("6"));
//        realEstateTypeCat.put("bds-ban-Nhà ngõ, hẻm", realEstateType.get("7"));
//        realEstateTypeCat.put("bds-ban-Nhà biệt thự", realEstateType.get("17"));
//        realEstateTypeCat.put("bds-ban-Nhà phố liền kề", realEstateType.get("6"));
//        realEstateTypeCat.put("bds-ban-Chung cư", realEstateType.get("18"));
//        realEstateTypeCat.put("bds-ban-Duplex", realEstateType.get("18"));
//        realEstateTypeCat.put("bds-ban-Penthouse", realEstateType.get("18"));
//        realEstateTypeCat.put("bds-ban-Căn hộ dịch vụ, mini", realEstateType.get("18"));
//        realEstateTypeCat.put("bds-ban-Tập thể, cư xá", realEstateType.get("18"));
//        realEstateTypeCat.put("bds-ban-Officetel", realEstateType.get("18"));
//        realEstateTypeCat.put("bds-ban-Đất thổ cư", realEstateType.get("20"));
//        realEstateTypeCat.put("bds-ban-Đất nền dự án", realEstateType.get("21"));
//        realEstateTypeCat.put("bds-ban-Đất công nghiệp", realEstateType.get("20"));
//        realEstateTypeCat.put("bds-ban-Đất nông nghiệp", realEstateType.get("20"));
//        realEstateTypeCat.put("bds-ban-Mặt bằng kinh doanh", realEstateType.get("20"));
//        realEstateTypeCat.put("bds-ban-Văn phòng", realEstateType.get("62"));
//        realEstateTypeCat.put("bds-ban-Shophouse", realEstateType.get("62"));
//        realEstateTypeCat.put("bds-ban-Officetel", realEstateType.get("18"));
//
//        realEstateTypeCat.put("bds-cho-thue-Nhà mặt phố, mặt tiền", realEstateType.get("15"));
//        realEstateTypeCat.put("bds-cho-thue-Nhà ngõ, hẻm", realEstateType.get("14"));
//        realEstateTypeCat.put("bds-cho-thue-Nhà biệt thự", realEstateType.get("85"));
//        realEstateTypeCat.put("bds-cho-thue-Nhà phố liền kề", realEstateType.get("15"));
//        realEstateTypeCat.put("bds-cho-thue-Chung cư", realEstateType.get("40"));
//        realEstateTypeCat.put("bds-cho-thue-Duplex", realEstateType.get("40"));
//        realEstateTypeCat.put("bds-cho-thue-Penthouse", realEstateType.get("40"));
//        realEstateTypeCat.put("bds-cho-thue-Căn hộ dịch vụ, mini", realEstateType.get("40"));
//        realEstateTypeCat.put("bds-cho-thue-Tập thể, cư xá", realEstateType.get("40"));
//        realEstateTypeCat.put("bds-cho-thue-Officetel", realEstateType.get("40"));
//        realEstateTypeCat.put("bds-cho-thue-Đất thổ cư", realEstateType.get("86"));
//        realEstateTypeCat.put("bds-cho-thue-Đất nền dự án", realEstateType.get("86"));
//        realEstateTypeCat.put("bds-cho-thue-Đất công nghiệp", realEstateType.get("86"));
//        realEstateTypeCat.put("bds-cho-thue-Đất nông nghiệp", realEstateType.get("86"));
//        realEstateTypeCat.put("bds-cho-thue-Mặt bằng kinh doanh", realEstateType.get("87"));
//        realEstateTypeCat.put("bds-cho-thue-Văn phòng", realEstateType.get("30"));
//        realEstateTypeCat.put("bds-cho-thue-Shophouse", realEstateType.get("30"));
//        realEstateTypeCat.put("bds-cho-thue-Officetel", realEstateType.get("30"));

        Map<String, Category>  legalDoc= NoodevCategory.getInstance().getLegalDocs();
        legalDocCat=new HashMap<>();
        legalDocCat.put("Sổ hồng/ Sổ đỏ", legalDoc.get("Sổ hồng"));
        legalDocCat.put("Sổ đỏ/ Sổ hồng", legalDoc.get("Sổ hồng"));
        legalDocCat.put("Sổ hồng", legalDoc.get("Sổ hồng"));
        legalDocCat.put("Sổ đỏ", legalDoc.get("Sổ đỏ"));
        legalDocCat.put("Đã có sổ",legalDoc.get("Sổ hồng"));
        legalDocCat.put("Đã có sổ riêng",legalDoc.get("Sổ hồng"));
        legalDocCat.put("Giấy tờ hợp lệ", legalDoc.get("Giấy tờ khác"));
        legalDocCat.put("Giấy phép XD", legalDoc.get("Giấy tờ khác"));
        legalDocCat.put("Giấy phép KD", legalDoc.get("Giấy tờ khác"));
        legalDocCat.put("---", legalDoc.get("Giấy tờ khác"));

        entranceCat =NoodevCategory.getInstance().getEntrance();

        Map<String, Category> rePostBy = NoodevCategory.getInstance().getRePostBy();
        rePostByCat =new HashMap<>();
        rePostByCat.put("Môi giới", rePostBy.get("1"));
        rePostByCat.put("Chính chủ", rePostBy.get("2"));

        postPriorityCat=NoodevCategory.getInstance().getPostPriority();

        postDirectionCat= NoodevCategory.getInstance().getDirection();

//        Map<String, String> categoryRequest=new HashMap<>();
//        categoryRequest.put("post_type","du-an");
//        List<CategoryDTO> listProjectType= categoryFeign.getCategories(categoryRequest).getBody();
//        List<CategoryDTO> listInvestor= categoryFeign.getListInvestor().getBody();
//        Map<String, CategoryDTO> mapProjectType=new HashMap<>();
//        listProjectType.forEach(ele-> mapProjectType.put(ele.getName(), ele));
//        setProjectType(mapProjectType);
//
//        Map<String, CategoryDTO> mapInvestor=new HashMap<>();
//        listInvestor.forEach(ele-> mapInvestor.put(ele.getName(), ele));
//        setInvestor(mapInvestor);
    }
    public Category mapPostType(String postType){
        return postTypeCat.get(postType);
    }
    public Category mapRealEstateType(String name, String postType){
        return realEstateTypeCat.get(GlobalConstant.commandMapToPostType.get(postType)+"-"+name);
    }
    public Category mapRealEstateType(String name){
        return realEstateTypeCat.get(name);
    }
    public Category mapLegalDoc(String name){
        return legalDocCat.get(name);
    }
    public Category mapEntrance(String name, Character sperator){
        try{
            String entranceNumber=name.replace("m", "");
            String entranceTrim=sperator.equals(',')?
                    entranceNumber.replaceAll("\\.","").replace(",", ".")
                    : entranceNumber.replace(",", "");
            Double entrance= Double.valueOf(entranceTrim);
            if(0<entrance && entrance<=3){
                Category category=entranceCat.get("Ngõ hẻm");
                return category;
            }
            if(3<entrance && entrance<=5){
                Category category= entranceCat.get("Ngõ 1 ô Tô");
                return  category;
            }
            if(5<entrance && entrance<=7){
                return entranceCat.get("Ngõ 2 ô tô tránh");
            }
            if(7<entrance && entrance<=10){
                return entranceCat.get("Ngõ 2 ô tô tránh trở lên");
            }
            if(10<entrance){
                return entranceCat.get("Mặt phố - Mặt đường");
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }
    public Category mapRePostBy(String name){
        // Éo biết chắc name là cái j, check cho chéc :D
        if(name.equals("Môi giới")){
            return rePostByCat.get(name);
        }
        if(name.equals("Chính chủ")){
            return rePostByCat.get(name);
        }

        if(name.equals("---")){
            name="Môi giới";
        }else{
            name="Chính chủ";
        }
        return rePostByCat.get(name);
    }
    public Category mapPriority(String name){
        return postPriorityCat.get(name);
    }
    public Category mapDirection(String name){
        return postDirectionCat.get(name.replace("- ",""));
    }
    public Category mapNewAddress(String province, String district){
        if(province.equals("TP Hồ Chí Minh") && district.equals("Quận 2")){
            AddressItem addressItem=AddressUtils.getDistrictByName(province, "Thành phố Thủ Đức");
            return new Category(addressItem.getCode(), addressItem.getName());
        }
        if(province.equals("TP Hồ Chí Minh") && district.equals("Quận 9")){
            AddressItem addressItem=AddressUtils.getDistrictByName(province, "Thành phố Thủ Đức");
            return new Category(addressItem.getCode(), addressItem.getName());
        }
        return null;
    };
    public String mapNewProvince(String provinceText){
        if(provinceText.equals("TP.HCM")){
            provinceText="Hồ Chí Minh";
        }
        if(provinceText.equals("Đăk Nông")){
            provinceText="Đắk Nông";
        }
        if(provinceText.equals("Bà Rịa Vũng Tàu")){
            provinceText="Bà Rịa - Vũng Tàu";
        }
        if(provinceText.equals("Huế")){
            provinceText="Thừa Thiên Huế";
        }
        return provinceText;
    }
    public String mapProjectType(String name){
        switch (name){
            case "Căn hộ chung cư":
                return "căn hộ, chung cư";
            case "Cao ốc văn phòng":
                return "cao ốc văn phòng";
            case "Trung tâm thương mại":
                return "trung tâm thương mại";
            case "Khu đô thị mới":
                return "khu đô thị mới";
            case "Khu phức hợp":
                return "khu phức hợp";
            case "Nhà ở xã hội":
                return "nhà ở xã hội";
            case "Khu nghỉ dưỡng, Sinh thái":
                return "khu nghỉ dưỡng, sinh thái";
            case "Khu công nghiệp":
                return "khu công nghiệp";
            case "Biệt thự, liền kề":
                return "biệt thự liền kề";
            case "Nhà mặt phố":
                return "nhà mặt phố";
            default:
                return "dự án khác";
        }
    }
    public String mapPhoneNumber(String phoneNumber){
        return phoneNumber.replace(".", "").replaceAll(" ", "").trim();
    }

    public int getProjectTypeIdByName(String name){
        return projectType.get(mapProjectType(name)).getId();
    }
    public void setInvestor(Map<String, CategoryDTO> investor) {
        this.investor = investor;
    }
    public void setProjectType(Map<String, CategoryDTO> projectType) {
        this.projectType = projectType;
    }
}
