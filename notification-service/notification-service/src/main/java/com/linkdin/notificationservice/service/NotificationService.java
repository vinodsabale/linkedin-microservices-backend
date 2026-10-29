package com.linkdin.notificationservice.service;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
	/**
	 * Consume user.created event
	 * Send Welcome notification
	 */
	@KafkaListener(topics="user.created")
	public void consumerUserCreated(@Payload Map<String,Object> payload) {
		try {
			String userId=(String) payload.get("userId");
			String firstName=(String) payload.get("firstName");
			sendNotification(userId,"Welcome to linkdin !",String.format("Welcome %s Your account has been created "+"Start connection with professional..AND build you carrer !!", firstName));
		}catch(Exception e) {
			log.error("Errro sending welcome notification : {}",e.getMessage());	
			}
	}
	/**
	 * consume connection.requested event
	 * Notify receiver about connection request
	 */
	@KafkaListener(topics="connection.requested")
	public void consumeConnectionRequest(@Payload Map<String,Object> payload) {
		try {
			String reciverId=(String) payload.get("receiverId");
			String requesterId=(String) payload.get("requestedId");
			
			sendNotification(reciverId,"New Connection Request",String.format("User %s want to connect with you. ", requesterId));
		}catch(Exception e) {
			log.error("Error sending connection request notification :{}",e.getMessage());
		}
	}
	/**
	 * consume connection.accepted event
	 * Notify requester that connection was accepted
	 */
	@KafkaListener(topics="connection.accepted")
	public void consumeConnectionAccepted(@Payload Map<String,Object> payload) {
		try {
			String reciverId=(String) payload.get("receiverId");
			String requesterId=(String) payload.get("requestedId");
			sendNotification(requesterId," Connection Accepted",String.format("User %s accpeted your connection request. "+"You are now connected !.", requesterId));
		}catch(Exception e) {
			log.error("Error sending connection accepted notification : {}",e.getMessage());
		}
	}
	
	@KafkaListener(topics="post.liked")
	public void consumePostLiked(@Payload Map<String,Object> payload) {
		try {
			String authorId=(String) payload.get("authorId");
			String userId=(String) payload.get("userId");
			String postId=(String)payload.get("postId");
			sendNotification(authorId, "Someone liked your post ", String.format("User %s liked your post %s", userId, postId));
		}catch(Exception e) {
			log.error("Error sending like notification : {}",e.getMessage());
		}
	}
	
	public void consumePostComment(@Payload Map<String,Object> payload) {
		try {
			String postAuthorId=(String) payload.get("postAuthorId");
			String commenterId=(String) payload.get("authorId");
			String postId=(String) payload.get("postId");
			sendNotification(postAuthorId,"New comment on yout post ",String.format("User %s commented on your post %s", commenterId,postId));
		}catch(Exception e ) { 
			log.error("Errro sending comment notification : {}",e.getMessage());
		}
	}
	@KafkaListener(topics="post.commented")
	private void sendNotification(String userId, String title, String message) {
		log.info("-----------------------------------------------------------------------------");
		log.info("Notifcation sned ");
		log.info("To User :{}",userId);
		log.info("Title : {}",title);
		log.info("Message :{}",message);
		log.info("---------------------------------------------------------------------------------");
	}
	
	
	
}
