package com.resdii.vars.enums;

public enum BotStatus {
    STARTING("STARTING"),
    STARTED("STARTED"),
    RUNNING("RUNNING"),
    STOPPING("STOPPING"),
    STOPPED("STOPPED");
    public String value;
    BotStatus(String value){
        this.value = value;
    }
}
