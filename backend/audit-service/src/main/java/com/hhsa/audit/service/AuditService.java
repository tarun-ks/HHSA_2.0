package com.hhsa.audit.service;

import com.hhsa.audit.dto.AuditEventDTO;
import com.hhsa.audit.dto.AuditEventRequest;
import com.hhsa.audit.entity.AuditLog;
import com.hhsa.audit.repository.AuditLogRepository;
import com.hhsa.common.infrastructure.service.BaseService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Audit service implementation.
 * Publishes audit events via OpenTelemetry and stores in database.
 */
@Service
@Transactional
public class AuditService implements BaseService<AuditLog, Long, AuditLogRepository> {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final Tracer tracer;
    private final ObjectMapper objectMapper;

    public AuditService(
            AuditLogRepository auditLogRepository,
            OpenTelemetry openTelemetry,
            ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.tracer = openTelemetry.getTracer("audit-service");
        this.objectMapper = objectMapper;
    }

    @Override
    public AuditLogRepository getRepository() {
        return auditLogRepository;
    }

    @Override
    public String getEntityName() {
        return "AuditLog";
    }

    /**
     * Publish audit event via OpenTelemetry and store in database
     */
    public AuditEventDTO publishAuditEvent(AuditEventRequest request) {
        logger.debug("Publishing audit event: {} - {} - {}", request.getAction(), request.getEntityType(), request.getEntityId());

        // Create span for audit event
        Span span = tracer.spanBuilder("audit.event")
            .setAttribute("audit.entity.type", request.getEntityType())
            .setAttribute("audit.entity.id", request.getEntityId())
            .setAttribute("audit.action", request.getAction())
            .setAttribute("audit.user.id", request.getUserId())
            .startSpan();

        try {
            // Add changes as span attributes if available
            if (request.getChanges() != null) {
                try {
                    String changesJson = objectMapper.writeValueAsString(request.getChanges());
                    span.setAttribute("audit.changes", changesJson);
                } catch (Exception e) {
                    logger.warn("Failed to serialize changes to JSON", e);
                }
            }

            // Create audit log entity
            AuditLog auditLog = new AuditLog();
            auditLog.setEntityType(request.getEntityType());
            auditLog.setEntityId(request.getEntityId());
            auditLog.setAction(request.getAction());
            auditLog.setUserId(request.getUserId());
            
            if (request.getTenantId() != null) {
                // Convert tenantId from String to Long if needed
                try {
                    auditLog.setTenantId(Long.parseLong(request.getTenantId()));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid tenant ID format: {}", request.getTenantId());
                }
            }
            
            // Store changes as Map (JSONB)
            if (request.getChanges() != null) {
                auditLog.setChanges(request.getChanges());
            }

            auditLog.setIpAddress(request.getIpAddress());
            auditLog.setUserAgent(request.getUserAgent());

            // Save to database
            auditLog = save(auditLog);

            // Create DTO
            AuditEventDTO dto = toDTO(auditLog);
            dto.setTraceId(span.getSpanContext().getTraceId());
            dto.setSpanId(span.getSpanContext().getSpanId());

            logger.info("Audit event published: {} - {} - {} (trace: {})", 
                request.getAction(), request.getEntityType(), request.getEntityId(), 
                span.getSpanContext().getTraceId());

            return dto;

        } finally {
            span.end();
        }
    }

    /**
     * Query audit events by entity
     */
    public Page<AuditEventDTO> findByEntity(String entityType, Long entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable)
            .map(this::toDTO);
    }

    /**
     * Query audit events by user
     */
    public Page<AuditEventDTO> findByUser(String userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable)
            .map(this::toDTO);
    }

    /**
     * Query audit events by action
     */
    public Page<AuditEventDTO> findByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable)
            .map(this::toDTO);
    }

    /**
     * Convert entity to DTO
     */
    private AuditEventDTO toDTO(AuditLog auditLog) {
        AuditEventDTO dto = new AuditEventDTO();
        dto.setId(auditLog.getId());
        dto.setEntityType(auditLog.getEntityType());
        dto.setEntityId(auditLog.getEntityId());
        dto.setAction(auditLog.getAction());
        dto.setUserId(auditLog.getUserId());
        
        if (auditLog.getTenantId() != null) {
            dto.setTenantId(String.valueOf(auditLog.getTenantId()));
        }
        
        dto.setTimestamp(auditLog.getCreatedAt());
        dto.setChanges(auditLog.getChanges());
        dto.setIpAddress(auditLog.getIpAddress());
        dto.setUserAgent(auditLog.getUserAgent());

        return dto;
    }
}

