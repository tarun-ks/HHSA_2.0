package com.nyc.hhs.thread;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;

public abstract class DocumentVaultParallelProcessHandler
{
	private static final LogInfo LOG_OBJECT = new LogInfo(DocumentVaultParallelProcessHandler.class);

	public abstract void postProcessHandler(Channel aoChannel, ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException;
/**
 * @param aoDPP DocumentVaultParallelProcessor object
 * @param aoRequest ActionRequest object
 * @param aoResponse ActionResponse object
 * @throws ApplicationException when any exception occurred we wrap it into this custom exception
	 */
	public void postProcessHandler(DocumentVaultParallelProcessor aoDPP, ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException
	{
		try
		{
			if (aoDPP != null && aoDPP.getChannel() != null)
			{
				Channel loChannel = aoDPP.getChannel();
				Object loProcessCompleteObj = loChannel.getData("processComplete");
				if (loProcessCompleteObj != null)
				{
					// Check if passes or fails
					if ((Boolean) loProcessCompleteObj)
					{
						this.postProcessHandler(loChannel, aoRequest, aoResponse);
						aoRequest.getPortletSession().removeAttribute("currentProcessThreadObject");
					}
					else
					{
						ApplicationException loEx = (ApplicationException) loChannel.getData("exception");
						aoRequest.getPortletSession().setAttribute("type", "Error", PortletSession.APPLICATION_SCOPE);
						aoRequest.getPortletSession().setAttribute("isLinkedToAPP", "in progress",
								PortletSession.APPLICATION_SCOPE);
						aoRequest.getPortletSession().setAttribute("message", loEx.getRootCause(),
								PortletSession.APPLICATION_SCOPE);
						aoRequest.getPortletSession().setAttribute("messageType", "failed",
								PortletSession.APPLICATION_SCOPE);
						aoResponse.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
					}
				}
				else
				{
					aoRequest.getPortletSession().setAttribute("type", "in progress", PortletSession.APPLICATION_SCOPE);
					aoRequest.getPortletSession().setAttribute("isLinkedToAPP", "in progress",
							PortletSession.APPLICATION_SCOPE);
					aoRequest.getPortletSession().setAttribute("message", "in progress",
							PortletSession.APPLICATION_SCOPE);
					aoRequest.getPortletSession().setAttribute("messageType", "in progress",
							PortletSession.APPLICATION_SCOPE);
					aoResponse.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
				}
			}else{
				LOG_OBJECT.Error("Error occured in checkDeleteProgress");
			}
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in checkDeleteProgress", aoEx);
		}
	}
}
