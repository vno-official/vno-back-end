package com.vno.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenDto {
    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("token_type")
    public String tokenType = "Bearer";

    @JsonProperty("expires_in")
    public int expiresIn;

    public AccessTokenDto() {}

    public AccessTokenDto(String accessToken, int expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
