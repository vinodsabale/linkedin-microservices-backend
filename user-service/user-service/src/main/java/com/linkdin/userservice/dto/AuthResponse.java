package com.linkdin.userservice.dto;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String tokenType="Bearer";
    public AuthResponse() {}
	public AuthResponse(String accessToken, String refreshToken, String userId, String email, String firstName,
			String lastName, String tokenType) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.userId = userId;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.tokenType = tokenType;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	@Override
	public String toString() {
		return "AuthResponse [accessToken=" + accessToken + ", refreshToken=" + refreshToken + ", userId=" + userId
				+ ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + ", tokenType="
				+ tokenType + "]";
	}
    
}
