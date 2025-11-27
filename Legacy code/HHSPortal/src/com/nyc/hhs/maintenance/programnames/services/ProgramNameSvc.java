package com.nyc.hhs.maintenance.programnames.services;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;

import com.nyc.hhs.maintenance.programnames.model.PaginationBean;


public interface ProgramNameSvc {

    public PaginationBean extractPagingParamFromRequest(ActionRequest aoRequest);
	public Map<String,?> fetchProgramList(long curPage, long rowsInPage, String searchWord,  String searchSortOrder);
	public Map<String,?> fetchProgramList(PaginationBean loPageInfo);
	
    public int programNameDupCheck(String programName, String userId, String agencyId);
	public int addNewProgram(String programName, String userId, String agencyId);
    public int programNameChange(long programId, String programName, String userId);
    
	public int inactivateProgram(long programId, String userId);
	public int activateProgram(long programId, String userId);

	public Map<String,?> getAgencyList();
	public List<String> getProgramNamesForTypeHead(String searchWord);
	

}
