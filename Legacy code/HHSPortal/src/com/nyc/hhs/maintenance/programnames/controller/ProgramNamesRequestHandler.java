package com.nyc.hhs.maintenance.programnames.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.BaseController;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.maintenance.programnames.model.PaginationBean;
import com.nyc.hhs.maintenance.programnames.services.ProgramNameSvc;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.HHSUtil;


/**
 * This class is basically used to handle all the actions requested by the user
 * from Program Maintenance screen in Maintenance Tab. It process all the request by executing
 * certain transactions and redirect user to the required page as per the
 * result. When user pull the page at the first time handleActionRequestInternal method will
 * redirect to handleRenderRequestInternal method which will render UI to show the required result to the user.
 *  
 */
@Controller(value = "programNamesDetailHandler")
@RequestMapping("view")
public class ProgramNamesRequestHandler extends BaseController {
	private final long DEFAULT_CUR_PAGE_NUM = 1;
	private final long DEFAULT_ROWS_PER_PAGE = 20;

	private static final LogInfo LOG_OBJECT = new LogInfo(ProgramNamesRequestHandler.class);

	private ProgramNameSvc programNamesSvc;
	
	public ProgramNameSvc getProgramNamesSvc() {
		return programNamesSvc;
	}

	public void setProgramNamesSvc(ProgramNameSvc programNamesSvc) {
		this.programNamesSvc = programNamesSvc;
	}
	
	@ModelAttribute("paginationBean")
	public PaginationBean getCommandObject() {
		return new PaginationBean();
	}
	
