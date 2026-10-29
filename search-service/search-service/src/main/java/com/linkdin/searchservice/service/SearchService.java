package com.linkdin.searchservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.linkdin.searchservice.model.PostDocument;
import com.linkdin.searchservice.model.UserDocument;
import com.linkdin.searchservice.repository.PostSearchRepository;
import com.linkdin.searchservice.repository.UserSearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {
	private final UserSearchRepository userSearchRepo;
	private final PostSearchRepository postSearchRepo;
	//search post by content
	public List<PostDocument> searchPost(String post) {
		log.info("Searching post : {}",post);
		
		return postSearchRepo.searchPosts(post);
	}
  /**
   * search user by skill
   * @param skill
   * @return
   */
	public List<UserDocument> searchBySkill(String skill) {
		log.info("Searching user by skill : {}",skill);
		
		return userSearchRepo.findBySkillsContaining(skill);
	}

	public List<UserDocument> searchUsers(String q) {
        log.info("Searching user :{}",q);
		
		return userSearchRepo.searchUsers(q);
	}

	

}
