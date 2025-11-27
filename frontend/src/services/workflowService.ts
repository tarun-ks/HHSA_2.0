import { apiClient } from './apiClient';
import type { ApiResponse } from './apiClient';

export interface TaskDTO {
  taskKey: number;
  taskType: string;
  taskId: string;
  processInstanceKey: number;
  processDefinitionId: string;
  assignee?: string;
  candidateUser?: string;
  candidateGroup?: string;
  creationTime: string;
  dueDate?: string;
  variables?: Record<string, any>;
  state: string;
}

// Alias for backward compatibility
export type WorkflowTask = TaskDTO;

export interface TaskCompleteRequest {
  taskId: number;
  variables?: Record<string, any>;
  comments?: string;
  approved?: boolean;
}

export interface ProcessInstanceDTO {
  processInstanceKey: number;
  processDefinitionKey: string;
  processDefinitionVersion: number;
  bpmnProcessId: string;
  state: string;
  startTime: string;
  endTime?: string;
  variables?: Record<string, any>;
}

export interface TaskHistoryDTO {
  taskKey: number;
  taskId: string;
  taskName: string;
  taskType: string;
  processInstanceKey: number;
  processDefinitionId: string;
  creationTime: string;
  createdBy?: string;
  assignmentTime?: string;
  assignedTo?: string;
  claimedTime?: string;
  claimedBy?: string;
  completionTime?: string;
  completedBy?: string;
  dueDate?: string;
  state: string;
  assignee?: string;
  candidateUser?: string;
  candidateGroup?: string;
  completionComment?: string;
  outcome?: string;
}

/**
 * Workflow service for managing workflow tasks and processes
 * Uses existing Workflow Adapter Service backend (port 8093)
 */
