package com.linkdin.searchservice.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Document for user search
 * Indexed when user register or update profile
 * Enables full text search across name,headline,skills,location
 * 
 */
@Document(indexName="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {
	@Id
	
	 private String id;
	@Field(type=FieldType.Text) 
	 private String firstName;
	@Field(type=FieldType.Text) 
	 private String lastName;
	@Field(type=FieldType.Text) 
	 private String headline;
	@Field(type=FieldType.Keyword) 
	 private String location;
	@Field(type=FieldType.Keyword) 
	 private List<String> skill;
	@Field(type=FieldType.Keyword) 
	 private String email;
	 private String profilePhotoUrl;
	 
}
