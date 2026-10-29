package com.linkdin.userservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.linkdin.userservice.entity.UserRole;


public class UserResponse {
	private String id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String headline;
	private String about;
	private String location;
	private String profilePhotoUrl;
	private String coverPhotoUrl;
	private UserRole role;
	private List<String> skill;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	public UserResponse() {}
	public UserResponse(String id, String email, String password, String firstName, String lastName, String headline,
			String about, String location, String profilePhotoUrl, String coverPhotoUrl, UserRole role,
			List<String> skill, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.headline = headline;
		this.about = about;
		this.location = location;
		this.profilePhotoUrl = profilePhotoUrl;
		this.coverPhotoUrl = coverPhotoUrl;
		this.role = role;
		this.skill = skill;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getProfilePhotoUrl() {
		return profilePhotoUrl;
	}
	public void setProfilePhotoUrl(String profilePhotoUrl) {
		this.profilePhotoUrl = profilePhotoUrl;
	}
	public String getCoverPhotoUrl() {
		return coverPhotoUrl;
	}
	public void setCoverPhotoUrl(String coverPhotoUrl) {
		this.coverPhotoUrl = coverPhotoUrl;
	}
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	public List<String> getSkill() {
		return skill;
	}
	public void setSkill(List<String> skill) {
		this.skill = skill;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	@Override
	public String toString() {
		return "UserResponse [id=" + id + ", email=" + email + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", headline=" + headline + ", about=" + about + ", location=" + location
				+ ", profilePhotoUrl=" + profilePhotoUrl + ", coverPhotoUrl=" + coverPhotoUrl + ", role=" + role
				+ ", skill=" + skill + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	
	
}
