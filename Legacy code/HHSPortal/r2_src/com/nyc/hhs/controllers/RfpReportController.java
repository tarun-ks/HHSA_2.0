package com.nyc.hhs.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.EPinDetailBean;

/* QC 9401 R 8.5 Let the city user select Epin,
 * find the Procurement ID and display the record from Procurement table
 * If user click on Generate Report button make Ajax call (front end) to HHSUtilServer 
 * to generate RFP report for Procurement ID.
 * Excel file would be downloaded to the use local folder.
 */

@Controller(value = "rfpreportController")
@RequestMapping("view")
public class RfpReportController extends BaseController
{
	/**
	 * This the log object which is used to log any error into log file when any
	 * exception occurred
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(RfpReportController.class);

	/**
	 * Default Render Action Created 
	 * @param aoRequest Render Request
	 * @param aoResponse Render Response
	 * @return Model and View
	 * @throws ApplicationException 
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse) throws ApplicationException
	{  
		return getEpinRfpRenderRequest(aoRequest, aoResponse);
	}

	/**
	 * Default Resource Action Created 
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 * @throws ApplicationException return ModelAndView
	 */
	@Override
	@ResourceMapping
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{  
		ModelAndView loModelandView = generateSelectEpinScreen(aoRequest, aoResponse);
		return loModelandView;
	}

	/** QC9401 R 8.5.0 - generates XMl RFP Report file and download it for user
	 * Select EPIN Render Action
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response return Model And View 
	 * @throws ApplicationException 
	 */
	@RenderMapping(params = "render_action=enterEpin") 
	protected ModelAndView getEpinRfpRenderRequest(RenderRequest aoRequest, RenderResponse aoResponse) throws ApplicationException
	{
		setExceptionMessageInResponse(aoRequest);
		ModelAndView loModelandView = generateSelectEpinScreen(aoRequest, aoResponse);
		return loModelandView;
	}

	/**
	 * This method generates Select EPIN Screen for User 
	 * <ul>
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response return String
	 * @throws ApplicationException 
	 */
	@SuppressWarnings("unchecked")
	protected ModelAndView generateSelectEpinScreen(PortletRequest aoRequest, PortletResponse aoResponse) throws ApplicationException  //String
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsView = "rfpreport";
		ModelAndView loModelandView = new ModelAndView(lsView); 		
		try
		{
			List<EPinDetailBean> rfpEpinLst  = (List)loSession.getAttribute( "rfpEpinLst", PortletSession.APPLICATION_SCOPE);
			String errMessage = (String)loSession.getAttribute("errMessage",  PortletSession.APPLICATION_SCOPE);
			
			if (errMessage != null && !errMessage.isEmpty())
			{	
				loModelandView.addObject("errMessage", errMessage);
			}
			if (rfpEpinLst !=  null )
			{				
		        loModelandView.addObject("rfpEpinLst", rfpEpinLst); 
		 	}
			
			loSession.removeAttribute("rfpEpinLst", PortletSession.APPLICATION_SCOPE);	
			loSession.removeAttribute("errMessage", PortletSession.APPLICATION_SCOPE);
			
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error(ApplicationConstants.ERROR_MSG_SELECT_ORGANIZATION_LOAD_FAIL + loEx);
			setGenericErrorMessage(aoRequest);
		}
		
