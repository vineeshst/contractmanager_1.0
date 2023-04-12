package com.manage.contract.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ApiService {
    private String serviceName;
    private String authorizationEndPoint;
    private String refreshTokenEndPoint;
    private String accessTokenEndPoint;
    private String clientId;
    private String clientSecret;
    private String refreshToken;
    private String redirectUri;
    private String tokenScope;
    private boolean implicitAuthorization;
    private String userId;
    private String password;
    private String authorizationCode;

    public ApiService() {
    }

    public ApiService(String serviceName, String authorizationEndPoint, String refreshTokenEndPoint, String accessTokenEndPoint, String clientId, String clientSecret, String redirectUri) {
        this.serviceName = serviceName;
        this.authorizationEndPoint = authorizationEndPoint;
        this.refreshTokenEndPoint = refreshTokenEndPoint;
        this.accessTokenEndPoint = accessTokenEndPoint;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getAuthorizationEndPoint() {
        return authorizationEndPoint;
    }

    public void setAuthorizationEndPoint(String authorizationEndPoint) {
        this.authorizationEndPoint = authorizationEndPoint;
    }

    public String getRefreshTokenEndPoint() {
        return refreshTokenEndPoint;
    }

    public void setRefreshTokenEndPoint(String refreshTokenEndPoint) {
        this.refreshTokenEndPoint = refreshTokenEndPoint;
    }

    public String getAccessTokenEndPoint() {
        return accessTokenEndPoint;
    }

    public void setAccessTokenEndPoint(String accessTokenEndPoint) {
        this.accessTokenEndPoint = accessTokenEndPoint;
    }

    public boolean isImplicitAuthorization() {
        return implicitAuthorization;
    }

    public void setImplicitAuthorization(boolean implicitAuthorization) {
        this.implicitAuthorization = implicitAuthorization;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getTokenScope() {
        return tokenScope;
    }

    public void setTokenScope(String tokenScope) {
        this.tokenScope = tokenScope;
    }
}
