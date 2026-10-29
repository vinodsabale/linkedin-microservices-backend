package com.linkdin.postservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkdin.postservice.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, String> {

	void deleteAllByPostId(String postId);

	public List<Comment> findByPostIdOrderByCreatedAtAsc(String postId);

}
