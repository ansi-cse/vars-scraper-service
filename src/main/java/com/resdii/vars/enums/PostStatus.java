package com.resdii.vars.enums;

public enum PostStatus {
    NOT_EXIST("NOT_EXIST"),
    FAILED("FAILED"),
    SUCCESS("SUCCESS"),
    PROCESSING("PROCESSING"),
    TRY("TRY");
    public String value;
    PostStatus(String value){
        this.value = value;
    }
}
