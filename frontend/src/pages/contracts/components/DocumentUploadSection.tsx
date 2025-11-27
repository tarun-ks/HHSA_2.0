import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from '../../../components/atoms/Button';
import { documentService, Document } from '../../../services/documentService';
import { useToast } from '../../../hooks/useToast';
import { Loader } from '../../../components/atoms/Loader';

interface DocumentUploadSectionProps {
  contractId: number;
  entityType?: string;
}

/**
 * Document Upload Section Component
 * Allows users to upload, view, and manage documents for a contract
 */
export const DocumentUploadSection = ({ contractId, entityType = 'Contract' }: DocumentUploadSectionProps) => {
  const toast = useToast();
  const queryClient = useQueryClient();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploadCategory, setUploadCategory] = useState<string>('CONTRACT');
  const [uploadDescription, setUploadDescription] = useState<string>('');

  const { data: documentsData, isLoading } = useQuery({
    queryKey: ['documents', entityType, contractId],
    queryFn: () => documentService.getDocumentsByEntity(entityType, contractId),
    enabled: !!contractId,
  });

  const uploadMutation = useMutation({
    mutationFn: (file: File) =>
      documentService.uploadDocument(file, entityType, contractId, uploadCategory, uploadDescription),
    onSuccess: (response) => {
      if (response.success) {
        toast.success('Document uploaded successfully!');
        queryClient.invalidateQueries({ queryKey: ['documents', entityType, contractId] });
        setSelectedFile(null);
        setUploadDescription('');
      } else {
        toast.error(response.error?.description || 'Failed to upload document');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (documentId: number) => documentService.deleteDocument(documentId),
    onSuccess: (response) => {
      if (response.success) {
        toast.success('Document deleted successfully!');
        queryClient.invalidateQueries({ queryKey: ['documents', entityType, contractId] });
      } else {
        toast.error(response.error?.description || 'Failed to delete document');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  const handleUpload = () => {
    if (!selectedFile) {
      toast.error('Please select a file to upload');
      return;
    }
    uploadMutation.mutate(selectedFile);
  };

  const handleDownload = async (doc: Document) => {
    try {
      const blob = await documentService.downloadDocument(doc.id);
      const url = window.URL.createObjectURL(blob);
      const a = window.document.createElement('a');
      a.href = url;
      a.download = doc.fileName;
      window.document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      window.document.body.removeChild(a);
    } catch (error: any) {
      toast.error('Failed to download document');
    }
  };

  const handleDelete = (documentId: number) => {
    if (window.confirm('Are you sure you want to delete this document?')) {
      deleteMutation.mutate(documentId);
    }
  };

  const documents = documentsData?.data || [];

  return (
    <div className="space-y-4">
      {/* Upload Section */}
      <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4 space-y-4">
        <h3 className="text-md font-semibold text-gray-900 dark:text-white">Upload Document</h3>
        
        <div className="space-y-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              File
            </label>
            <input
              type="file"
              onChange={handleFileSelect}
              className="block w-full text-sm text-gray-500 dark:text-gray-400
                file:mr-4 file:py-2 file:px-4
                file:rounded-full file:border-0
                file:text-sm file:font-semibold
                file:bg-blue-50 file:text-blue-700
                hover:file:bg-blue-100
                dark:file:bg-gray-700 dark:file:text-gray-300"
            />
            {selectedFile && (
              <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
                Selected: {selectedFile.name} ({(selectedFile.size / 1024).toFixed(2)} KB)
              </p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Category
            </label>
            <select
              value={uploadCategory}
              onChange={(e) => setUploadCategory(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md dark:bg-gray-700 dark:text-white dark:border-gray-600"
            >
              <option value="CONTRACT">Contract Document</option>
              <option value="BUDGET">Budget Document</option>
              <option value="ATTACHMENT">Attachment</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Description (Optional)
            </label>
            <input
              type="text"
              value={uploadDescription}
              onChange={(e) => setUploadDescription(e.target.value)}
              placeholder="Document description"
              className="w-full px-3 py-2 border border-gray-300 rounded-md dark:bg-gray-700 dark:text-white dark:border-gray-600"
            />
          </div>

          <Button
            variant="primary"
            onClick={handleUpload}
            disabled={!selectedFile || uploadMutation.isPending}
            isLoading={uploadMutation.isPending}
          >
            Upload Document
          </Button>
        </div>
      </div>

      {/* Documents List */}
      <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
        <h3 className="text-md font-semibold text-gray-900 dark:text-white mb-4">Uploaded Documents</h3>
        
        {isLoading ? (
          <div className="flex justify-center py-8">
            <Loader size="md" />
          </div>
        ) : documents.length === 0 ? (
          <p className="text-sm text-gray-500 dark:text-gray-400 text-center py-8">
            No documents uploaded yet
          </p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
              <thead className="bg-gray-50 dark:bg-gray-700">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                    Document Name
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                    Type
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                    Size
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                    Uploaded By
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                    Date
                  </th>
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                {documents.map((doc) => (
                  <tr key={doc.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                    <td className="px-4 py-3 text-sm text-gray-900 dark:text-white">
                      {doc.fileName}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-300">
                      {doc.category || 'N/A'}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-300">
                      {(doc.fileSize / 1024).toFixed(2)} KB
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-300">
                      {doc.uploadedBy || 'N/A'}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-300">
                      {new Date(doc.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-3 text-right text-sm font-medium space-x-2">
                      <button
                        onClick={() => handleDownload(doc)}
                        className="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
                      >
                        Download
                      </button>
                      <button
                        onClick={() => handleDelete(doc.id)}
                        className="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                        disabled={deleteMutation.isPending}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

