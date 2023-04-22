package com.resdii.vars.services;

import com.resdii.vars.repository.NooConfigRepository;
import com.resdii.vars.repository.impl.NooConfigRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseServiceImpl{

    protected NooConfigRepository nooConfigRepository;

    @Autowired
    public void setNooConfigRepository(NooConfigRepositoryImpl nooConfigRepository) {
        this.nooConfigRepository = nooConfigRepository;
    }
}
