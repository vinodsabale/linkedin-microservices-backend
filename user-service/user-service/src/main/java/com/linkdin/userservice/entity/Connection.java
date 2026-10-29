package com.linkdin.userservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name="connections")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Connection {
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private String id;
	@Column(nullable=false)
	private String requesterId;
	private String reciverId;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private ConnectionStatus status;
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
