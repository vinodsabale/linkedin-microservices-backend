package com.linkdin.feedservice.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.linkdin.feedservice.client.UserServiceClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedEventConsumer {
	private final UserServiceClient userServiceClient;
	private static final String FEED_KEY_PREFIX="feed";
	private final RedisTemplate<String, Object>  redisTemplate;
	@Value("${feed.max-size}")
	private int maxFeedSize;
	
	/**
	 * COnsume post.created event
	 * when a user created post - immdetly push that post 
	 * to all their connnnection feed 
	 * this is how linkedin or twitter generate feed 
	 * @param payload
	 */
	@KafkaListener(topics="post.created")
	public void consumePostCreated(@Payload Map<String,Object> payload) {
		try {
			String postId=(String)payload.get("postId");
			String authorId=(String) payload.get("authorId");
			//Get all coonection of the post author
			List<Map<String,Object>> connections=userServiceClient.getConnection(authorId);
			//Push post to each connection's feed
			for(Map<String,Object> connection:connections) {
				String connectionId=(String)connection.get("id");
				String feedKey=FEED_KEY_PREFIX+connectionId;
				
				//add post to feed (as sorted set
				redisTemplate.opsForList().leftPush(feedKey, postId);
				redisTemplate.opsForList().trim(feedKey, 0, maxFeedSize-1);
				 log.info("Post {} pushed to feed of user : {}",postId,connectionId);
				 
			}//for each -end
			String authorFeedKey=FEED_KEY_PREFIX+authorId;
			redisTemplate.opsForList().leftPush(authorFeedKey, postId);
			redisTemplate.opsForList().trim(authorFeedKey, 0, maxFeedSize-1);
		}catch(Exception e) {
		 log.error("Error is pushing : {} ",e.getMessage());	
		}
	}
	
}
