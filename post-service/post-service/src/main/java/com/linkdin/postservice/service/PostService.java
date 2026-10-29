package com.linkdin.postservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.linkdin.postservice.entity.Comment;
import com.linkdin.postservice.entity.Like;
import com.linkdin.postservice.entity.Post;
import com.linkdin.postservice.repository.CommentRepository;
import com.linkdin.postservice.repository.LikeRepository;
import com.linkdin.postservice.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRespository;
	private final LikeRepository likeRepository;
	private final CommentRepository commentRepository;
	private final S3Service s3Service;
	private final KafkaTemplate<String,Object> kafkaTemplate;
	   // Kafka topics
	    private static final String POST_CREATED_TOPIC = "post.created";
	    private static final String POST_LIKED_TOPIC   = "post.liked";
	    private static final String POST_COMMENTED_TOPIC = "post.commented";
	    private static final String POST_DELETED_TOPIC = "post.deleted";

	
	
	/**
	 * create a post
	 * optinally upload image to s3
	 * publsih post.created event to kafka
	 * feed service and search service will consume this.
	 * @param authorId
	 * @param content
	 * @param image
	 * @return
	 */
	public Post createPost(String authorId, String content, MultipartFile image) {
		log.info("Creating post for user :{} ",authorId);
		Post post=new Post();
		post.setAuthorId(authorId);
		post.setContent(content);
		if(image!=null && !image.isEmpty()) {
			String imageUrl=s3Service.uploadFile(image,"posts/"+authorId);
			post.setImageUrl(imageUrl);
		}
		Post savedPost=postRespository.save(post);
		log.info("Post created : {}",savedPost.getId()); 
		//publish to kafka - feed service and search service consume this
		Map<String,Object> postCreatedEvent=new HashMap<>();
		postCreatedEvent.put("postId", savedPost.getId());
		postCreatedEvent.put("authorId",savedPost.getAuthorId());
		postCreatedEvent.put("content", savedPost.getContent());
		postCreatedEvent.put("imageUrl", savedPost.getImageUrl());
		postCreatedEvent.put("createdAt", savedPost.getCreatedAt());
		kafkaTemplate.send(POST_CREATED_TOPIC,savedPost.getId(),postCreatedEvent);
		log.info("post.created event publsihed:{}",savedPost.getId());
		return savedPost;
		
	}
	public Post getPost(String postId) {
		return postRespository.findById(postId).orElseThrow(()->new RuntimeException("Post not found :"+postId));
	}
	public List<Post> getUserPosts(String userId) {
		return postRespository.findByAuthorIdOrderByCreatedAtDesc(userId);
	}
	/**
	 * like or unlike post
	 * @param postId
	 * @param userId
	 * @return
	 */
	public String getLikePosts(String postId, String userId) {
		Post post=getPost(postId);
		if(likeRepository.existsByPostIdAndUserId(postId,userId)) {
			likeRepository.findByPostIdAndUserId(postId,userId).ifPresent(likeRepository::delete);
			post.setLikeCount(post.getLikeCount()-1);
			postRespository.save(post);
			return "Post unliked";
						
		}
		Like like =new Like();
		like.setPostId(postId);
		like.setUserId(userId);
		likeRepository.save(like);
		post.setLikeCount(post.getLikeCount()+1);
		postRespository.save(post);
		//publish post.liked event
		Map<String,Object> postLikedEvent=new HashMap<>();
		postLikedEvent.put("postId", postId);
		postLikedEvent.put("userId", userId);
		postLikedEvent.put("authorId", post.getAuthorId());
		kafkaTemplate.send(POST_LIKED_TOPIC,postId,postLikedEvent);
		return "Post liked";
				
	}
	
	    // --- Existing methods omitted for brevity ---

	    /**
	     * Add a comment to a post
	     * Publishes post.commented event
	     */
	    public Comment addComment(String postId, String authorId, String content) {
	        Post post = getPost(postId);
	        Comment comment = new Comment();
	        comment.setPostId(postId);
	        comment.setAuthorId(authorId);
	        comment.setContent(content);

	        Comment savedComment = commentRepository.save(comment);
	        log.info("Comment added to post {} by user {}", postId, authorId);

	        // Publish Kafka event
	        Map<String,Object> postCommentedEvent = new HashMap<>();
	        postCommentedEvent.put("postId", postId);
	        postCommentedEvent.put("commentId", savedComment.getId());
	        postCommentedEvent.put("authorId", authorId);
	        postCommentedEvent.put("content", content);
	        postCommentedEvent.put("createdAt", savedComment.getCreatedAt());

	        kafkaTemplate.send(POST_COMMENTED_TOPIC, postId, postCommentedEvent);
	        log.info("post.commented event published: {}", savedComment.getId());

	        return savedComment;
	    }

	    /**
	     * Delete a post (only if user is author)
	     * Publishes post.deleted event
	     */
	    public void deletePost(String postId, String userId) {
	        Post post = getPost(postId);
	        if (!post.getAuthorId().equals(userId)) {
	            throw new RuntimeException("User not authorized to delete this post");
	        }

	        // Delete likes and comments associated with the post
	        likeRepository.deleteAllByPostId(postId);
	        commentRepository.deleteAllByPostId(postId);
	        postRespository.delete(post);

	        log.info("Post deleted: {}", postId);

	        // Publish Kafka event
	        Map<String,Object> postDeletedEvent = new HashMap<>();
	        postDeletedEvent.put("postId", postId);
	        postDeletedEvent.put("authorId", userId);

	        kafkaTemplate.send(POST_DELETED_TOPIC, postId, postDeletedEvent);
	        log.info("post.deleted event published: {}", postId);
	    }

	    /**
	     * Get all comments for a post
	     */
	    public List<Comment> getAllComments(String postId) {
	        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
	    }
	
	
}
