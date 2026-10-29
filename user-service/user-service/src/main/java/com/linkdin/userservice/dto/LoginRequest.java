package com.linkdin.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
	@NotBlank(message="Email is Required")
	@Email(message="Invalid email formate")
	private String email;
	@NotBlank(message="password must there !")
	@Size(min=6,message="Invalid password")
	private String password;
	public LoginRequest() {}
	public LoginRequest(@NotBlank(message = "Email is Required") @Email(message = "Invalid email formate") String email,
			@NotBlank(message = "password must there !") @Size(min = 6, message = "Invalid password") String password) {
		super();
		this.email = email;
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "LoginRequest [email=" + email + ", password=" + password + "]";
	}
	
}
