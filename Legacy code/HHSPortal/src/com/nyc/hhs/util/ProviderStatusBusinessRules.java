package com.nyc.hhs.util;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ApplicationSummary;
import com.nyc.hhs.model.ProviderStatusBean;

/**
 * This class give the provider status for business and service application for
 * various scenarios such as on 'expire', 'completion', 'removal', 'withdrawal'
 * and 'rejection'
 * 
 */

public class ProviderStatusBusinessRules
{
	/**
	 * This Method Returns the Provider Status on Expire
	 * 
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusOnExpiry() throws ApplicationException
	{
		return ApplicationConstants.STATUS_EXPIRED;
	}

	/**
	 * This Method fetches the provider Status at the submission of BR
	 * Application
	 * 
	 * @param asCurrentProviderStatus Current Provider Status
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusAtBRApplicationSubmission(String asCurrentProviderStatus)
			throws ApplicationException
	{
		String lsReturnStatus = "";
		if (ApplicationConstants.STATUS_EXPIRED.equalsIgnoreCase(asCurrentProviderStatus)
				|| ApplicationConstants.STATUS_NOT_APPLIED.equalsIgnoreCase(asCurrentProviderStatus)
				|| ApplicationConstants.STATUS_WITHDRAWN.equalsIgnoreCase(asCurrentProviderStatus)
				|| ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentProviderStatus)
				|| ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentProviderStatus)
				|| ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentProviderStatus))
		{

			lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
		}
		else
		{
			if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentProviderStatus)
					|| ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentProviderStatus))
			{
				lsReturnStatus = asCurrentProviderStatus;
			}

		}
		return lsReturnStatus;
	}

	/**
	 * This Method fetches the provider Status at the submission of Service
	 * Application
	 * 
	 * @param asCurrentProviderStatus Current Provider Status
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusAtServiceApplicationSubmission(String asCurrentProviderStatus,
			String asCurrentBRStatus) throws ApplicationException
	{
		String lsReturnStatus = "";
		if ((ApplicationConstants.STATUS_WITHDRAWN.equalsIgnoreCase(asCurrentProviderStatus)
				|| ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentProviderStatus)
				|| ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentProviderStatus) || ApplicationConstants.STATUS_DEFFERED
				.equalsIgnoreCase(asCurrentProviderStatus))
				&& (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_IN_REVIEW.equalsIgnoreCase(asCurrentBRStatus) || ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS
						.equalsIgnoreCase(asCurrentBRStatus)))
		{

			lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
		}
		else
		{
			lsReturnStatus = asCurrentProviderStatus;

		}
		return lsReturnStatus;
	}

	/**
	 * This Method Fetches the Provider Status on Business Application
	 * Completion.
	 * 
	 * @param asCurrentProviderStatus Current Provider Status
	 * @param asCurrentBRStatus Current Business Application Status
	 * @param aoOrgExpDate Organization Expiration Date
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusOnBusinessApplicationCompletion(String asCurrentProviderStatus,
			String asCurrentBRStatus, Date aoOrgExpDate, List<ProviderStatusBean> loProviderStatusBeanList,
			String asBrappId) throws ApplicationException
	{
		String lsReturnStatus = "";
		if (aoOrgExpDate.compareTo(new Date()) > 0)
		{
			if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentProviderStatus))
			{
				if (ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.equalsIgnoreCase(asCurrentBRStatus))
				{
					lsReturnStatus = ApplicationConstants.STATUS_APPROVED;
				}
				else if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
				{
					lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
				}
				else if (ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus))
				{
					lsReturnStatus = ApplicationConstants.STATUS_REJECTED;
				}
			}
			else if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentProviderStatus))
			{
				if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
				{
					lsReturnStatus = getStatusForConditionallyApprovedApplication(asCurrentProviderStatus,
							asCurrentBRStatus, loProviderStatusBeanList, asBrappId);
				}
			}
			else if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
			{
				lsReturnStatus = ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
			}
			else if (ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus))
			{
				lsReturnStatus = ApplicationConstants.STATUS_REJECTED;
			}
			else if (ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus))
			{
				lsReturnStatus = ApplicationConstants.STATUS_SUSPEND;
			}
			else if (ApplicationConstants.DEACTIVATED.equalsIgnoreCase(asCurrentBRStatus))
			{
				lsReturnStatus = ApplicationConstants.STATUS_SUSPEND;
			}
			else if (ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus))
			{
				lsReturnStatus = ApplicationConstants.STATUS_DEFFERED;
			}
			else if (ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.equalsIgnoreCase(asCurrentBRStatus))
			{
				lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
			}
			else
			{
				lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
			}
		}
		else
		{
			lsReturnStatus = ApplicationConstants.STATUS_EXPIRED;
		}

		return lsReturnStatus;
	}

	/**
	 * FIXED FOR DEFECT 1781 This Method calculate the status of provider when
	 * previously the provider status was conditionally approve.
	 * @param asCurrentProviderStatus Current provider status
	 * @param asCurrentBRStatus Current Business Application Status
	 * @param loProviderStatusBeanList List of all BR & service application
	 * @param asBrappId Business Application ID
	 * @return Status of provider.
	 */
	private static String getStatusForConditionallyApprovedApplication(String asCurrentProviderStatus,
			String asCurrentBRStatus, List<ProviderStatusBean> loProviderStatusBeanList, String asBrappId)
	{
		ProviderStatusBean loProviderStatusBean = null;
		String lsStatus = null;
		if (loProviderStatusBeanList != null && !loProviderStatusBeanList.isEmpty())
		{
			Iterator loIterator = loProviderStatusBeanList.iterator();
			while (loIterator.hasNext())
			{
				loProviderStatusBean = (ProviderStatusBean) loIterator.next();
				if (loProviderStatusBean.getApplicationId() != null
						&& !loProviderStatusBean.getApplicationId().equalsIgnoreCase(asBrappId))
				{
					if (loProviderStatusBean.getSupersedingStatus() != null
							&& loProviderStatusBean.getSupersedingStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_APPROVED))
					{
						return ApplicationConstants.STATUS_APPROVED;
					}
					else if (loProviderStatusBean.getSupersedingStatus() != null
							&& loProviderStatusBean.getSupersedingStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
					{
						lsStatus = ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
					}
				}
			}
		}
		else
		{
			lsStatus = ApplicationConstants.STATUS_APPROVED;
		}

		return lsStatus;
	}

