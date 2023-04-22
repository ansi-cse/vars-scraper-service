package com.resdii.vars.helper;

import com.resdii.vars.api.LocationApiClient;
import com.resdii.vars.dto.AutoCompleteDTO;
import com.resdii.vars.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class LocationHelper {

    private LocationApiClient locationApiClient;

    public AutoCompleteDTO getLocation(String address){
        ResponseDTO<ArrayList<AutoCompleteDTO>> responseDTO= locationApiClient.autoComplete(address).getBody();
        responseDTO.getData();
        if(responseDTO.getData().size()==0){
            return null;
        }
        return responseDTO.getData().get(0);
    }

    @Autowired
    public void setLocationClient(LocationApiClient locationApiClient) {
        this.locationApiClient = locationApiClient;
    }
}
