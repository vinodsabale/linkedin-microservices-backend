package com.linkdin.userservice.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.linkdin.userservice.dto.AuthResponse;
import com.linkdin.userservice.dto.LoginRequest;
import com.linkdin.userservice.dto.RegisterRequest;
import com.linkdin.userservice.entity.User;
import com.linkdin.userservice.entity.UserRole;
import com.linkdin.userservice.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


/**
 * Service responsible for user authentication and onboarding flows.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    // Core Dependencies
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${jwt.secret}")
    private String secretKey;
      @Value("${jwt.expiration}")
    private Long jwtExpiration;
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    // Kafka Topics
    public static final String USER_CREATED_TOPIC = "user.created";

    /**
     * Registers a new user, hashes sensitive credentials, stores the record in the DB,
     * broadcasts a creation event to Kafka, and issues JWT auth tokens.
     *
     * @param request payload containing user registration details
     * @return AuthResponse containing access/refresh tokens and basic profile info
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Starting user registration for email: {}", request.getEmail());

        // Step 1: Validate email uniqueness to prevent duplicate accounts
        if (userRepo.existsByEmail(request.getEmail())) {
            log.warn("Registration failed. Email already in use: {}", request.getEmail());
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Step 2: Build the User entity and securely hash the raw password
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .headline(request.getHeadline())
                .location(request.getLocation())
                .password(passwordEncoder.encode(request.getPassword())) // Salting & BCrypt hashing
                .role(UserRole.NORMAL_USER)                              // Assign default access role
                .build();

        // Step 3: Persist the user to the database
        User savedUser = userRepo.save(user);
        log.info("User successfully persisted to database with ID: {}", savedUser.getId());

        // Step 4: Publish asynchronous 'user.created' event for downstream services (Search, Notifications)
        publishUserCreatedEvent(savedUser);

        // Step 5: Generate authentication tokens and construct the API response
        String accessToken = generateToken(savedUser.getId(), savedUser.getEmail());
        return buildAuthResponse(savedUser, accessToken);
    }

    /**
     * Helper method to construct and broadcast the Kafka user creation event.
     */
    private void publishUserCreatedEvent(User user) {
        Map<String, Object> userCreatedEvent = new HashMap<>();
        userCreatedEvent.put("userId", user.getId());
        userCreatedEvent.put("firstName", user.getFirstName());
        userCreatedEvent.put("lastName", user.getLastName());
        userCreatedEvent.put("email", user.getEmail());
        userCreatedEvent.put("headline", user.getHeadline());
        userCreatedEvent.put("location", user.getLocation());

        // userId is used as the partition key to ensure ordered processing per user
        kafkaTemplate.send(USER_CREATED_TOPIC, user.getId(), userCreatedEvent);
        log.info("Published '{}' event to Kafka for User ID: {}", USER_CREATED_TOPIC, user.getId());
    }

    /**
     * Generates a signed JWT Access Token for API authorization.
     */
    /**
     * 
     * @param id
     * @param email
     * @return
     */
    private String generateToken(String id, String email) {
        // TODO: Implement JWT access token signing logic (e.g., using io.jsonwebtoken / jjwt)
      
    	return Jwts.builder().claim("userId",id)
    			.setSubject(email)
    			.setIssuedAt(new Date(System.currentTimeMillis()+jwtExpiration))
    			.signWith(getSigninKey(),SignatureAlgorithm.ES256).compact();
    }

    private Key getSigninKey() {
		byte[] keyBytes=Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
     * Generates a long-lived Refresh Token for session renewal.
     */
    /**
     * used to get a new access token when it expires
     * server valid and return new access token 
     * client send refresh token to /auth/refresh  endpoint
     * @param id
     * @return
     */
    private String generateRefreshToken(String id) {
       return Jwts.builder().claim("userId", id)
    		   .setSubject(id).setIssuedAt(new Date())
    		   .setIssuedAt(new Date())
    		   .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
    		   .signWith(getSigninKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Maps the persisted User entity and tokens into the public AuthResponse DTO.
     */
    private AuthResponse buildAuthResponse(User user, String token) {
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setRefreshToken(generateRefreshToken(user.getId()));
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        return response;
    }

    public AuthResponse login(@Valid LoginRequest request) {
        log.info("Login attempt : {}", request.getEmail());
        
        //  Find user by email
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not Found !! " + request.getEmail()));
        	//BCrypt verify - compare raw password with sotred hash
        // S Compare raw password with stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials !!");
        }

        log.info("Login Successfull ",user.getId());
        //  Generate tokens & return response
        String token = generateToken(user.getId(), user.getEmail());
        return buildAuthResponse(user, token);
        
    }
    
    
}