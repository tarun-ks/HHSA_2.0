import { apiClient } from './apiClient';
import type { ApiResponse } from './apiClient';

export interface Document {
  id: number;
  fileName: string;
  contentType: string;
  fileSize: number;
  storageKey: string;
  storageProvider: string;
  entityType?: string;
  entityId?: number;
  category?: string;
  description?: string;
  uploadedBy?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Document service for managing documents
 * Uses existing Document Service backend (port 8092)
 */
export const documentService = {
  /**
   * Upload a document
   */
  async uploadDocument(
    file: File,
    entityType?: string,
    entityId?: number,
    category?: string,
    description?: string
  ): Promise<ApiResponse<Document>> {
    const formData = new FormData();
    formData.append('file', file);
    if (entityType) formData.append('entityType', entityType);
    if (entityId) formData.append('entityId', entityId.toString());
    if (category) formData.append('category', category);
    if (description) formData.append('description', description);

    const response = await apiClient.post<ApiResponse<Document>>(
      '/api/v1/documents/upload',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response;
  },

  /**
   * Get documents by entity
   */
  async getDocumentsByEntity(
    entityType: string,
    entityId: number
  ): Promise<ApiResponse<Document[]>> {
    const response = await apiClient.get<ApiResponse<Document[]>>(
      `/api/v1/documents/entity/${entityType}/${entityId}`
    );
    return response;
  },

  /**
   * Download a document
   */
  async downloadDocument(documentId: number): Promise<Blob> {
    // Use getClient() to access axios instance for blob response
    const client = apiClient.getClient();
    const response = await client.get<Blob>(
      `/api/v1/documents/${documentId}/download`,
      {
        responseType: 'blob',
      }
    );
    return response.data;
  },

  /**
   * Delete a document
   */
  async deleteDocument(documentId: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete<ApiResponse<void>>(
      `/api/v1/documents/${documentId}`
    );
    return response;
  },

  /**
   * Get document metadata
   */
  async getDocument(documentId: number): Promise<ApiResponse<Document>> {
    const response = await apiClient.get<ApiResponse<Document>>(
      `/api/v1/documents/${documentId}`
    );
    return response;
  },
};

