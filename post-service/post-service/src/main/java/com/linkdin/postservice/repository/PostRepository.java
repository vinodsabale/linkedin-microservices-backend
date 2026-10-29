package com.linkdin.postservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkdin.postservice.entity.Post;

public interface PostRepository extends JpaRepository<Post, String> {

	public List<Post> findByAuthorIdOrderByCreatedAtDesc(String userId);

}
