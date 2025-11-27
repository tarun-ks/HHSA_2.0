package com.hhsa.workflow.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for process instance history
 */
@Data
public class ProcessInstanceHistoryDTO {

    private Long processInstanceKey;
    private String bpmnProcessId;
    private String processDefinitionId;
    private Long processDefinitionKey;
    private String state; // ACTIVE, COMPLETED, TERMINATED, etc.
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Map<String, Object> variables;
    private List<ActivityInstanceDTO> activities;
    private List<IncidentDTO> incidents;
}




