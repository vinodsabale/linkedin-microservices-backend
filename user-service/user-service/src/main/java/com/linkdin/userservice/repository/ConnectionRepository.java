package com.linkdin.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkdin.userservice.entity.Connection;
import com.linkdin.userservice.entity.ConnectionStatus;

public interface ConnectionRepository extends JpaRepository<Connection, String>{
	boolean existsByRequesterIdAndReciverId(String requestId,String reciverId);
	public List<Connection> findByRequesterIdAndStatus(String requestId,ConnectionStatus status);
	public void deleteAllByRequesterIdOrReciverId(String userId, String userId2);
	public List<Connection> findByReciverIdAndStatus(String userId, ConnectionStatus pending);
	
	
}