    /**
     * This method is a rendering handler after action base on parameters from actions
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @RenderMapping
    protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse) 
            throws ApplicationException {

        String loView = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID );
        if(loView ==  null ||  loView.isEmpty() ){
            //In case that thare is no view name from action, set view default 
            loView = ApplicationConstants.DEFAULT_VIEW_PROGRAM_NAMES;
        }

        ModelAndView loModelandView = new ModelAndView(loView);

        @SuppressWarnings("unchecked")
        Map<String,?> pNameLst = (Map<String,?>) aoRequest.getPortletSession().getAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME);

        if( pNameLst == null){
           pNameLst = programNamesSvc.fetchProgramList( DEFAULT_CUR_PAGE_NUM, DEFAULT_ROWS_PER_PAGE , ApplicationConstants.EMPTY_STRING 
                   , ApplicationConstants.PROGRAM_NAME_SORT_DEFAULT );
        }
        loModelandView.addAllObjects(pNameLst);

        aoRequest.getPortletSession().removeAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME);

        return loModelandView;
    }


	/**
	 * This method is a default action handler before rendering operation
	 * This is only used when user access very first time.
	 * 
	 * @param aoRequest request object
	 * @param aoResponse response object
	 */
	@ActionMapping
	protected void handleActionRequestInternal(ActionRequest aoRequest,	ActionResponse aoResponse) throws Exception {

        Map<String,?> pNameLst = programNamesSvc.fetchProgramList( DEFAULT_CUR_PAGE_NUM, DEFAULT_ROWS_PER_PAGE , null 
                , ApplicationConstants.PROGRAM_NAME_SORT_DEFAULT );

        // Start: Passing parameter for Rendering Phase         
        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DEFAULT_VIEW_PROGRAM_NAMES);
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);
        //Passing parameter for Rendering Phase
	}

    /**
     * This method is Action of paging with search criteria or without 
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=paging")
	protected void moveAnotherPage(  ActionRequest aoRequest,	ActionResponse aoResponse ) throws Exception{
        
        Map<String,?> pNameLst = programNamesSvc.fetchProgramList( programNamesSvc.extractPagingParamFromRequest(aoRequest) );

        // Start: Passing parameter for Rendering Phase         
        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, ApplicationConstants.DEFAULT_VIEW_PROGRAM_NAMES);
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);
        //Passing parameter for Rendering Phase

	}


    /**
     * This method is Action of soft deleting program  
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=delete")
	protected void deleteProgram(  ActionRequest aoRequest,	ActionResponse aoResponse ) throws Exception{
		long loProgramId	= 0; 
		try {
			loProgramId = Long.parseLong(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_ID ));
		} catch (NumberFormatException e) {
			throw new Exception();
		}
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);

		programNamesSvc.inactivateProgram(loProgramId, lsUserId);

        // Start: Passing parameter for Rendering Phase
		aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_WORD, 
				ApplicationConstants.EMPTY_STRING);
		aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_PARAM_CUR_PAGE, 
				String.valueOf(DEFAULT_CUR_PAGE_NUM)  );
		aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_PARAM_ROW_IN_PAGE, 
				aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_ROW_IN_PAGE)  );
        // End: Passing parameter for Rendering Phase
	}


    /**
     * This Action is to show pop-up to get new program name and agency  
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
	@ActionMapping(params = "submit_action=addNewProgramStep1")
	protected void addNewProgramAction(ActionRequest aoRequest, ActionResponse aoResponse){
	    
        // Start: Passing parameter for Rendering Phase
        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DIALOG_VIEW_NEW_PROGRAM_NAME);
        // End: Passing parameter for Rendering Phase

	}

    /**
     * This Action is to show confirmation pop-up for user to confirm new program name and agency.  
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
	@ActionMapping(params = "submit_action=createNewProgramStep")
    protected void createNewProgramAction(ActionRequest aoRequest, ActionResponse aoResponse){
	    //Getting parameters from view
        Map<String,Object> pNameLst = new HashMap<String,Object>();
        String lsPname = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_POPUP );
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_POPUP, lsPname);
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_AGENCY_ID_POPUP, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_AGENCY_ID_POPUP ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_AGENCY_NAME_POPUP, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_AGENCY_NAME_POPUP ));
        
        PortletSession loSession = aoRequest.getPortletSession();
        String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);
        
        String lsAgencyId = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_AGENCY_ID_CONFIRM );
        
        loSession.setAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_POPUP, lsPname);

        int liDupCheck = programNamesSvc.programNameDupCheck(lsPname, lsUserId, lsAgencyId);
        if(liDupCheck == 0){
        	pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_DUP_PROGRAM_NAME, ApplicationConstants.NO );
        } else {
        	pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_DUP_PROGRAM_NAME, ApplicationConstants.YES);
        }

        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_POPUP, lsPname);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DIALOG_VIEW_CONFIRM_NEW_PROGRAM_NAME);
        // End: Passing parameter for Rendering Phase
    }
    /**
     * This Action is to navigate back to previous step for new program name.  
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=createProgramNameBack")
    protected void reviewNewProgramBackAction(ActionRequest aoRequest, ActionResponse aoResponse){
        //Getting parameters from view
        Map<String,Object> pNameLst = new HashMap<String,Object>();

        String lsPname = (String)aoRequest.getPortletSession().getAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_POPUP);
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_POPUP, lsPname);
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_AGENCY_ID_POPUP, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_AGENCY_ID_POPUP ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_AGENCY_NAME_POPUP, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_AGENCY_NAME_POPUP ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_RESTORED_AGENCY, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_RESTORED_AGENCY ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_RESTORED_INPUT, lsPname.replaceAll("\"", "&quot;"));

        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DIALOG_VIEW_NEW_PROGRAM_NAME);
        // End: Passing parameter for Rendering Phase
    }
    /**
     * This Action is to create program name with user confirmation.  
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=confirmNewProgramStep")
    protected void confirmNewProgramAction(ActionRequest aoRequest, ActionResponse aoResponse){
        PortletSession loSession = aoRequest.getPortletSession();
        String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);
        
        //String lsProgramName = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_CONFIRM );
        String lsProgramName = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_POPUP);
        String lsAgencyId = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_AGENCY_ID_CONFIRM );
        
        int liRet = programNamesSvc.addNewProgram(lsProgramName, lsUserId, lsAgencyId);
        LOG_OBJECT.Info("return value = " + liRet);
        
        Map<String,?> pNameLst = programNamesSvc.fetchProgramList( DEFAULT_CUR_PAGE_NUM, DEFAULT_ROWS_PER_PAGE ,lsProgramName
                , ApplicationConstants.PROGRAM_NAME_SORT_DEFAULT );

        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DEFAULT_VIEW_PROGRAM_NAMES);
        // End: Passing parameter for Rendering Phase
    }

    /**
     * This Action is to show pop-up to get new program name for program name change  
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=modifyProgramNameStep1")
    protected void modifyProgramNameAction(ActionRequest aoRequest, ActionResponse aoResponse){
        Map<String,Object> pNameLst = new HashMap<String,Object>();

        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_OLD_PROGRAM_NAME, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_OLD_PROGRAM_NAME ));


        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_OLD_PROGRAM_NAME, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_OLD_PROGRAM_NAME ));
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE ));
        
        
        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DIALOG_VIEW_PROGRAM_NAME_CHANGE);
        // End: Passing parameter for Rendering Phase
    }
    
    /**
     * This Action is to show confirmation pop-up for user to confirm program name change.  
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=modifyProgramNameStep2")
    protected void receiveNewProgramName4ChangeAction(ActionRequest aoRequest, ActionResponse aoResponse){
        //Getting parameters from view
        Map<String,Object> pNameLst = new HashMap<String,Object>();
        PortletSession loSession = aoRequest.getPortletSession();

        String lsAgencyId = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_AGENCY_ID_POPUP );
        String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);        
        
        String lsNameChg = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME );
        String lsOldName = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_OLD_PROGRAM_NAME);
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME, lsNameChg);
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_OLD_PROGRAM_NAME, lsOldName);

        int liDupCheck = programNamesSvc.programNameDupCheck(lsNameChg, lsUserId, lsAgencyId);
        if(liDupCheck == 0){
        	pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_DUP_PROGRAM_NAME, ApplicationConstants.NO );
        } else {
        	pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_DUP_PROGRAM_NAME, ApplicationConstants.YES);
        }

        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);

        aoRequest.getPortletSession().setAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME, lsNameChg);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, ApplicationConstants.DIALOG_VIEW_CONFIRM_PROGRAM_NAME_CHANGE);
        // End: Passing parameter for Rendering Phase
    }

    /**
     * This Action is to show confirmation pop-up for user to confirm program name change.  
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=modifyProgramNameBack")
    protected void receiveNewProgramNameBackAction(ActionRequest aoRequest, ActionResponse aoResponse){
        //Getting parameters from view
        Map<String,Object> pNameLst = new HashMap<String,Object>();

        String lsPname = (String)aoRequest.getPortletSession().getAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME) ;
        String lsOldName = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_OLD_PROGRAM_NAME);
        String LsPid  = (String)aoRequest.getPortletSession().getAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE);
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE, LsPid);
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_OLD_PROGRAM_NAME, lsOldName);
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_CONFIRM, lsPname );
        //pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_RESTORED_INPUT, lsPname.replaceAll("\"", "&quot;"));

        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DIALOG_VIEW_PROGRAM_NAME_CHANGE);
        // End: Passing parameter for Rendering Phase
    }
    
    /**
     * This Action is to change program name with user confirmation.  
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=confirmProgramNameChange")
    protected void confirmProgramNameChangeAction(ActionRequest aoRequest, ActionResponse aoResponse) throws Exception{
        PortletSession loSession = aoRequest.getPortletSession();
        String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);
        
//        String lsProgramName = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_CONFIRM );

        String lsProgramName = (String)aoRequest.getPortletSession().getAttribute(ApplicationConstants.PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME);

        long loProgramId    = 0; 
        try {
            loProgramId = Long.parseLong(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE ));
        } catch (NumberFormatException e) {
            throw new Exception();
        }

        programNamesSvc.programNameChange(loProgramId, lsProgramName, lsUserId);

        Map<String,?> pNameLst = programNamesSvc.fetchProgramList( DEFAULT_CUR_PAGE_NUM, DEFAULT_ROWS_PER_PAGE , lsProgramName 
                , ApplicationConstants.PROGRAM_NAME_SORT_DEFAULT );

        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DEFAULT_VIEW_PROGRAM_NAMES);
        // End: Passing parameter for Rendering Phase
    }

    /**
     * 
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=inactivateProgramStep")
    protected void inactivateProgramNameAction(ActionRequest aoRequest, ActionResponse aoResponse){

        Map<String,Object> pNameLst = new HashMap<String,Object>();
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_ID, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_ID ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_NAME, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_NAME ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_AGNCY, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_AGNCY ));

        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DIALOG_VIEW_PROGRAM_INACTIVATE);
        // End: Passing parameter for Rendering Phase
    }
    
    /**
     * 
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=confirmProgramInactivation")
    protected void confirmProgramInactivationAction(ActionRequest aoRequest, ActionResponse aoResponse) throws Exception{
        
        PortletSession loSession = aoRequest.getPortletSession();
        String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);

        String lsProgramName = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_NAME );
        long loProgramId    = 0; 
        try {
            loProgramId = Long.parseLong(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_ID ));
        } catch (NumberFormatException e) {
            throw new Exception();
        }

        programNamesSvc.inactivateProgram(loProgramId, lsUserId);

        Map<String,?> pNameLst = programNamesSvc.fetchProgramList( DEFAULT_CUR_PAGE_NUM, DEFAULT_ROWS_PER_PAGE , lsProgramName 
                , ApplicationConstants.PROGRAM_NAME_SORT_DEFAULT );

        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DEFAULT_VIEW_PROGRAM_NAMES);
        // End: Passing parameter for Rendering Phase
    }

    
    /**
     * 
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=activateProgramStep")
    protected void activateProgramAction(ActionRequest aoRequest, ActionResponse aoResponse){

        Map<String,Object> pNameLst = new HashMap<String,Object>();

        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_ID, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_ID ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_NAME, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_NAME ));
        pNameLst.put(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_AGNCY, aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_AGNCY ));
        
        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DIALOG_VIEW_PROGRAM_ACTIVATE);
        // End: Passing parameter for Rendering Phase
    }
    
    /**
     * 
     * 
     * @param aoRequest request object
     * @param aoResponse response object
     */
    @ActionMapping(params = "submit_action=confirmProgramActivation")
    protected void confirmProgramActivationAction(ActionRequest aoRequest, ActionResponse aoResponse) throws Exception{
        PortletSession loSession = aoRequest.getPortletSession();
        String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);
        
        String lsProgramName = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_NAME );
        long loProgramId    = 0; 
        try {
            loProgramId = Long.parseLong(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_TARGET_PROGRAM_ID ));
        } catch (NumberFormatException e) {
            throw new Exception();
        }

        programNamesSvc.activateProgram(loProgramId, lsUserId);

        Map<String,?> pNameLst = programNamesSvc.fetchProgramList( DEFAULT_CUR_PAGE_NUM, DEFAULT_ROWS_PER_PAGE , lsProgramName 
                , ApplicationConstants.PROGRAM_NAME_SORT_DEFAULT);

        // Start: Passing parameter for Rendering Phase
        aoRequest.getPortletSession().setAttribute(ApplicationConstants.VIEW_PARAMETER_OBJECT_PROGRAM_NAME, pNameLst);

        aoResponse.setRenderParameter(ApplicationConstants.PROGRAM_NAME_RANDER_VIEW_ID, 
                ApplicationConstants.DEFAULT_VIEW_PROGRAM_NAMES);
        // End: Passing parameter for Rendering Phase
    }


    /**
     * This Action is for type ahead AJAX call 
     * 
     * @param ResourceRequest aoResourceRequest
     * @param ResourceResponse aoResourceResponse
     */
    @ResourceMapping("programNameListUrl")
    public void getProgramNameList(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
    {
        List<String> loProgranNameList = new ArrayList<String>(); 

        try
        {
            String lsQueryStringFromReq = aoResourceRequest.getParameter(HHSConstants.QUERY);
            final int liMinLength = Integer.valueOf(HHSConstants.THREE);
            String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
                    ApplicationConstants.KEY_SESSION_USER_ID);
            UserThreadLocal.setUser(lsUserId);

            loProgranNameList = programNamesSvc.getProgramNamesForTypeHead(lsQueryStringFromReq);

            aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
            final String lsOutputJSONaoResponse = HHSUtil
                    .generateDelimitedResponse(loProgranNameList, lsQueryStringFromReq, liMinLength).toString()
                    .trim();
            aoResourceResponse.getWriter().print(lsOutputJSONaoResponse);

        }
        catch (Exception loEx)
        {
            LOG_OBJECT.Error("error occurred while getting procurement epin list :", loEx);
        }
    }
	
	
	
}




