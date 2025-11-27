package com.hhsa.audit.controller;

import com.hhsa.audit.dto.AuditEventDTO;
import com.hhsa.audit.dto.AuditEventRequest;
import com.hhsa.audit.service.AuditService;
import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.common.core.dto.PageResponse;
import com.hhsa.common.core.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Audit controller.
 * Provides endpoints for publishing and querying audit events.
 */
@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Audit", description = "Audit logging endpoints")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/events")
    @Operation(summary = "Publish Audit Event", description = "Publish an audit event via OpenTelemetry")
    public ResponseEntity<ApiResponse<AuditEventDTO>> publishAuditEvent(@Valid @RequestBody AuditEventRequest request) {
        try {
            AuditEventDTO event = auditService.publishAuditEvent(request);
            return ResponseEntity.ok(ApiResponse.success(event, "Audit event published successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to publish audit event: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("AUDIT_ERROR", "Failed to publish audit event: " + e.getMessage())));
        }
    }

    @GetMapping("/events/entity/{entityType}/{entityId}")
    @Operation(summary = "Get Audit Events by Entity", description = "Get audit events for a specific entity")
    public ResponseEntity<ApiResponse<PageResponse<AuditEventDTO>>> getAuditEventsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            Pageable pageable = PaginationUtil.createPageable(page, size);
            Page<AuditEventDTO> events = auditService.findByEntity(entityType, entityId, pageable);
            PageResponse<AuditEventDTO> response = PageResponse.of(events);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve audit events: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("QUERY_ERROR", "Failed to retrieve audit events: " + e.getMessage())));
        }
    }

    @GetMapping("/events/user/{userId}")
    @Operation(summary = "Get Audit Events by User", description = "Get audit events for a specific user")
    public ResponseEntity<ApiResponse<PageResponse<AuditEventDTO>>> getAuditEventsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            Pageable pageable = PaginationUtil.createPageable(page, size);
            Page<AuditEventDTO> events = auditService.findByUser(userId, pageable);
            PageResponse<AuditEventDTO> response = PageResponse.of(events);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve audit events: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("QUERY_ERROR", "Failed to retrieve audit events: " + e.getMessage())));
        }
    }

    @GetMapping("/events/action/{action}")
    @Operation(summary = "Get Audit Events by Action", description = "Get audit events for a specific action")
    public ResponseEntity<ApiResponse<PageResponse<AuditEventDTO>>> getAuditEventsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            Pageable pageable = PaginationUtil.createPageable(page, size);
            Page<AuditEventDTO> events = auditService.findByAction(action, pageable);
            PageResponse<AuditEventDTO> response = PageResponse.of(events);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve audit events: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("QUERY_ERROR", "Failed to retrieve audit events: " + e.getMessage())));
        }
    }

    @GetMapping("/events")
    @Operation(summary = "List Audit Events", description = "List all audit events with pagination")
    public ResponseEntity<ApiResponse<PageResponse<AuditEventDTO>>> listAuditEvents(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        try {
            Pageable pageable = PaginationUtil.createPageable(page, size, sortBy, sortDirection);
            Page<AuditEventDTO> events = auditService.findAll(pageable).map(auditLog -> {
                AuditEventDTO dto = new AuditEventDTO();
                dto.setId(auditLog.getId());
                dto.setEntityType(auditLog.getEntityType());
                dto.setAction(auditLog.getAction());
                dto.setUserId(auditLog.getUserId());
                dto.setTimestamp(auditLog.getCreatedAt());
                dto.setIpAddress(auditLog.getIpAddress());
                dto.setUserAgent(auditLog.getUserAgent());
                // correlationId not available in AuditLog entity
                return dto;
            });

            PageResponse<AuditEventDTO> response = PageResponse.of(events);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve audit events: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("QUERY_ERROR", "Failed to retrieve audit events: " + e.getMessage())));
        }
    }
}


