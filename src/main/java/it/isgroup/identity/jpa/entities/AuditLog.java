package it.isgroup.identity.jpa.entities;

import java.time.Instant;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_log")
public class AuditLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Instant occurredAt;
	@Column(length = 100, nullable = false)
	private String actor;

	@Column(length = 60, nullable = false)
	private String action;

	@Column(length = 60, nullable = false)
	private String resource;

	private String resourceId;

	@Column(length = 20, nullable = false)
	private String outcome;

	@Column(length = 4000, nullable = false)
	private String details;
	
	public AuditLog(Instant t, String actor, String action, String resource, String rid, String outcome,
			String details) {
		this.occurredAt = t;
		this.actor = actor;
		this.action = action;
		this.resource = resource;
		this.resourceId = rid;
		this.outcome = outcome;
		this.details = details;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instant getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(Instant occurredAt) {
		this.occurredAt = occurredAt;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}