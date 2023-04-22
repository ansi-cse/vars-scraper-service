package com.resdii.vars.enums;

public enum ProjectStatus {
    DBG("Đã bàn giao"),
    SMB("Sắp mở bán"),
    DMB("Đang mở bán"),
    DCN("Đang cập nhật");
    public String value;
    ProjectStatus(String value){
        this.value = value;
    }
}
