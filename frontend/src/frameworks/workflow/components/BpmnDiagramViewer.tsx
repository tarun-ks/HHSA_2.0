import { useEffect, useRef, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import BpmnViewer from 'bpmn-js/lib/Viewer';
import { workflowService } from '../../../services/workflowService';
import { getWorkflowById } from '../workflowDefinitions';
import { Loader } from '../../../components/atoms/Loader';
import 'bpmn-js/dist/assets/diagram-js.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css';
import './BpmnDiagramViewer.css';

interface BpmnDiagramViewerProps {
  processDefinitionKey?: string; // BPMN process ID (e.g., "ContractConfiguration")
  workflowId?: string; // WF302, WF303, etc. (will be converted to processDefinitionKey)
  processInstanceKey?: number; // Optional: for highlighting active elements
  height?: string;
  className?: string;
}

/**
 * Reusable BPMN diagram viewer component using bpmn-js.
 * Framework-based: works for any workflow automatically.
 * 
 * Features:
 * - Renders actual BPMN diagrams with full branch support
 * - Highlights active elements based on process instance state
 * - Automatically fetches BPMN XML from backend
 * - Works for any workflow (WF302, WF303, etc.)
 */
export const BpmnDiagramViewer = ({
  processDefinitionKey,
  workflowId,
  processInstanceKey,
  height = '500px',
  className = '',
}: BpmnDiagramViewerProps) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const viewerRef = useRef<BpmnViewer | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Determine process definition key
  // Framework-based: supports both processDefinitionKey and workflowId
  const resolvedProcessDefinitionKey = processDefinitionKey || 
    (workflowId ? getWorkflowById(workflowId)?.processKey : undefined);

  // Fetch BPMN XML
  const { data: bpmnData, isLoading: isLoadingBpmn } = useQuery({
    queryKey: ['bpmn-xml', resolvedProcessDefinitionKey],
    queryFn: () => workflowService.getBpmnXml(resolvedProcessDefinitionKey!),
    enabled: !!resolvedProcessDefinitionKey,
  });

  // Fetch process instance and tasks for highlighting
  const { data: processInstanceData } = useQuery({
    queryKey: ['process-instance', processInstanceKey],
    queryFn: () => workflowService.getProcessInstance(processInstanceKey!),
    enabled: !!processInstanceKey,
  });

  const { data: tasksData } = useQuery({
    queryKey: ['tasks', processInstanceKey],
    queryFn: () => workflowService.getTasksByProcessInstance(processInstanceKey!),
    enabled: !!processInstanceKey,
  });

  // Fetch activity instances for status visualization (completed, active, error)
  const { data: activitiesData, error: activitiesError } = useQuery({
    queryKey: ['activities', processInstanceKey],
    queryFn: () => workflowService.getFlowNodeInstances(processInstanceKey!),
    enabled: !!processInstanceKey,
    retry: 1,
  });

  // Fetch incidents for error visualization
  const { data: incidentsData, error: incidentsError } = useQuery({
    queryKey: ['incidents', processInstanceKey],
    queryFn: () => workflowService.getIncidents(processInstanceKey!),
    enabled: !!processInstanceKey,
    retry: 1,
  });

  // Log errors (React Query v5 doesn't support onError)
  useEffect(() => {
    if (activitiesError) {
      console.warn('Failed to fetch activity instances:', activitiesError);
    }
    if (incidentsError) {
      console.warn('Failed to fetch incidents:', incidentsError);
    }
  }, [activitiesError, incidentsError]);

  // Initialize BPMN viewer
  useEffect(() => {
    if (!containerRef.current || !bpmnData?.success || !bpmnData.data) {
      return;
    }

    // Create viewer instance
    if (!viewerRef.current) {
      viewerRef.current = new BpmnViewer({
        container: containerRef.current,
      });
    }

    const viewer = viewerRef.current;

    // Import BPMN XML
    viewer.importXML(bpmnData.data)
      .then(() => {
        setError(null);
        
        // Highlight elements based on status if process instance provided
        if (processInstanceKey) {
          highlightElementsByStatus(
            viewer,
            (activitiesData?.success && activitiesData.data) ? activitiesData.data : [],
            (incidentsData?.success && incidentsData.data) ? incidentsData.data : [],
            (tasksData?.success && tasksData.data) ? tasksData.data : []
          );
        }
      })
      .catch((err: Error) => {
        console.error('Error rendering BPMN diagram:', err);
        setError('Failed to render BPMN diagram: ' + err.message);
      });

    // Cleanup
    return () => {
      if (viewerRef.current) {
        viewerRef.current.destroy();
        viewerRef.current = null;
      }
    };
  }, [bpmnData, processInstanceKey, tasksData, activitiesData, incidentsData]);

  // Update highlighting when data changes
  useEffect(() => {
    if (viewerRef.current && processInstanceKey) {
      highlightElementsByStatus(
        viewerRef.current,
        (activitiesData?.success && activitiesData.data) ? activitiesData.data : [],
        (incidentsData?.success && incidentsData.data) ? incidentsData.data : [],
        (tasksData?.success && tasksData.data) ? tasksData.data : []
      );
    }
  }, [activitiesData, incidentsData, tasksData, processInstanceKey]);

  if (!resolvedProcessDefinitionKey) {
    return (
      <div className={`flex justify-center items-center text-gray-500 dark:text-gray-400 ${className}`} style={{ height }}>
        <p>Process definition key or workflow ID required</p>
      </div>
    );
  }

  if (isLoadingBpmn) {
    return (
      <div className={`flex justify-center items-center ${className}`} style={{ height }}>
        <Loader size="lg" />
      </div>
    );
  }

  if (error) {
    return (
      <div className={`flex justify-center items-center text-red-500 ${className}`} style={{ height }}>
        <p>{error}</p>
      </div>
    );
  }

  if (!bpmnData?.data) {
    return (
      <div className={`flex justify-center items-center text-gray-500 dark:text-gray-400 ${className}`} style={{ height }}>
        <p>BPMN diagram not found</p>
      </div>
    );
  }

  return (
    <div className={`w-full ${className}`} style={{ height }}>
      <div ref={containerRef} className="w-full h-full" />
    </div>
  );
};

