package com.linkdin.userservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.linkdin.userservice.dto.UserResponse;
import com.linkdin.userservice.entity.Connection;
import com.linkdin.userservice.entity.ConnectionStatus;
import com.linkdin.userservice.entity.User;
import com.linkdin.userservice.repository.ConnectionRepository;
import com.linkdin.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * Service layer for handling user operations and connections.
 * Publishes Kafka events for user and connection lifecycle changes.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // Kafka Topics
    private static final String CONNECTION_REQUESTED_TOPIC = "connection.requested";
    private static final String CONNECTION_ACCEPTED_TOPIC = "connection.accepted";
    private static final String CONNECTION_REJECTED_TOPIC = "connection.rejected";
    private static final String USER_CREATED_TOPIC = "user.created";
    private static final String USER_UPDATED_TOPIC = "user.updated";
    private static final String USER_DELETED_TOPIC = "user.deleted";

    /**
     * Fetch user profile by ID.
     * @param userId target user ID
     * @return UserResponse DTO
     */
    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not found " + userId));
        return mapToResponse(user);
    }

    /**
     * Map User entity to UserResponse DTO.
     * @param user User entity
     * @return UserResponse DTO
     */
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setHeadline(user.getHeadline());
        response.setAbout(user.getAbout());
        response.setLocation(user.getLocation());
        response.setCoverPhotoUrl(user.getCoverPhotoUrl());
        response.setRole(user.getRole());
        response.setSkill(user.getSkill());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    /**
     * Update user profile and publish user.updated event.
     * @param userId user ID
     * @param req updated profile data
     * @return updated UserResponse
     */
    public UserResponse updateProfile(String userId, UserResponse req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found " + userId));
        user.setHeadline(req.getHeadline());
        user.setAbout(req.getAbout());
        user.setLocation(req.getLocation());
        user.setSkill(req.getSkill());
        User savedUser = userRepository.save(user);

        Map<String, Object> userUpdatedEvent = new HashMap<>();
        userUpdatedEvent.put("userId", savedUser.getId());
        userUpdatedEvent.put("firstName", savedUser.getFirstName());
        userUpdatedEvent.put("lastName", savedUser.getLastName());
        userUpdatedEvent.put("headline", savedUser.getHeadline());
        userUpdatedEvent.put("location", savedUser.getLocation());
        userUpdatedEvent.put("skill", savedUser.getSkill());

        kafkaTemplate.send(USER_UPDATED_TOPIC, savedUser.getId(), userUpdatedEvent);
        log.info("user.updated event published : {}", savedUser.getId());

        return mapToResponse(savedUser);
    }

    /**
     * Send connection request and publish connection.requested event.
     * @param receivedUserId target user ID
     * @param requestedUserId requester user ID
     * @return confirmation message
     */
    public String sendConnectionRequest(String recivedUserId, String requestedUserId) {
        if (connectionRepository.existsByRequesterIdAndReciverId(requestedUserId, recivedUserId)) {
            throw new RuntimeException("Connection request already sent");
        }
        Connection con = new Connection();
        con.setRequesterId(requestedUserId);
        con.setReciverId(recivedUserId);
        con.setStatus(ConnectionStatus.PENDING);
        connectionRepository.save(con);

        Map<String, Object> connectionRequestedEvent = new HashMap<>();
        connectionRequestedEvent.put("requestedUserId", requestedUserId);
        connectionRequestedEvent.put("recivedUserId", recivedUserId);

        kafkaTemplate.send(CONNECTION_REQUESTED_TOPIC, recivedUserId, connectionRequestedEvent);
        log.info("Connection request sent : {} -> {}", requestedUserId, recivedUserId);

        return "Connection request sent";
    }

    /**
     * Accept connection request and publish connection.accepted event.
     * @param connectionId connection ID
     * @return confirmation message
     */
    public String acceptConnectionRequest(String connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found :" + connectionId));
        connection.setStatus(ConnectionStatus.CONNECTED);
        connectionRepository.save(connection);

        Map<String, Object> connectionAcceptedEvent = new HashMap<>();
        connectionAcceptedEvent.put("requestedUserId", connection.getRequesterId());
        connectionAcceptedEvent.put("recivedUserId", connection.getReciverId());

        kafkaTemplate.send(CONNECTION_ACCEPTED_TOPIC, connection.getRequesterId(), connectionAcceptedEvent);
        log.info("Connection accepted : {}", connectionId);

        return "Connection Accepted";
    }

    /**
     * Reject connection request and publish connection.rejected event.
     * @param connectionId connection ID
     * @return confirmation message
     */
    public String rejectConnectionRequest(String connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found :" + connectionId));
        connection.setStatus(ConnectionStatus.REJECTED);
        connectionRepository.save(connection);

        Map<String, Object> connectionRejectedEvent = new HashMap<>();
        connectionRejectedEvent.put("requestedUserId", connection.getRequesterId());
        connectionRejectedEvent.put("recivedUserId", connection.getReciverId());

        kafkaTemplate.send(CONNECTION_REJECTED_TOPIC, connection.getRequesterId(), connectionRejectedEvent);
        log.info("Connection rejected : {}", connectionId);

        return "Connection Rejected";
    }

    /**
     * Get all connected users for given userId.
     * @param userId requester user ID
     * @return list of UserResponse
     */
    public List<UserResponse> getConnections(String userId) {
        List<Connection> connections = connectionRepository.findByRequesterIdAndStatus(userId, ConnectionStatus.CONNECTED);
        return connections.stream()
                .map(c -> getUserProfile(c.getReciverId()))
                .collect(Collectors.toList());
    }

    /**
     * Get all pending connection requests for given userId.
     * @param userId receiver user ID
     * @return list of UserResponse
     */
    public List<UserResponse> getPendingRequests(String userId) {
        List<Connection> pending = connectionRepository.findByReciverIdAndStatus(userId, ConnectionStatus.PENDING);
        return pending.stream()
                .map(c -> getUserProfile(c.getRequesterId()))
                .collect(Collectors.toList());
    }

    /**
     * Create new user and publish user.created event.
     * @param user User entity
     * @return created UserResponse
     */
    public UserResponse createUser(User user) {
        User savedUser = userRepository.save(user);

        Map<String, Object> userCreatedEvent = new HashMap<>();
        userCreatedEvent.put("userId", savedUser.getId());
        userCreatedEvent.put("email", savedUser.getEmail());

        kafkaTemplate.send(USER_CREATED_TOPIC, savedUser.getId(), userCreatedEvent);
        log.info("User created : {}", savedUser.getId());

        return mapToResponse(savedUser);
    }

    /**
     * Delete user and publish user.deleted event.
     * @param userId user ID
     */
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        userRepository.delete(user);

        connectionRepository.deleteAllByRequesterIdOrReciverId(userId, userId);

        Map<String, Object> userDeletedEvent = new HashMap<>();
        userDeletedEvent.put("userId", userId);

        kafkaTemplate.send(USER_DELETED_TOPIC, userId, userDeletedEvent);
        log.info("User deleted : {}", userId);
    }

    /**
     * Search users by keyword in firstName, lastName, headline, or skill.
     * @param keyword search keyword
     * @return list of UserResponse
     */
    public List<UserResponse> searchUsers(String keyword) {
        List<User> users = userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrHeadlineContainingIgnoreCaseOrSkillContainingIgnoreCase(
                        keyword, keyword, keyword, keyword);
        return users.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    /**
     * Upload user profile photo by help of s3 bucket service
     * @param userId based on UserResponse,file for Multiple file upload
     * @param file
     * @return
     */
	public UserResponse updateProfilePhoto(String userId, MultipartFile file) {
		User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found "+userId));
		String photoUrl=s3Service.uploadFile(file,"profiles/"+userId+"/avatar");
		user.setProfilePhotoUrl(photoUrl);
		User savedUser=userRepository.save(user);
		log.info("Profile phot uploded for user :{} "+userId);
		return mapToResponse(savedUser);
		 
	}
}
