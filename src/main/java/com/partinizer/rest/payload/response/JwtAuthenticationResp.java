package com.partinizer.rest.payload.response;

public class JwtAuthenticationResp {
	private String accessToken;
	private String tokenType = "Bearer";

	public JwtAuthenticationResp(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

}
