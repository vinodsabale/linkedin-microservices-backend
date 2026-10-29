package com.linkdin.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
	@NotBlank(message="Email is Required")
	@Email(message="Invalid email formate")
	private String email;
	@NotBlank(message="password must there !")
	@Size(min=6,message="Password must be at least 6 character")
	private String password;
	@NotBlank(message="first name is Required")
	private String firstName;
	@NotBlank(message="last name is Required")
	private String lastName;
	private String headline;
	private String location;
	public RegisterRequest() {}
	public RegisterRequest(
			@NotBlank(message = "Email is Required") @Email(message = "Invalid email formate") String email,
			@NotBlank(message = "password must there !") @Size(min = 6, message = "Password must be at least 6 character") String password,
			@NotBlank(message = "first name is Required") String firstName,
			@NotBlank(message = "last name is Required") String lastName, String headline, String location) {
		super();
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.headline = headline;
		this.location = location;
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
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	@Override
	public String toString() {
		return "RegisterRequest [email=" + email + ", password=" + password + ", firstName=" + firstName + ", lastName="
				+ lastName + ", headline=" + headline + ", location=" + location + "]";
	}
	
}
