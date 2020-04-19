package com.aws.codestar.hikermeetup.security.services;

import com.aws.codestar.hikermeetup.security.model.CognitoAuthenticationToken;
import com.aws.codestar.hikermeetup.security.model.CurrentUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrentUserInfoService {

    @Value("${endpoints.userInfo}")
    private String userInfoUrl = "";

    public CurrentUserInfo getUserInfo() {
        CognitoAuthenticationToken authentication = (CognitoAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", String.format("Bearer %s", authentication.getCredentials()));

        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<CurrentUserInfo> responseEntity = restTemplate.exchange(userInfoUrl, HttpMethod.GET, httpEntity, CurrentUserInfo.class);
        return responseEntity.getBody();
    }
}