		return loModelandView;
	}

    /**
     * This is submit action for Multi-Account Login Created for R4
     * @param aoRequest Action Request
     * @param aoResponse Action Response
     */
    @ActionMapping(params = "submit_action=findRFP")
    protected void findRfpForEpinAction(ActionRequest aoRequest, ActionResponse aoResponse)
    {  
        findProcurement(aoRequest, aoResponse);
    }
    
    /**
	 * This method find Procurement ID for Selected EPIN
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response return String
	 */
    @SuppressWarnings("unchecked")
    protected void findProcurement(ActionRequest aoRequest, ActionResponse aoResponse)
    {	
    	PortletSession loSession = aoRequest.getPortletSession();
       	String lsEpin = null;
		String lsActionStatus = ApplicationConstants.SUCCESS;
		String lsProcurementId = ApplicationConstants.EMPTY_STRING;
		String lsAgencyId = ApplicationConstants.EMPTY_STRING;
		String errMessage = ApplicationConstants.EMPTY_STRING;
		EPinDetailBean 	ePinDetailBean = null;
								
		try
		{   LOG_OBJECT.Debug("findProcurement-------------- ");
			lsEpin = aoRequest.getParameter("epinEntry"); 
			LOG_OBJECT.Debug("aoRequest.getParameter(lsEpin) ::  "+lsEpin);
			
			if (null != lsEpin && !lsEpin.isEmpty() )
			{
				Channel loChannelObj = new Channel();
				loChannelObj.setData("asEpinId", lsEpin);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_PROCUREMENT_FOR_EPIN);
				
				List<EPinDetailBean> rfpEpinLst = (List<EPinDetailBean>) loChannelObj.getData("loProcurementList");
				
				if(rfpEpinLst !=null && !rfpEpinLst.isEmpty())
				{
					for (EPinDetailBean loBin : rfpEpinLst)
					{
						lsEpin = loBin.getEpinId();
						loSession.setAttribute("epinId", loBin.getEpinId(), PortletSession.APPLICATION_SCOPE);
						loSession.setAttribute("procurementId", loBin.getProcurementId(), PortletSession.APPLICATION_SCOPE);
						lsProcurementId = loBin.getProcurementId();
						lsAgencyId = loBin.getAgencyId();
						loSession.setAttribute("agencyId", loBin.getAgencyId(), PortletSession.APPLICATION_SCOPE);
						loSession.setAttribute("Status", loBin.getStatus(), PortletSession.APPLICATION_SCOPE);
						loSession.setAttribute("procurementTitle", loBin.getProcurementTitle(), PortletSession.APPLICATION_SCOPE);
						LOG_OBJECT.Debug(" ------epinId ::  "+loBin.getEpinId());
						LOG_OBJECT.Debug(" ------procurementId ::  "+loBin.getProcurementId());
						LOG_OBJECT.Debug(" ------agencyId ::  "+loBin.getAgencyId());
						LOG_OBJECT.Debug(" ------status ::  "+loBin.getStatus());
						LOG_OBJECT.Debug(" ------procurementTitle ::  "+loBin.getProcurementTitle());
					}
				
					loSession.setAttribute("rfpEpinLst", rfpEpinLst, PortletSession.APPLICATION_SCOPE);
					// create XMLS file
		        	lsAgencyId = (String) loSession.getAttribute( "agencyId", PortletSession.APPLICATION_SCOPE);
		        	lsEpin = (String) loSession.getAttribute( "epinId", PortletSession.APPLICATION_SCOPE);
					String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
					String nameOfFile = lsAgencyId + "_" + lsEpin + "_" + date + ".xlsx";
					LOG_OBJECT.Debug("------Name of File ::  "+nameOfFile);
					loSession.setAttribute("nameOfFile", nameOfFile, PortletSession.APPLICATION_SCOPE);
					
				}
				else 
				{
					errMessage =  ApplicationConstants.ERROR_MESSAGE_REPORT +" for EPIN: "+lsEpin;
		
			    }
				loSession.setAttribute("errMessage", errMessage, PortletSession.APPLICATION_SCOPE);
			}
				
			
		}	
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		catch (Exception loEx)
		{
			lsActionStatus = HHSConstants.AS_FAILURE;
			LOG_OBJECT.Error(ApplicationConstants.ERROR_MSG_UNABLE_TO_REDIRECT + loEx);
			setExceptionMessageFromAction((ActionResponse) aoResponse,
					ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, "enterEpin");
					
		}
		finally
		{
			if (null != lsActionStatus && lsActionStatus.startsWith(HHSConstants.AS_FAILURE))
			{
				setExceptionMessageFromAction((ActionResponse) aoResponse,
						ApplicationConstants.ERROR_MSG_AUTHORIZATION2, ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, "enterEpin");
						
			}
		}
		         
    }
  
   
}