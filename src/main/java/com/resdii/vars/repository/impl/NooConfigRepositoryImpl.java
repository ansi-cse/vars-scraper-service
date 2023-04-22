package com.resdii.vars.repository.impl;

import com.resdii.ms.common.utils.JsonUtils;
import com.resdii.noodev.sdk.NooConfig;
import com.resdii.noodev.sdk.NooException;
import com.resdii.vars.repository.NooConfigRepository;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class NooConfigRepositoryImpl implements NooConfigRepository {
    @Override
    public Object getNooConfig(String code) throws NooException {
        NooConfig nooConfig = NooConfig.get();
        return nooConfig.get(code, null);
    }

    @Override
    public <T> List<T> getNooConfigAsList(String code, Class<T[]> tClass) throws NooException {
        NooConfig nooConfig = NooConfig.get();
        JSONArray array = nooConfig.getJSONArray(code, null);
        if(array == null){
            return null;
        }
        return JsonUtils.parseList(array.toString(), tClass);
    }

    @Override
    public Map<String, Object> getAll() throws NooException {
        NooConfig nooConfig = NooConfig.get();
        return nooConfig.getParams();
    }
}
