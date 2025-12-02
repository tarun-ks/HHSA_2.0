import { } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from '../../components/atoms/Button';
import { Card } from '../../components/atoms/Card';
import { Input } from '../../components/atoms/Input';
import { contractService, ContractCreateRequest } from '../../services/contractService';
import { useToast } from '../../hooks/useToast';

const contractSchema = z.object({
  contractNumber: z.string().min(1, 'Contract number is required').max(30, 'Max 30 characters'),
  contractTitle: z.string().min(1, 'Contract title is required').max(255, 'Max 255 characters'),
  contractValue: z.number().positive('Contract value must be greater than 0'),
  contractAmount: z.number().positive('Contract amount must be greater than 0'),
  contractStartDate: z.string().min(1, 'Start date is required'),
  contractEndDate: z.string().min(1, 'End date is required'),
  agencyId: z.string().min(1, 'Agency ID is required'),
  programId: z.string().optional(),
  providerId: z.string().min(1, 'Provider ID is required'),
  organizationId: z.string().min(1, 'Organization ID is required'),
  ePin: z.string().min(1, 'E-PIN is required'),
  procurementId: z.string().optional(),
}).refine((data) => {
  const startDate = new Date(data.contractStartDate);
  const endDate = new Date(data.contractEndDate);
  return startDate < endDate;
}, {
  message: 'End date must be after start date',
  path: ['contractEndDate'],
});

type ContractFormData = z.infer<typeof contractSchema>;

/**
 * Contract creation page.
 * Form for creating a new contract.
 */
export const ContractCreatePage = () => {
  const navigate = useNavigate();
  const toast = useToast();
  const queryClient = useQueryClient();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ContractFormData>({
    resolver: zodResolver(contractSchema),
  });

  const createMutation = useMutation({
    mutationFn: (data: ContractCreateRequest) => contractService.createContract(data),
    onSuccess: (response) => {
      console.log('Contract creation response:', response);
      if (response.success && response.data) {
        toast.success('Contract created successfully!');
        queryClient.invalidateQueries({ queryKey: ['contracts'] });
        navigate(`/contracts/${response.data.id}`);
      } else {
        console.error('Contract creation failed:', response.error);
        toast.error(response.error?.description || 'Failed to create contract');
      }
    },
    onError: (error: any) => {
      console.error('Contract creation error:', error);
      console.error('Error response:', error.response?.data);
      const errorMessage = error.response?.data?.error?.description 
        || error.response?.data?.message 
        || error.message 
        || 'An error occurred while creating the contract';
      toast.error(errorMessage);
    },
  });

  const onSubmit = (data: ContractFormData) => {
    console.log('Form submitted with data:', data);
    console.log('Form errors:', errors);
    
    // Ensure ePin field name matches backend expectation
    const requestData: ContractCreateRequest = {
      contractNumber: data.contractNumber,
      contractTitle: data.contractTitle,
      contractValue: data.contractValue,
      contractAmount: data.contractAmount,
      contractStartDate: data.contractStartDate,
      contractEndDate: data.contractEndDate,
      agencyId: data.agencyId,
      programId: data.programId,
      providerId: data.providerId,
      organizationId: data.organizationId,
      ePin: data.ePin, // Explicitly set to ensure field name is correct
      procurementId: data.procurementId,
    };
    console.log('Sending request:', requestData);
    createMutation.mutate(requestData);
  };

  const handleFormError = (errors: any) => {
    console.error('Form validation errors:', errors);
    toast.error('Please fix the form errors before submitting');
  };

  return (
    <div className="container-page section-spacing">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <h1 className="text-responsive-xl font-bold text-gray-900 dark:text-white">Create Contract</h1>
        <Button 
          variant="outline" 
          onClick={() => navigate('/contracts')}
          className="w-full sm:w-auto"
        >
          Cancel
        </Button>
      </div>

      <Card>
        <form onSubmit={handleSubmit(onSubmit, handleFormError)} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-6">
            <Input
              {...register('contractNumber')}
              label="Contract Number"
              error={errors.contractNumber?.message}
              placeholder="CT-2024-001"
            />
            <Input
              {...register('ePin')}
              label="E-PIN"
              error={errors.ePin?.message}
              placeholder="E-PIN from APT system"
            />
          </div>

          <Input
            {...register('contractTitle')}
            label="Contract Title"
            error={errors.contractTitle?.message}
            placeholder="Procurement/Contract title"
          />

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-6">
            <Input
              {...register('contractValue', { valueAsNumber: true })}
              label="Contract Value"
              type="number"
              step="0.01"
              error={errors.contractValue?.message}
              placeholder="0.00"
            />
            <Input
              {...register('contractAmount', { valueAsNumber: true })}
              label="Contract Amount"
              type="number"
              step="0.01"
              error={errors.contractAmount?.message}
              placeholder="0.00"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-6">
            <Input
              {...register('contractStartDate')}
              label="Start Date"
              type="date"
              error={errors.contractStartDate?.message}
            />
            <Input
              {...register('contractEndDate')}
              label="End Date"
              type="date"
              error={errors.contractEndDate?.message}
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-6">
            <Input
              {...register('agencyId')}
              label="Agency ID"
              error={errors.agencyId?.message}
            />
            <Input
              {...register('programId')}
              label="Program ID"
              error={errors.programId?.message}
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-6">
            <Input
              {...register('providerId')}
              label="Provider ID"
              error={errors.providerId?.message}
            />
            <Input
              {...register('organizationId')}
              label="Organization ID"
              error={errors.organizationId?.message}
            />
          </div>

          <Input
            {...register('procurementId')}
            label="Procurement ID"
            error={errors.procurementId?.message}
          />

          <div className="flex flex-col sm:flex-row justify-end gap-3 sm:gap-4 pt-4 border-t border-gray-200 dark:border-gray-700">
            <Button
              type="button"
              variant="outline"
              onClick={() => navigate('/contracts')}
              disabled={createMutation.isPending}
              className="w-full sm:w-auto order-2 sm:order-1"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              isLoading={createMutation.isPending || isSubmitting}
              disabled={createMutation.isPending || isSubmitting}
              className="w-full sm:w-auto order-1 sm:order-2"
            >
              Create Contract
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};


