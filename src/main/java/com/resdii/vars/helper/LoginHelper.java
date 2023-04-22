package com.resdii.vars.helper;

import com.resdii.vars.api.LoginApiClient;
import com.resdii.vars.api.UserApiClient;
import com.resdii.vars.dto.LoginDTO;
import com.resdii.vars.dto.LoginResponseDTO;
import com.resdii.vars.dto.ResponseDTO;
import com.resdii.vars.dto.UserInfoDTO;
import com.resdii.vars.enums.PrefixToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.resdii.vars.utils.CommonUtils.addPrefixToken;

@Component
@Scope("singleton")
public class LoginHelper {

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;


    public UserInfoDTO getUserInfoDTO() {
        return userInfoDTO;
    }

    public void setUserInfoDTO(UserInfoDTO userInfoDTO) {
        this.userInfoDTO = userInfoDTO;
    }

    private UserInfoDTO userInfoDTO;

    private LoginApiClient loginApiClient;

    private UserApiClient userApiClient;

    public void login(String clientId, String clientSecret,String phone, String password) {
        LoginDTO loginDTO=new LoginDTO(clientId, clientSecret, phone, password);
        ResponseDTO responseDTO= loginApiClient.login(loginDTO).getBody();
        LoginResponseDTO loginResponseDTO= (LoginResponseDTO) responseDTO.getData();
        ResponseDTO user= userApiClient.getUserInformation(loginResponseDTO.getAccessToken()).getBody();
        setToken(addPrefixToken(loginResponseDTO.getAccessToken(), PrefixToken.BEARER));
        setUserInfoDTO((UserInfoDTO) user.getData());
    }

    @Autowired
    public void setLoginClient(LoginApiClient loginApiClient) {
        this.loginApiClient = loginApiClient;
    }

    @Autowired
    public void setUserClient(UserApiClient userApiClient) {this.userApiClient = userApiClient;}
}
