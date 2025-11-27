# NYC Procure - Framework Documentation

> **Purpose**: Comprehensive documentation of all frameworks, services, and reusable components created for the NYC Procure application. This document helps new developers understand the current implementation and avoid starting from scratch.

**Last Updated**: 2025-01-18  
**Version**: 2.0

---

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Backend Frameworks](#backend-frameworks)
3. [Frontend Frameworks](#frontend-frameworks)
4. [Backend Services](#backend-services)
5. [Frontend Services](#frontend-services)
6. [Common Patterns](#common-patterns)
7. [Development Guidelines](#development-guidelines)

---

## 🏗️ Architecture Overview

### Technology Stack

**Backend:**
- Spring Boot 3.2.0
- Java 17
- PostgreSQL (shared database: `eprocurement`)
- Camunda 8 (Zeebe) for workflows
- Keycloak for authentication
- OpenTelemetry for audit logging

**Frontend:**
- React 18+ with TypeScript
- Vite for build tooling
- Redux Toolkit + React Query for state management
- TailwindCSS for styling
- React Router for routing

### Architecture Principles

1. **Microservices Architecture**: Each capability is an independent service
2. **Pluggable Design**: Services can be swapped without breaking consumers
3. **Framework-Based**: Reusable frameworks for common functionality
4. **API Gateway**: Single entry point (Spring Cloud Gateway on port 8080)
5. **Shared Database**: Single PostgreSQL database `eprocurement` (project decision)

---

## 🔧 Backend Frameworks

### 1. Common Core (`backend/common-core`)

**Purpose**: Lightweight shared library with NO JPA dependencies.

**Scope (STRICTLY LIMITED):**
- ✅ DTOs (`ApiResponse`, `PageResponse`)
- ✅ Exception Handling (`ResourceNotFoundException`, `ValidationException`)
- ✅ Security Utilities (JWT parsing, token validation helpers)
- ✅ Constants and Enums
- ✅ Validation Utilities (`ValidationUtil`, `DateUtil`, `PaginationUtil`)

**FORBIDDEN:**
- ❌ Business Logic
- ❌ Entities (JPA dependencies)
- ❌ Repositories
- ❌ Services with business logic

**Usage:**
```xml
<dependency>
    <groupId>com.hhsa</groupId>
    <artifactId>common-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

**Key Classes:**
- `ApiResponse<T>`: Standardized API response wrapper
- `PageResponse<T>`: Paginated response wrapper
- `ResourceNotFoundException`: For missing resources
- `ValidationException`: For validation errors
- `GlobalExceptionHandler`: Global exception handler

### 2. Common Infrastructure (`backend/common-infrastructure`)

**Purpose**: JPA infrastructure for services that need database access.

**Scope:**
- ✅ `BaseEntity`: Base entity with audit fields (created_at, updated_at, created_by, updated_by, deleted, version)
- ✅ `BaseRepository<T>`: Base repository with pagination and soft delete support
- ✅ `BaseService<T>`: Base service with CRUD operations

**Usage:**
```xml
<dependency>
    <groupId>com.hhsa</groupId>
    <artifactId>common-infrastructure</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

**Key Features:**
- Soft delete pattern (never hard delete)
- Audit fields (created_at, updated_at, created_by, updated_by)
- Pagination support
- Optimistic locking (version field)

### 3. Workflow Adapter Framework

**Purpose**: Abstract workflow engine operations for pluggable workflow support.

**Location**: `backend/workflow-adapter-service`

**Key Interface:**
```java
public interface WorkflowAdapter {
    DeploymentResult deployProcess(String bpmnXml);
    ProcessInstanceResult startProcess(String processDefinitionKey, Map<String, Object> variables);
    void completeTask(Long taskId, Map<String, Object> variables);
    List<TaskDTO> getTasksForUser(String userId);
    List<TaskDTO> getTasksByProcessInstance(Long processInstanceKey);
    void assignTask(Long taskId, String userId);
    void claimTask(Long taskId, String userId);
    void unclaimTask(Long taskId);
    void cancelProcessInstance(Long processInstanceKey);
    DecisionResult evaluateDecision(String decisionKey, Map<String, Object> variables);
    List<ActivityInstanceDTO> getFlowNodeInstances(Long processInstanceKey);
    List<IncidentDTO> getIncidents(Long processInstanceKey);
    List<TaskHistoryDTO> getTaskHistory(Long processInstanceKey);
}
```

**Implementation:**
- `Camunda8WorkflowAdapter`: Camunda 8 (Zeebe) implementation
- `OperateApiService`: Integration with Camunda Operate API

**Features:**
- BPMN process deployment
- Process instance management
- Task management (assign, claim, complete)
- DMN decision evaluation
- Process history and activity tracking
- Incident management

---

## 🎨 Frontend Frameworks

### 1. Authorization Framework (`frontend/src/frameworks/authorization`)

**Purpose**: Role and permission-based access control.

**Components:**
- `PermissionGate`: Single component with `hide`, `readonly`, `disabled` modes
- `RoleGate`: Conditional rendering based on roles
- `ProtectedRoute`: Route-level protection
- `ProtectedComponent`: Component-level protection

**Hooks:**
- `useAuthorization`: Main authorization hook with cached permission checks
- `usePermission`: Permission checking hook
- `useRole`: Role checking hook

**Utilities:**
- `permissionCache`: Performance optimization (O(1) lookup, pre-computed on login)
- `roleUtils`: Role checking utilities
- `permissionUtils`: Permission checking utilities

**Usage:**
```typescript
// Hide button if no permission
<PermissionGate permission="CONTRACT_DELETE" mode="hide">
  <Button>Delete</Button>
</PermissionGate>

// Disable button if no permission
<PermissionGate permission="CONTRACT_EDIT" mode="disabled">
  <Button>Edit</Button>
</PermissionGate>

// Make table readonly if no permission
<PermissionGate permission="CONTRACT_CONFIGURE" mode="readonly">
  <EditableTable ... />
</PermissionGate>
```

**Permissions:**
- `CONTRACT_CREATE`, `CONTRACT_VIEW`, `CONTRACT_CONFIGURE`, `CONTRACT_EDIT`, `CONTRACT_DELETE`
- `CONTRACT_BUDGET_SAVE`
- `WORKFLOW_TASK_VIEW`, `WORKFLOW_TASK_APPROVE`
- `SERVICES_ENABLE`, `SERVICES_VIEW`
- `BUDGET_TEMPLATE_SELECT`, `BUDGET_TEMPLATE_REMOVE`

**Performance:**
- Permission cache pre-computed on user login
- O(1) lookup instead of O(n) calculation
- Automatic cache invalidation on role changes

### 2. UI Framework (`frontend/src/frameworks/ui`)

**Purpose**: Reusable UI components for consistent design.

**Components:**
- `Tabs`: Tab navigation with lazy loading, icons, and badges
- `LazyTabContent`: Wrapper for lazy loading tab content
- `SearchBar`: Search and filter component with debouncing
- `ListPageLayout`: Standardized layout for list pages

**Usage:**
```typescript
// Tabs with lazy loading
<Tabs
  items={[
    { id: 'tab1', label: 'Tab 1', content: <Component1 />, lazy: true },
    { id: 'tab2', label: 'Tab 2', content: <Component2 />, lazy: true },
  ]}
/>

// Search bar with filters
<SearchBar
  onSearch={handleSearch}
  filters={[
    { key: 'status', type: 'select', options: [...] },
    { key: 'dateRange', type: 'dateRange' },
  ]}
/>

// List page layout
<ListPageLayout
  title="Contracts"
  searchBar={<SearchBar ... />}
>
  <ContractList />
</ListPageLayout>
```

### 3. Business Rules Framework (`frontend/src/frameworks/business-rules`)

**Purpose**: Declarative business rules engine for validation and calculations.

**Components:**
- `RuleEngine`: Core rule execution engine
- `RuleContext`: Context for rule evaluation
- `RuleResult`: Result of rule evaluation

**Hooks:**
- `useValidation`: Validation hook for forms and tables
- `useBusinessRules`: Business rules hook

**Rules:**
- `contractConfigurationRules`: Contract configuration validation rules
- `contractBudgetRules`: Contract budget validation rules
- `calculationRules`: Calculation rules
- `validationRules`: Common validation rules

**Usage:**
```typescript
const { getValidationFunction } = useValidation<COAAllocation>(
  CONTRACT_CONFIGURATION_ROW_RULES,
  ruleContext
);

const validateRow = getValidationFunction();

// Use in EditableTable
<EditableTable
  validateRow={validateRow}
  ...
/>
```

**Rule Types:**
- Row-level validation
- Form-level validation
- Calculation rules
- Duplicate detection
- Sum validation

### 4. Grid Framework (`frontend/src/frameworks/grid`)

**Purpose**: Grid/table functionality with plugins.

**Components:**
- `EditableTable`: Editable table component (from `components/organisms`)
- `ExcelExportPlugin`: Excel export plugin
- `PrintPlugin`: Print plugin

**Hooks:**
- `useGridPlugins`: Grid plugin management

**Usage:**
```typescript
<EditableTable
  columns={columns}
  data={data}
  onChange={handleChange}
  plugins={[
    ExcelExportPlugin({ filename: 'export', format: 'csv' }),
    PrintPlugin({ title: 'Report' }),
  ]}
/>
```

### 5. Workflow Framework (`frontend/src/frameworks/workflow`)

**Purpose**: Workflow visualization and management.

**Components:**
- `BpmnDiagramViewer`: BPMN diagram viewer using `bpmn-js`
- `ProcessInstanceStatus`: Process instance status display
- `TaskHistoryTimeline`: Task history timeline
- `WorkflowActionButtons`: Workflow action buttons (complete, assign, etc.)
- `WorkflowStatusSection`: Workflow status section

**Configuration:**
- `workflowDefinitions.ts`: Centralized workflow definitions

**Usage:**
```typescript
// Workflow definitions
export const WORKFLOW_DEFINITIONS = {
  WF302: {
    processKey: "ContractConfiguration",
    workflowId: "WF302",
    displayName: "Contract Configuration",
    ...
  },
};

// BPMN diagram viewer
<BpmnDiagramViewer
  processInstanceKey={processInstanceKey}
  bpmnXml={bpmnXml}
/>
```

**Features:**
- Color-coded workflow status (green: completed, orange: active, red: error)
- Task history timeline
- Process instance tracking
- Activity instance visualization

---

## 🚀 Backend Services

### 1. API Gateway Service (`backend/api-gateway-service`)

**Purpose**: Single entry point for all frontend requests.

**Port**: 8080

**Features:**
- JWT validation (delegates to Keycloak)
- CORS handling (centralized)
- Rate limiting
- Request routing to backend services

**Routing:**
- `/api/v1/{service-name}/**` → backend service
- Frontend ONLY communicates with API Gateway, never directly with backend services

### 2. Auth Service (`backend/auth-service`)

**Purpose**: Authentication and authorization.

**Features:**
- Keycloak integration (primary)
- JWT token validation
- User management
- Role-based access control

**Endpoints:**
- `POST /api/v1/auth/login`: Authenticate user
- `POST /api/v1/auth/refresh`: Refresh token
- `POST /api/v1/auth/logout`: Logout
- `GET /api/v1/auth/me`: Get current user

### 3. Contract Management Service (`backend/contract-management-service`)

**Purpose**: Contract lifecycle management.

**Features:**
- Contract CRUD operations
- Contract configuration (COA allocations)
- Contract budget management
- Funding source allocation
- Workflow integration

**Database Tables:**
- `contracts`
- `contract_coa_allocations`
- `contract_budgets`
- `contract_funding_sources`
- `contract_budget_templates`

**Workflow Integration:**
- WF302: Contract Configuration workflow
- WF303: Contract Certification of Funds workflow

### 4. Workflow Adapter Service (`backend/workflow-adapter-service`)

**Purpose**: Workflow engine abstraction and Camunda 8 integration.

**Features:**
- BPMN process deployment
- Process instance management
- Task management
- DMN decision evaluation
- Process history tracking

**Integration:**
- Camunda 8 (Zeebe) Gateway: `localhost:26500`
- Camunda Operate API for process history

### 5. Document Service (`backend/document-service`)

**Purpose**: Document management with pluggable storage.

**Features:**
- Document upload/download
- Metadata management
- Pluggable storage (local file system for POC, S3 for future)
- Document relationships (entity type, entity ID, category)

**Storage Providers:**
- Local File System (POC): Organized by date (yyyy/MM/dd)
- S3 (Future): AWS S3 integration

### 6. Audit Service (`backend/audit-service`)

**Purpose**: Audit logging using OpenTelemetry.

**Features:**
- Audit event publishing
- OpenTelemetry integration
- Audit all create, update, delete operations
- Workflow state change auditing

---

## 📱 Frontend Services

### 1. API Client (`frontend/src/services/apiClient.ts`)

**Purpose**: Centralized API client for all backend communication.

**Features:**
- Base URL: `http://localhost:8080` (API Gateway)
- JWT token management
- Request/response interceptors
- Error handling

### 2. Contract Service (`frontend/src/services/contractService.ts`)

**Purpose**: Contract-related API calls.

**Endpoints:**
- Contract CRUD operations
- Contract configuration
- Contract budget management
- Funding source management

### 3. Workflow Service (`frontend/src/services/workflowService.ts`)

**Purpose**: Workflow-related API calls.

**Endpoints:**
- Process instance management
- Task management
- Process history
- Activity instances
- Incidents

### 4. Auth Service (`frontend/src/services/authService.ts`)

**Purpose**: Authentication-related API calls.

**Endpoints:**
- Login
- Logout
- Token refresh
- Current user

---

## 🔄 Common Patterns

### 1. API Response Pattern

All backend services use `ApiResponse<T>` for consistent responses:

```java
// Success response
return ApiResponse.success(data);

// Error response
return ApiResponse.error("Error message", errorDetails);
```

### 2. Soft Delete Pattern

All entities extend `BaseEntity` which includes:
- `deleted` boolean field
- Soft delete methods in `BaseRepository`
- Never hard delete data

### 3. Audit Pattern

All entities include audit fields:
- `created_at`, `updated_at`
- `created_by`, `updated_by`
- `version` (optimistic locking)

### 4. Workflow Integration Pattern

1. Define workflow in BPMN file
2. Deploy via Workflow Adapter Service
3. Start process instance with variables
4. Track process instance key in entity
5. Query tasks and history via Workflow Adapter Service

### 5. Permission Pattern

Use `PermissionGate` component for all permission-based UI:
- `mode="hide"`: Hide element if no permission
- `mode="readonly"`: Show but make readonly
- `mode="disabled"`: Show but disable

### 6. Tab Pattern

Use `Tabs` component for tab-based pages:
- Lazy load tab content
- Support icons and badges
- Consistent styling

### 7. List Page Pattern

Use `ListPageLayout` + `SearchBar` for list pages:
- Standardized header
- Search and filter integration
- Consistent layout

---

## 📝 Development Guidelines

### Backend Development

1. **Service Creation:**
   - Extend `BaseEntity` for entities
   - Use `BaseRepository` for repositories
   - Use `BaseService` for services
   - Use `ApiResponse<T>` for all API responses

2. **Database:**
   - Use Flyway for migrations
   - Table naming: `{service}_{entity}` (e.g., `contract_coa_allocations`)
   - Always include audit fields

3. **API Design:**
   - Version all APIs: `/api/v1/...`
   - Use RESTful conventions
   - Document with OpenAPI

4. **Workflow Integration:**
   - Use `WorkflowAdapter` interface
   - Store `processInstanceKey` in entity
   - Handle workflow exceptions gracefully

### Frontend Development

1. **Component Creation:**
   - Use framework components when possible
   - Follow atomic design (atoms → molecules → organisms)
   - Use TypeScript for all components

2. **State Management:**
   - Redux Toolkit for global state
   - React Query for server state
   - Local state for UI-only state

3. **API Integration:**
   - Use service files (e.g., `contractService.ts`)
   - Use React Query for data fetching
   - Handle errors consistently

4. **Permission Control:**
   - Always use `PermissionGate` for permission-based UI
   - Define permissions in `rolePermissions.ts`
   - Use cached permission checks

5. **Styling:**
   - Use TailwindCSS
   - Follow design system
   - Support dark mode

### Testing

1. **Backend:**
   - Unit tests for all business logic
   - Integration tests for all APIs
   - Contract tests for API compatibility

2. **Frontend:**
   - Unit tests for components
   - Integration tests for user flows
   - E2E tests for critical paths

---

## 🔗 Key Files and Locations

### Backend

- **Common Core**: `backend/common-core/src/main/java/com/hhsa/common/core/`
- **Common Infrastructure**: `backend/common-infrastructure/src/main/java/com/hhsa/common/infrastructure/`
- **Workflow Adapter**: `backend/workflow-adapter-service/src/main/java/com/hhsa/workflow/`
- **API Gateway**: `backend/api-gateway-service/src/main/resources/application.yml`

### Frontend

- **Authorization Framework**: `frontend/src/frameworks/authorization/`
- **UI Framework**: `frontend/src/frameworks/ui/`
- **Business Rules**: `frontend/src/frameworks/business-rules/`
- **Workflow Framework**: `frontend/src/frameworks/workflow/`
- **Grid Framework**: `frontend/src/frameworks/grid/`

### Configuration

- **Database**: PostgreSQL `eprocurement` database
- **Keycloak**: `http://localhost:8090`
- **Zeebe Gateway**: `localhost:26500`
- **API Gateway**: `http://localhost:8080`

---

## 📚 Additional Resources

### Requirements
- `Contract Financials and Contract Budget Design.pdf`: Primary requirements document

### Legacy Code
- `Legacy code/HHSPortal/`: Legacy codebase (for business context only, NOT for implementation patterns)

### Rules
- `.cursor/rules/project-rules.mdc`: Critical project rules and guidelines

---

## 🎯 Quick Start for New Developers

1. **Read this document** to understand frameworks
2. **Review requirements PDF** for business context
3. **Check existing services** for implementation patterns
4. **Use framework components** instead of creating from scratch
5. **Follow development guidelines** for consistency

---

**Note**: This document is maintained as the single source of truth for all frameworks. Update it when adding new frameworks or making significant changes.

