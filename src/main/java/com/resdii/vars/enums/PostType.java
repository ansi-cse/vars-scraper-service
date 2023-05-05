package com.resdii.vars.enums;

/**
 * @author ANSI.
 */

public enum PostType {
    BDS_SALE("bds-ban"),
    BDS_RENT("bds-cho-thue"),
    BDS_PROJECT("du-an");
    public String value;
    PostType(String value){
        this.value = value;
    }
}
