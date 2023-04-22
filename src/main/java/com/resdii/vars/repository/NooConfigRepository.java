package com.resdii.vars.repository;

import com.resdii.noodev.sdk.NooException;

import java.util.List;
import java.util.Map;

public interface NooConfigRepository {
    Object getNooConfig(String code) throws NooException;
    <T> List<T> getNooConfigAsList(String code, Class<T[]> tClass) throws NooException;
    Map<String, Object> getAll() throws NooException;
}