	/**
	 * This Method fetches the Provider Status on Service Application
	 * Completion.
	 * 
	 * @param asCurrentProviderStatus Current Provider Status
	 * @param asCurrentBRStatus Current Business Application Status
	 * @param asCurrentServiceApplicationStatus Current Service Application
	 *            Status
	 * @param aoLServiceApplicationStatuses List of Service Application Statuses
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusOnServiceApplicationCompletion(String asCurrentProviderStatus,
			String asCurrentBRStatus, String asCurrentServiceApplicationStatus, List aoLServiceApplicationStatuses)
			throws ApplicationException
	{
		String lsReturnStatus = "";
		if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentProviderStatus))
		{
			lsReturnStatus = asCurrentProviderStatus;
		}
		else
		{
			if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentServiceApplicationStatus))
			{
				if (ApplicationConstants.STATUS_WITHDRAWN.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus))
				{
					lsReturnStatus = asCurrentProviderStatus;
				}
				else if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
				{
					lsReturnStatus = ApplicationConstants.STATUS_APPROVED;
				}
				else if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
				{
					lsReturnStatus = ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
				}
			}
			else if (ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentServiceApplicationStatus))
			{
				lsReturnStatus = getStatusOnServiceReject(asCurrentProviderStatus, asCurrentBRStatus,
						aoLServiceApplicationStatuses, lsReturnStatus);
			}
			else if (ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentServiceApplicationStatus))
			{
				if (ApplicationConstants.STATUS_WITHDRAWN.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus))
				{
					lsReturnStatus = asCurrentProviderStatus;
				}
				else if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
						|| ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
				{
					lsReturnStatus = getReturnStatusOnStatusApprovedAndStatusConditionallyApproved(aoLServiceApplicationStatuses);
				}
			}
			else if (ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS
					.equalsIgnoreCase(asCurrentServiceApplicationStatus))
			{
				lsReturnStatus = getReturnStatusOnStatusReturnedForRevisions(asCurrentProviderStatus,
						asCurrentBRStatus, aoLServiceApplicationStatuses, lsReturnStatus);
			}
			else if (ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentServiceApplicationStatus))
			{
				lsReturnStatus = getStatusOnServiceSuspend(asCurrentProviderStatus, asCurrentBRStatus,
						aoLServiceApplicationStatuses, lsReturnStatus);
			}
			else if (ApplicationConstants.DEACTIVATED.equalsIgnoreCase(asCurrentServiceApplicationStatus))
			{
				lsReturnStatus = getStatusOnServiceSuspend(asCurrentProviderStatus, asCurrentBRStatus,
						aoLServiceApplicationStatuses, lsReturnStatus);
			}
		}
		return lsReturnStatus;
	}

	/**
	 * This method Return Status On Status Returned For Revisions.
	 * @param asCurrentProviderStatus
	 * @param asCurrentBRStatus
	 * @param aoLServiceApplicationStatuses
	 * @param lsReturnStatus
	 * @return
	 */
	private static String getReturnStatusOnStatusReturnedForRevisions(String asCurrentProviderStatus,
			String asCurrentBRStatus, List aoLServiceApplicationStatuses, String asReturnStatus)
	{
		if (ApplicationConstants.STATUS_WITHDRAWN.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus))
		{
			asReturnStatus = asCurrentProviderStatus;
		}
		else if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
		{
			if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
			{
				asReturnStatus = asCurrentProviderStatus;
			}
			else
			{
				asReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
			}
		}
		return asReturnStatus;
	}

	/**
	 * This method return status on Status Approved AndStatus Conditionally
	 * Approved.
	 * @param aoLServiceApplicationStatuses
	 * @return
	 */
	private static String getReturnStatusOnStatusApprovedAndStatusConditionallyApproved(
			List aoLServiceApplicationStatuses)
	{
		String lsReturnStatus;
		if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_IN_REVIEW)
				|| aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
		{
			lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
		}
		else
		{
			lsReturnStatus = ApplicationConstants.STATUS_DEFFERED;
		}
		return lsReturnStatus;
	}

	/**
	 * This Method calculate provider status on service application Reject
	 * @param asCurrentProviderStatus Current Provider status
	 * @param asCurrentBRStatus Current Business Application status
	 * @param aoLServiceApplicationStatuses All service application statuses
	 * @param asReturnStatus status
	 * @return
	 */
	private static String getStatusOnServiceReject(String asCurrentProviderStatus, String asCurrentBRStatus,
			List aoLServiceApplicationStatuses, String asReturnStatus)
	{
		if (ApplicationConstants.STATUS_WITHDRAWN.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus))
		{
			asReturnStatus = asCurrentProviderStatus;
		}
		else if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
		{
			if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_IN_REVIEW)
					|| aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
			{
				asReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
			{
				asReturnStatus = ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_DEFFERED))
			{
				asReturnStatus = ApplicationConstants.STATUS_DEFFERED;
			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_WITHDRAWN))
			{
				asReturnStatus = ApplicationConstants.STATUS_WITHDRAWN;
			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_SUSPEND))
			{
				asReturnStatus = ApplicationConstants.STATUS_SUSPEND;
			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.DEACTIVATED))
			{
				asReturnStatus = ApplicationConstants.STATUS_SUSPEND;
			}
			else
			{
				asReturnStatus = ApplicationConstants.STATUS_REJECTED;
			}
		}
		return asReturnStatus;
	}

	/**
	 * This Method calculate provider status on service application suspend
	 * @param asCurrentProviderStatus Current Provider status
	 * @param asCurrentBRStatus Current Business Application status
	 * @param aoLServiceApplicationStatuses All service application statuses
	 * @param lsReturnStatus status
	 * @return calculated status
	 */
	private static String getStatusOnServiceSuspend(String asCurrentProviderStatus, String asCurrentBRStatus,
			List aoLServiceApplicationStatuses, String asReturnStatus)
	{
		if (ApplicationConstants.STATUS_WITHDRAWN.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus))
		{
			asReturnStatus = asCurrentProviderStatus;
		}
		else if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_IN_REVIEW.equalsIgnoreCase(asCurrentBRStatus))
		{
			if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_IN_REVIEW)
					|| aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
			{
				asReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
			{
				asReturnStatus = ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_DEFFERED))
			{
				asReturnStatus = ApplicationConstants.STATUS_DEFFERED;
			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_WITHDRAWN))
			{
				asReturnStatus = ApplicationConstants.STATUS_WITHDRAWN;
			}
			else
			{
				asReturnStatus = ApplicationConstants.STATUS_SUSPEND;
			}
		}

		return asReturnStatus;
	}

	/**
	 * This Method Fetches the Provider Status On Conditional Approval of
	 * Service.
	 * @param asCurrentProviderStatus Current Provider Status
	 * @param asCurrentBRStatus Current Business Application Status
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusOnServiceConditionalApproval(String asCurrentProviderStatus,
			String asCurrentBRStatus) throws ApplicationException
	{
		String lsReturnStatus = "";

		if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentProviderStatus))
		{
			lsReturnStatus = asCurrentProviderStatus;
		}
		else
		{
			if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
					|| ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
			{
				//BEGIN - QC 8515 Release 6.1.0	
				//If BR is Conditionally Approved and
				// Service Application that is Conditionally Approved or Approved 
				//set Provider Status as Approved
				//lsReturnStatus = ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
				lsReturnStatus = ApplicationConstants.STATUS_APPROVED;
				//end - fix for QC 8515 Release 6.1.0	
			}
			else
			{
				lsReturnStatus = asCurrentProviderStatus;
			}
		}

		return lsReturnStatus;
	}

	/**
	 * This Method fetches the provider Status on removal of Service Conditional
	 * Approval.
	 * @param asCurrentBRStatus Current Business Application Status
	 * @param aoLServiceApplicationStatuses List of Service Application Status
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusOnRemovalOfConditionalApproval(String asCurrentBRStatus,
			List aoLServiceApplicationStatuses) throws ApplicationException
	{
		String lsReturnStatus = "";
		if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = getReturnStatus(aoLServiceApplicationStatuses, lsReturnStatus);

		}
		else if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = getReturnStatus(aoLServiceApplicationStatuses, lsReturnStatus);
		}
		else if (ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_REJECTED;
		}
		else if (ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_SUSPEND;
		}
		else if (ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_DEFFERED;
		}
		else if (ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS;
		}
		else if (ApplicationConstants.STATUS_DRAFT.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_EXPIRED;
		}
		else if (ApplicationConstants.STATUS_IN_REVIEW.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
		}
		return lsReturnStatus;
	}

	/**
	 * This Method Fetches the Provider Status On BR Withdrawal.
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusOnBRWithdrawal() throws ApplicationException
	{
		String lsReturnStatus = ApplicationConstants.STATUS_WITHDRAWN;
		return lsReturnStatus;
	}

	/**
	 * This Method fetches the Provider Status On BR WithDrawal Rejection
	 * @param asCurrentBRStatus Current Business Application Status
	 * @param aoLServiceApplicationStatuses List of Service Application Status
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusOnBRWithdrawalRejection(String asCurrentBRStatus,
			List aoLServiceApplicationStatuses) throws ApplicationException
	{
		String lsReturnStatus = "";
		if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = getReturnStatus(aoLServiceApplicationStatuses, lsReturnStatus);

		}
		else if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = getReturnStatus(aoLServiceApplicationStatuses, lsReturnStatus);
		}
		else if (ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_REJECTED;
		}
		else if (ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_SUSPEND;
		}
		else if (ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_DEFFERED;
		}
		else if (ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS;
		}
		else if (ApplicationConstants.STATUS_DRAFT.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_NOT_APPLIED;
		}
		else if (ApplicationConstants.STATUS_IN_REVIEW.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
		}
		return lsReturnStatus;
	}

	/**
	 * This Method is used to fetch the provider Status on Service Application
	 * Withdrawal.
	 * @param asCurrentProviderStatus Current Provider Status
	 * @param asCurrentBRStatus Current Business Application Status
	 * @param asCurrentServiceStatus Current Service Status
	 * @param aoLServiceApplicationStatuses List of Service Application Status
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusOnServiceApplicationWithdrawal(String asCurrentProviderStatus,
			String asCurrentBRStatus, String asCurrentServiceStatus, List aoLServiceApplicationStatuses)
			throws ApplicationException
	{
		String lsReturnStatus = "";
		// need to check for br status of deferred, withdrawn, rejected and
		// suspended
		if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus)
				|| ApplicationConstants.STATUS_IN_REVIEW.equalsIgnoreCase(asCurrentBRStatus))
		{
			if ((asCurrentServiceStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) || asCurrentServiceStatus
					.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
					&& !(aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_APPROVED)
							|| aoLServiceApplicationStatuses
									.contains(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED)
							|| aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_IN_REVIEW) || aoLServiceApplicationStatuses
							.contains(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)))
			{
				lsReturnStatus = ApplicationConstants.STATUS_WITHDRAWN;
			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_APPROVED)
					|| aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
			{
				lsReturnStatus = asCurrentProviderStatus;

			}
			else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_IN_REVIEW)
					|| aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
			{
				lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;

			}
			else
			{
				lsReturnStatus = ApplicationConstants.STATUS_WITHDRAWN;
			}
		}
		else
		{
			lsReturnStatus = asCurrentProviderStatus;
		}
		return lsReturnStatus;
	}

	/**
	 * This Method is used to fetch the Provider Status on Service Application
	 * WithDrawal Rejection .
	 * @param asCurrentBRStatus Current Business Application Status
	 * @param aoLServiceApplicationStatuses List of Service Application Status
	 * @return Status
	 * @throws ApplicationException
	 */
	public static String getProviderStatusOnServiceApplicationWithdrawalRejection(String asCurrentBRStatus,
			List aoLServiceApplicationStatuses) throws ApplicationException
	{
		String lsReturnStatus = "";
		if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = getReturnStatus(aoLServiceApplicationStatuses, lsReturnStatus);

		}
		else if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = getReturnStatus(aoLServiceApplicationStatuses, lsReturnStatus);
		}
		else if (ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_REJECTED;
		}
		else if (ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_SUSPEND;
		}
		else if (ApplicationConstants.STATUS_DEFFERED.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_DEFFERED;
		}
		else if (ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.equalsIgnoreCase(asCurrentBRStatus))
		{
			lsReturnStatus = ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS;
		}
		return lsReturnStatus;
	}

	/**
	 * This Method returns Status
	 * 
	 * @param aoLServiceApplicationStatuses
	 * @param lsReturnStatus
	 * @return
	 */
	private static String getReturnStatus(List aoLServiceApplicationStatuses, String lsReturnStatus)
	{
		if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_APPROVED))
		{
			lsReturnStatus = ApplicationConstants.STATUS_APPROVED;
		}
		else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
		{
			lsReturnStatus = ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
		}
		else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_IN_REVIEW))
		{
			lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
		}
		else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
		{
			/** Rel 4.1.0 QC 6787 BEGIN */
			lsReturnStatus = ApplicationConstants.STATUS_IN_REVIEW;
			/** Rel 4.1.0 QC 6787 END*/
		}
		else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_WITHDRAWN))
		{
			lsReturnStatus = ApplicationConstants.STATUS_WITHDRAWN;
		}
		else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_DEFFERED))
		{
			lsReturnStatus = ApplicationConstants.STATUS_DEFFERED;
		}
		else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_SUSPEND))
		{
			lsReturnStatus = ApplicationConstants.STATUS_SUSPEND;
		}
		else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.DEACTIVATED))
		{
			lsReturnStatus = ApplicationConstants.STATUS_SUSPEND;
		}
		else if (aoLServiceApplicationStatuses.contains(ApplicationConstants.STATUS_REJECTED))
		{
			lsReturnStatus = ApplicationConstants.STATUS_REJECTED;
		}
		return lsReturnStatus;
	}

	/**
	 * This Method is used to fetch the Provider Status on Service Application
	 * Status with Business application Conditionally Approved.
	 * @param asCurrentBRStatus Current Business Application Status
	 * @param loApplicationSummaryBeanList List of Service Application Status
	 * @param asBrappId business Application Id
	 * @return Status
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public static String calculateStatusForConditionallyApprovedApplication(String asCurrentBRStatus,
			List<ApplicationSummary> loApplicationSummaryBeanList, String asBrappId) throws ApplicationException
	{
		ApplicationSummary loApplicationSummaryBean = null;
		//Defect #6201 Fix: Making Provider status Blank by default.
		String lsStatus = "";
		int liServiceAppCount = 0;
		int liDefferedSACount = 0;
		int liInReviewSACount = 0;
		int liRejectedSACount = 0;
		int liWithdrawnSACount = 0;
		int liReturnedSACount = 0;
		int liSuspendedSACount = 0;

		if (loApplicationSummaryBeanList != null && !loApplicationSummaryBeanList.isEmpty())
		{
			Iterator loIterator = loApplicationSummaryBeanList.iterator();
			while (loIterator.hasNext())
			{
				loApplicationSummaryBean = (ApplicationSummary) loIterator.next();

				if (null != loApplicationSummaryBean)
				{

					if (null != loApplicationSummaryBean.getMsSuperSedingEntityType()
							&& null != loApplicationSummaryBean.getMsSuperSedingStatus()
							&& loApplicationSummaryBean.getMsSuperSedingStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
					{
						/* BEGIN - QC 8515 Release 6.1.0	Scenario 8 */
						//return ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
						return ApplicationConstants.STATUS_APPROVED;
						/* END - QC 8515 Release 6.1.0	Scenario 8 */
						
					}
					else if (null != loApplicationSummaryBean.getMsSuperSedingEntityType()
							&& null != loApplicationSummaryBean.getMsSuperSedingStatus()
							&& loApplicationSummaryBean.getMsSuperSedingStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_SUSPEND))
					{
						liSuspendedSACount++;
					}
					else if (null != loApplicationSummaryBean.getMsAppStatus()
							&& loApplicationSummaryBean.getMsAppStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_APPROVED))
					{
						//QC 8515 Scenario 8
						//return ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
						return ApplicationConstants.STATUS_APPROVED;
					}
					else if (null != loApplicationSummaryBean.getMsAppStatus()
							&& loApplicationSummaryBean.getMsAppStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_IN_REVIEW))
					{
						liInReviewSACount++;

					}
					else if (null != loApplicationSummaryBean.getMsAppStatus()
							&& loApplicationSummaryBean.getMsAppStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
					{
						liReturnedSACount++;

					}
					else if (null != loApplicationSummaryBean.getMsAppStatus()
							&& loApplicationSummaryBean.getMsAppStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_WITHDRAWN))
					{
						liWithdrawnSACount++;

					}
					else if (null != loApplicationSummaryBean.getMsAppStatus()
							&& loApplicationSummaryBean.getMsAppStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_REJECTED))
					{
						liRejectedSACount++;

					}
					else if (null != loApplicationSummaryBean.getMsAppStatus()
							&& loApplicationSummaryBean.getMsAppStatus().equalsIgnoreCase(
									ApplicationConstants.STATUS_DEFFERED))
					{
						liDefferedSACount++;

					}
				}
			}
			liServiceAppCount = loApplicationSummaryBeanList.size();

			if (liServiceAppCount == liInReviewSACount + liReturnedSACount)
			{
				lsStatus = ApplicationConstants.STATUS_IN_REVIEW;
				return lsStatus;
			}
			else if (liWithdrawnSACount >= 1 && liInReviewSACount == 0 && liReturnedSACount == 0)
			{
				lsStatus = ApplicationConstants.STATUS_WITHDRAWN;
			}
			else if (liSuspendedSACount >= 1 && liWithdrawnSACount == 0 && liDefferedSACount == 0)
			{
				lsStatus = ApplicationConstants.STATUS_SUSPEND;
			}
			else if (liRejectedSACount >= 1 && liWithdrawnSACount == 0 && liDefferedSACount == 0
					&& liSuspendedSACount == 0)
			{
				lsStatus = ApplicationConstants.STATUS_REJECTED;
			}
			else if (liDefferedSACount >= 1 && liWithdrawnSACount == 0)
			{
				lsStatus = ApplicationConstants.STATUS_DEFFERED;
			}
		}
		return lsStatus;
	}
}
