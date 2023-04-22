package com.resdii.vars.enums;

public enum PrefixToken {
    BEARER("Bearer"),
    BASIC("Basic");
    public String value;
    PrefixToken(String value){
        this.value = value;
    }
}