export const workflowService = {
  /**
   * Get tasks for a user
   */
  async getTasksForUser(userId: string): Promise<ApiResponse<TaskDTO[]>> {
    const response = await apiClient.get<ApiResponse<TaskDTO[]>>(
      `/api/v1/workflows/tasks/user/${userId}`
    );
    return response;
  },

  /**
   * Get tasks for a user (alias for backward compatibility)
   */
  async getUserTasks(userId: string): Promise<ApiResponse<TaskDTO[]>> {
    return this.getTasksForUser(userId);
  },

  /**
   * Get tasks by process instance
   */
  async getTasksByProcessInstance(processInstanceKey: number): Promise<ApiResponse<TaskDTO[]>> {
    const response = await apiClient.get<ApiResponse<TaskDTO[]>>(
      `/api/v1/workflows/process-instances/${processInstanceKey}/tasks`
    );
    return response;
  },

  /**
   * Get process instance details
   */
  async getProcessInstance(processInstanceKey: number): Promise<ApiResponse<ProcessInstanceDTO>> {
    const response = await apiClient.get<ApiResponse<ProcessInstanceDTO>>(
      `/api/v1/workflows/process-instances/${processInstanceKey}`
    );
    return response;
  },

  /**
   * Complete a task
   */
  async completeTask(taskId: number, variables?: Record<string, any>): Promise<ApiResponse<void>> {
    const response = await apiClient.post<ApiResponse<void>>(
      `/api/v1/workflows/tasks/${taskId}/complete`,
      variables || {}
    );
    return response;
  },

  /**
   * Get process instance history for a contract
   */
  async getProcessInstanceHistory(contractId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.get<ApiResponse<any>>(
      `/api/v1/workflows/process-instances/contract/${contractId}/history`
    );
    return response;
  },

  /**
   * Start a process instance
   */
  async startProcess(processDefinitionKey: string, variables?: Record<string, any>): Promise<ApiResponse<ProcessInstanceResult>> {
    const response = await apiClient.post<ApiResponse<ProcessInstanceResult>>(
      `/api/v1/workflows/processes/${processDefinitionKey}/start`,
      variables || {}
    );
    return response;
  },

  /**
   * Cancel a process instance
   */
  async cancelProcessInstance(processInstanceKey: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete<ApiResponse<void>>(
      `/api/v1/workflows/process-instances/${processInstanceKey}`
    );
    return response;
  },

  /**
   * Assign a task to a user
   */
  async assignTask(taskId: number, userId: string): Promise<ApiResponse<void>> {
    const response = await apiClient.post<ApiResponse<void>>(
      `/api/v1/workflows/tasks/${taskId}/assign`,
      { userId }
    );
    return response;
  },

  /**
   * Claim an unassigned task
   */
  async claimTask(taskId: number, userId: string): Promise<ApiResponse<void>> {
    const response = await apiClient.post<ApiResponse<void>>(
      `/api/v1/workflows/tasks/${taskId}/claim`,
      { userId }
    );
    return response;
  },

  /**
   * Unclaim a task
   */
  async unclaimTask(taskId: number): Promise<ApiResponse<void>> {
    const response = await apiClient.post<ApiResponse<void>>(
      `/api/v1/workflows/tasks/${taskId}/unclaim`
    );
    return response;
  },

  /**
   * Reassign a task to a different user
   */
  async reassignTask(taskId: number, userId: string): Promise<ApiResponse<void>> {
    const response = await apiClient.post<ApiResponse<void>>(
      `/api/v1/workflows/tasks/${taskId}/reassign`,
      { userId }
    );
    return response;
  },

  /**
   * Return a task to the previous step
   */
  async returnTask(taskId: number, reason?: string): Promise<ApiResponse<void>> {
    const response = await apiClient.post<ApiResponse<void>>(
      `/api/v1/workflows/tasks/${taskId}/return`,
      reason ? { reason } : {}
    );
    return response;
  },

  /**
   * Get BPMN XML for a process definition
   * Framework-based: works for any workflow
   */
  async getBpmnXml(processDefinitionKey: string): Promise<ApiResponse<string>> {
    const response = await apiClient.get<ApiResponse<string>>(
      `/api/v1/workflows/process-definitions/${processDefinitionKey}/bpmn`
    );
    return response;
  },

  /**
   * Get flow node instances (activities) for a process instance
   * Used for visualizing workflow status in BPMN diagrams
   */
  async getFlowNodeInstances(processInstanceKey: number): Promise<ApiResponse<ActivityInstanceDTO[]>> {
    const response = await apiClient.get<ApiResponse<ActivityInstanceDTO[]>>(
      `/api/v1/workflows/process-instances/${processInstanceKey}/activities`
    );
    return response;
  },

  /**
   * Get incidents (errors) for a process instance
   * Used for visualizing errors in BPMN diagrams
   */
  async getIncidents(processInstanceKey: number): Promise<ApiResponse<IncidentDTO[]>> {
    const response = await apiClient.get<ApiResponse<IncidentDTO[]>>(
      `/api/v1/workflows/process-instances/${processInstanceKey}/incidents`
    );
    return response;
  },

  /**
   * Get task history for a process instance
   * Returns detailed timeline of task creation, assignment, and completion/approval
   */
  async getTaskHistory(processInstanceKey: number): Promise<ApiResponse<TaskHistoryDTO[]>> {
    const response = await apiClient.get<ApiResponse<TaskHistoryDTO[]>>(
      `/api/v1/workflows/process-instances/${processInstanceKey}/task-history`
    );
    return response;
  },
};

export interface ProcessInstanceResult {
  processInstanceKey: number;
  processDefinitionKey: string;
  processDefinitionVersion: number;
  bpmnProcessId: string;
  variables?: Record<string, any>;
}

export interface ActivityInstanceDTO {
  activityInstanceKey: number;
  activityId: string;
  activityName: string;
  activityType: string;
  state: string; // COMPLETED, ACTIVE, TERMINATED
  startTime?: string;
  endTime?: string;
  assignee?: string;
}

export interface IncidentDTO {
  incidentKey: number;
  incidentType: string;
  errorMessage: string;
  creationTime?: string;
  resolutionTime?: string;
  state: string; // OPEN, RESOLVED
}
