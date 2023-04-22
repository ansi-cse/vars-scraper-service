package com.resdii.vars.enums;

/**
 * @author ANSI.
 */
public enum BrokerType {

    ETP("ENTERPRISE"),
    PER("PERSONAL");

    public String value;
    BrokerType(String value){
        this.value = value;
    }
}