/**
 * Highlight elements in the BPMN diagram based on their status.
 * Framework-based: works for any workflow automatically.
 * 
 * Color coding:
 * - Green: Completed activities (state = COMPLETED)
 * - Orange: Active activities (state = ACTIVE or CREATED tasks)
 * - Red: Error/incident activities
 * - Default: Not started (no marker)
 */
function highlightElementsByStatus(
  viewer: BpmnViewer,
  activities: any[],
  incidents: any[],
  tasks: any[]
) {
  try {
    // Use any type for bpmn-js services (types may not be fully available)
    const canvas = viewer.get('canvas') as any;
    const elementRegistry = viewer.get('elementRegistry') as any;

    if (!canvas || !elementRegistry) {
      return;
    }

    // Get all BPMN elements (tasks, gateways, events, etc.)
    const allElements = elementRegistry.getAll();

    // Create maps for quick lookup
    const activityMap = new Map<string, any>();
    if (activities && Array.isArray(activities)) {
      activities.forEach((activity: any) => {
        if (activity && activity.activityId) {
          activityMap.set(activity.activityId, activity);
        }
      });
    }

    const incidentMap = new Map<string, any>();
    if (incidents && Array.isArray(incidents)) {
      incidents.forEach((incident: any) => {
        // Incidents may have flowNodeId - check both flowNodeId and activityId
        if (incident) {
          const nodeId = incident.flowNodeId || incident.activityId;
          if (nodeId) {
            incidentMap.set(nodeId, incident);
          }
        }
      });
    }

    // First, clear all markers from all elements
    const markers = ['completed-task', 'active-task', 'error-task'];
    allElements.forEach((element: any) => {
      const elementId = element?.id;
      if (elementId) {
        markers.forEach((marker) => {
          try {
            canvas.removeMarker(elementId, marker);
          } catch (e) {
            // Ignore errors when removing markers that don't exist
          }
        });
      }
    });

    // Then, add markers to elements based on their status
    allElements.forEach((element: any) => {
      const elementId = element?.id;
      if (!elementId) return;

      // Skip sequence flows and labels
      if (element.type === 'bpmn:SequenceFlow' || element.type === 'bpmn:Label') {
        return;
      }

      const activity = activityMap.get(elementId);
      const hasIncident = incidentMap.has(elementId);

      // Priority: Error > Active > Completed
      if (hasIncident || (activity && activity.state === 'TERMINATED')) {
        // Red: Error/incident
        try {
          canvas.addMarker(elementId, 'error-task');
        } catch (e) {
          console.warn(`Failed to add error marker to ${elementId}:`, e);
        }
      } else if (activity && activity.state === 'ACTIVE') {
        // Orange: Active
        try {
          canvas.addMarker(elementId, 'active-task');
        } catch (e) {
          console.warn(`Failed to add active marker to ${elementId}:`, e);
        }
      } else if (activity && activity.state === 'COMPLETED') {
        // Green: Completed
        try {
          canvas.addMarker(elementId, 'completed-task');
        } catch (e) {
          console.warn(`Failed to add completed marker to ${elementId}:`, e);
        }
      } else if (tasks && Array.isArray(tasks)) {
        // Check if there's an active task for this element
        const activeTask = tasks.find(
          (task: any) => task && (task.taskId === elementId || task.taskType === elementId) && task.state === 'CREATED'
        );
        if (activeTask) {
          // Orange: Active task
          try {
            canvas.addMarker(elementId, 'active-task');
          } catch (e) {
            console.warn(`Failed to add active task marker to ${elementId}:`, e);
          }
        }
      }
    });
  } catch (error) {
    console.warn('Failed to highlight elements by status:', error);
  }
}

