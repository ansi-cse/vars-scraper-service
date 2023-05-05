package com.resdii.vars.utils;

import com.resdii.ms.common.category.Category;
import com.resdii.vars.dto.PostAuthorDTO;
import com.resdii.vars.enums.PrefixToken;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

public class CommonUtils {

    private CommonUtils(){
        // Disable Constructor
    }

    public static List<String> findLinks(String html, String cssSelector) {
        try {
            List<String> links = new ArrayList<>();
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(cssSelector);
            for (Element element : elements) {
                String href=element.select("a").attr("href");
                links.add(href);
            }
            return links;
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            return new ArrayList<>();
        }
    }

    @SneakyThrows
    public static MultipartFile getMultipartFile(String fileName) {
        URI uri = URI.create(fileName);
        Path destination = Paths.get(FilenameUtils.getName(fileName));
        HttpURLConnection httpcon = (HttpURLConnection) uri.toURL().openConnection();
        httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");

        Files.copy(httpcon.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        Path path = Paths.get(FilenameUtils.getName(fileName));
        String name = fileName;
        String originalFileName = fileName;
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
            Files.delete(path);
        } catch (final IOException e) {
            System.out.println("getMultipartFile Error: " + e);
        }
        MultipartFile result = new MockMultipartFile(name, originalFileName, MediaType.MULTIPART_FORM_DATA_VALUE, content);
        return result;
    }

    public static void getImage(String url, String destinationFile){
        try{
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));
            URL imagesUrl = new URL(url);
            HttpURLConnection httpcon = (HttpURLConnection) imagesUrl.openConnection();
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");

            OutputStream outputStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[2048];
            int length;
            while ((length = httpcon.getInputStream().read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            httpcon.getInputStream().close();
            outputStream.close();
        }catch (Exception exception){
            throw new RuntimeException(exception);
        }
    }

    public static Float convertSquareTextToNumber(String squareText, Character sperator){
        if(squareText.equals("KXĐ")){
            return Float.parseFloat("0");
        }
        String temp=squareText
                .replaceAll("m²", "")
                .replaceAll("m2", "");
        String squareTextConvert =sperator.equals(',')?temp
                .replaceAll("\\.", "")
                .replaceAll(",",".")
                .replaceAll("---","0")
                .trim()
                :temp.replace(",","").trim();
        if(squareTextConvert.equals("")){
            squareTextConvert="0";
        }
        return Float.parseFloat(squareTextConvert);
    };

    public static Float convertLengthTextToNumber(String squareText, Character sperator){
        String temp= squareText.replaceAll("m", "").trim();
        String convertLengthText = sperator.equals(',') ?temp
                .replaceAll("\\.", "")
                .replaceAll(",",".")
                .replaceAll("---","0").trim()
                :temp.replace(",","").trim();
        if(convertLengthText.equals("")){
            convertLengthText="0";
        }
        return Float.parseFloat(convertLengthText);
    };

    public static Integer convertTextToNumber(String squareText){
        String textConvert =squareText
                .replaceAll("---","0")
                .replaceAll("tầng", "")
                .replaceAll("PN", "")
                .replaceAll("phòng", "")
                .trim();
        if(textConvert.equals("")){
            textConvert="0";
        }
        return Integer.parseInt(textConvert);
    };

    public static Double convertPriceTextToNumber(String priceText, Float square, Character sperator){
        if(priceText.contains("Thỏa thuận")){
            return null;
        }
        priceText=sperator.equals(',')
                ?priceText.replaceAll("\\.","")
                .replaceAll(",", ".")
                : priceText.replace(",","");
        //for muaban.net
        if(priceText.matches("([0-9])+ tỷ ([0-9])+ triệu")){
            String[] priceSplit= priceText.replace(" tỷ", "").replace(" triệu","").trim().split(" ");
            return Double.valueOf(priceSplit[0])*1000000000 + Double.parseDouble(priceSplit[1])*1000000;
        }
        String[] priceSplit=priceText.split(" ");
        Double price=new Double(0);
        // for normal case
        if(priceText.contains("đ")){
            price=Double.valueOf(priceSplit[0]);
        }
        if(priceText.contains("ngàn")){
            price=Double.valueOf(priceSplit[0])*1000;
        }
        if(priceText.contains("triệu")){
            price=Double.valueOf(priceSplit[0])*1000000;
        }
        if(priceText.contains("tỷ")){
            price=Double.valueOf(priceSplit[0])*1000000000;
        }
        if(priceText.contains("m2 / tháng")){
            return price*square;
        }
        return price;
    };

    public static String convertDateTextToDate(String date){
        if(date.equals("Hôm nay")){
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime ldt = LocalDateTime.now();
            return format.format(ldt);
        }
        if(date.equals("Hôm qua")){
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime ldt = LocalDateTime.now().plusDays(-1);
            return format.format(ldt);
        }
        return date;
    }

    public static String addPrefixToken(String token, PrefixToken prefixToken){
        return prefixToken.value+" "+token;
    }

    public static class CallBackForMultithreading implements Supplier<Integer> {
        @SneakyThrows
        @Override
        public Integer get() {
            return 1;
        }
    }
}

