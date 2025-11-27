package com.nyc.hhs.portlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;

/**
 * This controller is for displaying statistic to the user when city user logged
 * in.
 */

public class HomeSystemStaticsPortletView extends AbstractController implements ResourceAwareController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(HomeSystemStaticsPortletView.class);

	/**
	 * This method is handle all the rendering activities. also method sets the
	 * values in the RenderRequest reference, so that same values can be
	 * displayed on the required jsp.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return loModelAndView
	 * @throws PortletException
	 * @throws IOException
	 */
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView("homesystemstaticsfinal");
		return loModelAndView;
	}

	/**
	 * This method performs the required action, by setting the required values
	 * in the channel object and thereafter executing the transaction.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @throws PortletException
	 * @throws IOException
	 * @throws UnavailableException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		Channel loChannel = new Channel();
		loChannel.setData("asProviderStatus", ApplicationConstants.STATUS_APPROVED);
		loChannel.setData("asOrgId", "asOrgId");
		try
		{
			// transaction that will get the provider status
			TransactionManager.executeTransaction(loChannel, "getProviderStatus");
			Integer liApprovedProviderStatus = (Integer) loChannel.getData("liApprovedProviderStatus");
			List<Map<String, Object>> loDraftReviewRevisionStatusMap = (List<Map<String, Object>>) loChannel
					.getData("loDraftReviewRevisionStatusMap");
			if (loDraftReviewRevisionStatusMap != null && !loDraftReviewRevisionStatusMap.isEmpty())
			{
				for (Map<String, Object> loMap : loDraftReviewRevisionStatusMap)
				{
					if (loMap.get("APPLICATION_STATUS").equals(
							ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DRAFT))
					{
						aoRequest.setAttribute("draftStatus", loMap.get("COUNT"));
					}
					if (loMap.get("APPLICATION_STATUS").equals(
							ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_IN_REVIEW))
					{
						aoRequest.setAttribute("inReviewStatus", loMap.get("COUNT"));
					}
					if (loMap.get("APPLICATION_STATUS").equals(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
					{
						aoRequest.setAttribute("returnedForRevisionStatus", loMap.get("COUNT"));
					}
				}
			}
			if (liApprovedProviderStatus != null)
			{
				// set the provider status
				aoRequest.setAttribute("approvedProviderStatus", liApprovedProviderStatus);
			}
			aoRequest.setAttribute("visibiltyFlag", true);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception occurred while getting approved provider status", aoExp);
		}
		ModelAndView loModelAndView = new ModelAndView("homesystemstaticsfinal");
		return loModelAndView;
	}
}
