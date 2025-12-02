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
    // Ensure container is mounted and BPMN data is available
    if (!containerRef.current || !bpmnData?.success || !bpmnData.data) {
      return;
    }

    // Ensure container has dimensions before creating viewer
    const container = containerRef.current;
    if (!container.offsetWidth || !container.offsetHeight) {
      // Wait for container to be laid out
      const timeoutId = setTimeout(() => {
        if (container.offsetWidth && container.offsetHeight) {
          initializeViewer();
        }
      }, 100);
      return () => clearTimeout(timeoutId);
    }

    initializeViewer();

    function initializeViewer() {
      if (!containerRef.current || !bpmnData?.success || !bpmnData.data) {
        return;
      }

      // Destroy existing viewer if it exists
      if (viewerRef.current) {
        try {
          viewerRef.current.destroy();
        } catch (e) {
          console.warn('Error destroying existing viewer:', e);
        }
        viewerRef.current = null;
      }

      // Create new viewer instance
      try {
        viewerRef.current = new BpmnViewer({
          container: containerRef.current!,
        });
      } catch (e) {
        console.error('Error creating BPMN viewer:', e);
        setError('Failed to create BPMN viewer: ' + (e instanceof Error ? e.message : String(e)));
        return;
      }

      const viewer = viewerRef.current;

      // Import BPMN XML
      viewer.importXML(bpmnData.data)
        .then(() => {
          setError(null);
          
          // Highlight elements based on status if process instance provided
          if (processInstanceKey) {
            // Use setTimeout to ensure diagram is fully rendered before highlighting
            setTimeout(() => {
              const activities = (activitiesData?.success && activitiesData.data) ? activitiesData.data : [];
              const incidents = (incidentsData?.success && incidentsData.data) ? incidentsData.data : [];
              const tasks = (tasksData?.success && tasksData.data) ? tasksData.data : [];
              
              console.debug('Highlighting elements with:', {
                activitiesCount: activities.length,
                incidentsCount: incidents.length,
                tasksCount: tasks.length,
                activities: activities,
                tasks: tasks
              });
              
              highlightElementsByStatus(viewer, activities, incidents, tasks);
            }, 200); // Increased delay to ensure diagram is fully rendered
          }
        })
        .catch((err: Error) => {
          console.error('Error rendering BPMN diagram:', err);
          setError('Failed to render BPMN diagram: ' + err.message);
        });
    }

    // Cleanup
    return () => {
      if (viewerRef.current) {
        try {
          viewerRef.current.destroy();
        } catch (e) {
          console.warn('Error destroying viewer on cleanup:', e);
        }
        viewerRef.current = null;
      }
    };
  }, [bpmnData, processInstanceKey, tasksData, activitiesData, incidentsData]);

  // Update highlighting when data changes
  useEffect(() => {
    if (viewerRef.current && processInstanceKey && bpmnData?.success) {
      // Wait a bit to ensure diagram is rendered
      const timeoutId = setTimeout(() => {
        const activities = (activitiesData?.success && activitiesData.data) ? activitiesData.data : [];
        const incidents = (incidentsData?.success && incidentsData.data) ? incidentsData.data : [];
        const tasks = (tasksData?.success && tasksData.data) ? tasksData.data : [];
        
        console.debug('Updating highlights with:', {
          activitiesCount: activities.length,
          incidentsCount: incidents.length,
          tasksCount: tasks.length
        });
        
        highlightElementsByStatus(viewerRef.current!, activities, incidents, tasks);
      }, 200);
      
      return () => clearTimeout(timeoutId);
    }
  }, [activitiesData, incidentsData, tasksData, processInstanceKey, bpmnData]);

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
    const overlays = viewer.get('overlays') as any;

    if (!canvas || !elementRegistry || !overlays) {
      console.warn('BPMN canvas, elementRegistry, or overlays not available');
      return;
    }

    // Get all BPMN elements (tasks, gateways, events, etc.)
    const allElements = elementRegistry.getAll();
    
    // Clear all existing overlays first
    allElements.forEach((element: any) => {
      const elementId = element?.id;
      if (elementId) {
        try {
          overlays.remove({ element: elementId, type: 'badge' });
          overlays.remove({ element: elementId, type: 'checkmark' });
        } catch (e) {
          // Ignore errors when removing overlays that don't exist
        }
      }
    });
    console.debug(`Found ${allElements.length} BPMN elements to highlight`);

    // Create maps for quick lookup
    const activityMap = new Map<string, any>();
    if (activities && Array.isArray(activities)) {
      console.debug(`Processing ${activities.length} activities`);
      activities.forEach((activity: any) => {
        if (activity && activity.activityId) {
          activityMap.set(activity.activityId, activity);
          console.debug(`Mapped activity: ${activity.activityId} -> state: ${activity.state}`);
        } else {
          console.warn('Activity missing activityId:', activity);
        }
      });
    } else {
      console.warn('Activities data is not an array:', activities);
    }

    // Note: Incidents don't currently have flowNodeId in DTO, so we'll skip incident highlighting
    // This can be enhanced later when flowNodeId is added to IncidentDTO
    const incidentMap = new Map<string, any>();
    if (incidents && Array.isArray(incidents)) {
      console.debug(`Processing ${incidents.length} incidents (flowNodeId not available in DTO)`);
      // For now, incidents won't be highlighted until flowNodeId is added to DTO
    }

    // Create task map for quick lookup by element ID
    const taskMap = new Map<string, any>();
    if (tasks && Array.isArray(tasks)) {
      console.debug(`Processing ${tasks.length} tasks`);
      tasks.forEach((task: any) => {
        // Tasks may have taskId or taskType that matches BPMN element ID
        if (task && task.taskId) {
          taskMap.set(task.taskId, task);
        }
        if (task && task.taskType) {
          taskMap.set(task.taskType, task);
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

    // Count active tasks per element for badges
    const activeTaskCountMap = new Map<string, number>();
    if (tasks && Array.isArray(tasks)) {
      tasks.forEach((task: any) => {
        if (task && (task.state === 'CREATED' || task.state === 'ASSIGNED')) {
          const taskElementId = task.taskId || task.taskType;
          if (taskElementId) {
            activeTaskCountMap.set(taskElementId, (activeTaskCountMap.get(taskElementId) || 0) + 1);
          }
        }
      });
    }

    // Then, add markers to elements based on their status
    let highlightedCount = 0;
    console.debug(`Activity map size: ${activityMap.size}, Task map size: ${taskMap.size}, Incident map size: ${incidentMap.size}`);
    
    allElements.forEach((element: any) => {
      const elementId = element?.id;
      if (!elementId) return;

      // Skip sequence flows, labels, and text annotations
      if (
        element.type === 'bpmn:SequenceFlow' || 
        element.type === 'bpmn:Label' ||
        element.type === 'bpmn:TextAnnotation'
      ) {
        return;
      }

      const activity = activityMap.get(elementId);
      const task = taskMap.get(elementId);
      const hasIncident = incidentMap.has(elementId);
      const activeTaskCount = activeTaskCountMap.get(elementId) || 0;
      
      // Debug logging for user tasks and start events
      if (element.type === 'bpmn:UserTask' || element.type === 'bpmn:ServiceTask' || element.type === 'bpmn:StartEvent') {
        console.debug(`Element ${elementId} (${element.type}): activity=${!!activity}, task=${!!task}, incident=${hasIncident}, activeCount=${activeTaskCount}`);
        if (activity) {
          console.debug(`  Activity state: ${activity.state}`);
        }
        if (task) {
          console.debug(`  Task state: ${task.state}`);
        }
      }

      // Start events are always completed (they fire immediately) - check FIRST before other conditions
      if (element.type === 'bpmn:StartEvent') {
        try {
          canvas.addMarker(elementId, 'completed-task');
          // Add checkmark overlay for start events
          const checkmarkHtml = `<div class="bpmn-checkmark">✓</div>`;
          overlays.add(elementId, 'checkmark', {
            position: { top: -8, right: -8 },
            html: checkmarkHtml
          });
          highlightedCount++;
          console.debug(`Marked start event ${elementId} as COMPLETED`);
          return; // Skip to next element (use return in forEach callback, not continue)
        } catch (e) {
          console.warn(`Failed to add completed marker to start event ${elementId}:`, e);
          return; // Skip to next element even on error
        }
      }

      // Priority: Error > Active > Completed
      if (hasIncident || (activity && activity.state === 'TERMINATED')) {
        // Red: Error/incident
        try {
          canvas.addMarker(elementId, 'error-task');
          highlightedCount++;
          console.debug(`Marked ${elementId} as ERROR`);
        } catch (e) {
          console.warn(`Failed to add error marker to ${elementId}:`, e);
        }
      } else if (activity && activity.state === 'ACTIVE') {
        // Green: Active (matching Operate style)
        try {
          canvas.addMarker(elementId, 'active-task');
          // Add active count badge overlay (like Operate shows "1")
          if (activeTaskCount > 0) {
            const badgeHtml = `<div class="bpmn-badge bpmn-badge-active">${activeTaskCount}</div>`;
            overlays.add(elementId, 'badge', {
              position: { top: -8, right: -8 },
              html: badgeHtml
            });
          }
          highlightedCount++;
          console.debug(`Marked ${elementId} as ACTIVE (count: ${activeTaskCount})`);
        } catch (e) {
          console.warn(`Failed to add active marker to ${elementId}:`, e);
        }
      } else if (task && (task.state === 'CREATED' || task.state === 'ASSIGNED')) {
        // Green: Active task (CREATED or ASSIGNED) - matching Operate style
        try {
          canvas.addMarker(elementId, 'active-task');
          // Add active count badge overlay
          const count = activeTaskCount > 0 ? activeTaskCount : 1;
          const badgeHtml = `<div class="bpmn-badge bpmn-badge-active">${count}</div>`;
          overlays.add(elementId, 'badge', {
            position: { top: -8, right: -8 },
            html: badgeHtml
          });
          highlightedCount++;
          console.debug(`Marked ${elementId} as ACTIVE (task, count: ${count})`);
        } catch (e) {
          console.warn(`Failed to add active task marker to ${elementId}:`, e);
        }
      } else if (activity && activity.state === 'COMPLETED') {
        // Green: Completed (with checkmark)
        try {
          canvas.addMarker(elementId, 'completed-task');
          // Add checkmark overlay (like Operate)
          const checkmarkHtml = `<div class="bpmn-checkmark">✓</div>`;
          overlays.add(elementId, 'checkmark', {
            position: { top: -8, right: -8 },
            html: checkmarkHtml
          });
          highlightedCount++;
          console.debug(`Marked ${elementId} as COMPLETED`);
        } catch (e) {
          console.warn(`Failed to add completed marker to ${elementId}:`, e);
        }
      }
    });

    console.debug(`Highlighted ${highlightedCount} elements in BPMN diagram`);
  } catch (error) {
    console.error('Failed to highlight elements by status:', error);
  }
}

