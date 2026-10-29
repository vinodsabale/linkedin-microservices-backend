package com.linkdin.searchservice.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.linkdin.searchservice.model.UserDocument;

public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, String>{
	@Query("{\"multi_match\":{\"query\":\"?0\",\"fields\":[\"firstName\",\"lastName\",\"headline\",\"location\"]}}")
   public List<UserDocument> searchUsers(String post);
 //it serach by specific skill
	public List<UserDocument> findBySkillsContaining(String skill);
	
   
}
