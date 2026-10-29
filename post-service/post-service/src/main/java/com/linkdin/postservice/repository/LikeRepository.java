package com.linkdin.postservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkdin.postservice.entity.Like;
 

public interface LikeRepository extends JpaRepository<Like, String> {

	boolean existsByPostIdAndUserId(String postId, String userId);

	Optional<Like> findByPostIdAndUserId(String postId,String userId);

	void deleteAllByPostId(String postId);
	

}
