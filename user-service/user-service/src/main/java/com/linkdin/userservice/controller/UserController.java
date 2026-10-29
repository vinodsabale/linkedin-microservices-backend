package com.linkdin.userservice.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.linkdin.userservice.dto.UserResponse;
import com.linkdin.userservice.entity.User;
import com.linkdin.userservice.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
	private final UserService userService;
	/**
	 * Get user profile
	 * x-user-id=requesting user (from gateway)
	 * userid in path = target user to fetch
	 * @return
	 */
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId,@RequestHeader("X-User-Id") String requestingUserId){
		log.info("Get profile :{}",userId,requestingUserId);
		
		return ResponseEntity.ok(userService.getUserProfile(userId));
	}
	/**
	 * update own profile
	 * user can only update there own profile
	 * @param userId
	 * @param requestingUserId
	 * @return
	 */
	@PutMapping("/{userId}/profile")
	public ResponseEntity<UserResponse> updateProfile(@PathVariable String userId,@RequestHeader("X-User-Id") String requestingUserId,@RequestBody UserResponse req){
		log.info("profile is updated :{}",userId,requestingUserId);
		if(!userId.equals(requestingUserId)){
			return ResponseEntity.status(403).build();
		}
		return ResponseEntity.ok(userService.updateProfile(userId,req));
	}
	/**
	 * send connection request
	 * request id come from x-user-id header -already validation
	 * @param targetUserId
	 * @param requestingUserId
	 * @return
	 */
	@PostMapping("/{targetUserId}/connect")
	public ResponseEntity<String> sendConnectionRequest(@PathVariable String targetUserId,@RequestHeader("X-User-Id") String requestingUserId){
		return ResponseEntity.ok(userService.sendConnectionRequest(targetUserId,requestingUserId));
	}
	@PutMapping("/connection/{connectionId}/accept")
	public ResponseEntity<String> acceptConnection(@PathVariable String connectionId,@RequestHeader("X-User-Id") String requestingUserId){
		return ResponseEntity.ok(userService.acceptConnectionRequest(connectionId));
	}
	@GetMapping("/{userId}/connections")
	public ResponseEntity<List<UserResponse>> getConnection(@PathVariable String userId){
		return ResponseEntity.ok(userService.getConnections(userId));
	}
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable String userId,
	                                       @RequestHeader("X-User-Id") String requestingUserId) {
	    if (!userId.equals(requestingUserId)) {
	        return ResponseEntity.status(403).build();
	    }
	    userService.deleteUser(userId);
	    return ResponseEntity.noContent().build();
	}
	@PutMapping("/connection/{connectionId}/reject")
	public ResponseEntity<String> rejectConnection(@PathVariable String connectionId,
	                                               @RequestHeader("X-User-Id") String requestingUserId) {
	    return ResponseEntity.ok(userService.rejectConnectionRequest(connectionId));
	}
	@GetMapping("/{userId}/pending-requests")
	public ResponseEntity<List<UserResponse>> getPendingRequests(@PathVariable String userId) {
	    return ResponseEntity.ok(userService.getPendingRequests(userId));
	}
	@GetMapping("/search")
	public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword) {
	    return ResponseEntity.ok(userService.searchUsers(keyword));
	}
	@PostMapping
	public ResponseEntity<UserResponse> createUser(@RequestBody User user) {
	    return ResponseEntity.ok(userService.createUser(user));
	}
	@PostMapping("/{userId}/profile-photo")
	public ResponseEntity<UserResponse> uploadProfilePhoto(@PathVariable String userId,@RequestHeader("X-User-Id") String requestingUserId,@RequestParam("file")MultipartFile file){
		if(!userId.equals(requestingUserId)) {
			return ResponseEntity.status(403).build();
		}
		return ResponseEntity.ok(userService.updateProfilePhoto(userId, file));
	}

}	
