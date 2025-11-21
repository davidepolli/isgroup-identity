package it.isgroup.identity.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.isgroup.identity.jpa.entities.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}