package com.linkdin.postservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.linkdin.postservice.entity.Comment;
import com.linkdin.postservice.entity.Post;
import com.linkdin.postservice.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	
	//create post
	@PostMapping
	public ResponseEntity<Post> createPost(@RequestParam String authorId,@RequestParam String content,@RequestParam(required=false)MultipartFile image){
   return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(authorId,content,image));
    
	}
	@GetMapping("/{postId}")
	public ResponseEntity<Post> getPost(@PathVariable String postId){
		return ResponseEntity.ok(postService.getPost(postId));
	}
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Post>> getUserPosts(@PathVariable String userId){
	    return ResponseEntity.ok(postService.getUserPosts(userId));
	}

	@PostMapping("/{postId}/like")
	public ResponseEntity<String> getLikePosts(@PathVariable String postId,@RequestParam String userId){
	    return ResponseEntity.ok(postService.getLikePosts(postId,userId));
	}

	/*
	@GetMapping("/user/{userId}")
	public ResponseEntity<Post> getUserPosts(@PathVariable String userId){
		return ResponseEntity.ok(postService.getUserPosts(userId));
	}
	@PostMapping("/{postId}/like")
	public ResponseEntity<Post> getLikePosts(@PathVariable String postId,@RequestParam String userId){
		return ResponseEntity.ok(postService.getLikePosts(postId,userId));
	}*/
	@PostMapping("/{postId}/comments")
	public ResponseEntity<Comment> addComment(@PathVariable String postId,@RequestParam String authorId,@RequestParam String content){
		return ResponseEntity.status(HttpStatus.CREATED).body(postService.addComment(postId,authorId,content));
	}
	@DeleteMapping("/{postId}")
	public ResponseEntity<String> deletePost(@PathVariable String postId,@RequestParam String userId){
		postService.deletePost(postId,userId);
		return ResponseEntity.ok("Post Deleted ");
	}
	@GetMapping("/{postId}/comments")
	public ResponseEntity<List<Comment>> getAllCommnets(@PathVariable String postId){
		return ResponseEntity.ok(postService.getAllComments(postId));
	}
	
}
