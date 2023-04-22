package com.resdii.vars.mapper;

import com.resdii.vars.dto.PostDocument;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class PostDocumentMapper<T extends PostDocument> extends PostMapper<T> {
    @Override
    public T mapRealEstateType(T post, String name, Integer command) {
        post=super.mapRealEstateType(post,name,command);
        post.setRawRealEstate(name);
        return post;
    }

    @Override
    public T mapLegalDoc(T post, String name) {
        post=super.mapLegalDoc(post,name);
        post.setRawLegalDoc(name);
        return post;
    }

    @Override
    public T mapEntrance(T post, String name, Character sperator) {
        post=super.mapEntrance(post,name,sperator);
        post.setRawEntrance(name);
        return post;
    }

    @Override
    public T mapAddress(T post, String address) {
        post=super.mapAddress(post,address);
        post.setRawAddress(address);
        return post;
    }

    @Override
    public T mapPrice(T post, String priceText, Character sperator) {
        post=super.mapPrice(post, priceText, sperator);
        post.setRawPriceText(priceText);
        return post;
    }

    @Override
    public T mapImagesList(T post, Elements elements, String baseUrl) {
        post=super.mapImagesList(post, elements, baseUrl);
        List<String> urlsList=new ArrayList<>();
        post.getImageUrls().forEach(ele->{
            try {
                String destinationFile ="images/"+UUID.randomUUID()+"-"+FilenameUtils.getName(ele);
                URL url = new URL(ele);
                InputStream inputStream = url.openStream();
                OutputStream outputStream = new FileOutputStream(destinationFile);
                byte[] buffer = new byte[2048];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
                urlsList.add(destinationFile);
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        String[] inDisk=urlsList.stream().toArray(String[] ::new);
        post.setImagesUrlInDisk(inDisk);
        return post;
    }

    @Override
    public T mapThumbnail(T post) {
        try{
            post=super.mapThumbnail(post);
            if(!Objects.isNull(post.getThumbnailUrl())){
                //save to disk -> return url
                String destinationFile ="images/"+UUID.randomUUID()+"-"+FilenameUtils.getName(post.getThumbnailUrl());
                URL url = new URL(post.getThumbnailUrl());
                InputStream inputStream = url.openStream();
                OutputStream outputStream = new FileOutputStream(destinationFile);
                byte[] buffer = new byte[2048];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
                post.setThumbnailUrlInDisk(destinationFile);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return post;
    }
}
