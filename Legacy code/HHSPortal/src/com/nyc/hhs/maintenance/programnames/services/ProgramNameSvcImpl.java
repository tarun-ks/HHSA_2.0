package com.nyc.hhs.maintenance.programnames.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.maintenance.programnames.mapper.ProgramNameMapper;
import com.nyc.hhs.maintenance.programnames.model.PaginationBean;
import com.nyc.hhs.maintenance.programnames.model.ProgramNamesBean;
import com.nyc.hhs.model.AgencyDetailsBean;


public class ProgramNameSvcImpl implements ProgramNameSvc{
    private final long DEFAULT_CUR_PAGE_NUM = 1;
    private final long DEFAULT_ROWS_PER_PAGE = 20;

	private ProgramNameMapper programNameMapper;

	public ProgramNameMapper getProgramNameMapper() {
		return programNameMapper;
	}

	public void setProgramNameMapper(ProgramNameMapper programNameMapper) {
		this.programNameMapper = programNameMapper;
	}

	public Map<String,?> fetchProgramList(PaginationBean loPageInfo){
        Map<String,Object> modelMap = new HashMap<String,Object>();

        loPageInfo.escape();
        //fetchAgencyNames
        List<ProgramNamesBean> lst= programNameMapper.getAllProgramNames(loPageInfo);
        if( lst == null ){
            modelMap.put(ApplicationConstants.SEARCHED_PROGRAM_LST,  new ArrayList<ProgramNamesBean>() );
        } else{
            modelMap.put(ApplicationConstants.SEARCHED_PROGRAM_LST,  lst );
        }

        PaginationBean loPageResult = programNameMapper.getPageInfo(loPageInfo);
        loPageInfo.unEscape(); 
        loPageInfo.copySearchParam(loPageResult);

        modelMap.put(ApplicationConstants.SEARCHED_PROGRAM_PAGE_INFO, loPageResult  );

        List<AgencyDetailsBean> agencyLst = programNameMapper.getAgencyList();

        modelMap.put(ApplicationConstants.AGENCY_LIST, agencyLst  );

        
        return modelMap;
	}

	public Map<String,?> fetchProgramList(long curPage, long rowsInPage, String searchWord, String searchSortOrder){

		PaginationBean loPageInfo = new PaginationBean(curPage, rowsInPage, searchWord, searchSortOrder);
		
		return fetchProgramList( loPageInfo);
	}

	public Map<String,?> getAgencyList(){
	    Map<String,Object> modelMap = new HashMap<String,Object>();
        List<AgencyDetailsBean> agencyLst = programNameMapper.getAgencyList();

        modelMap.put(ApplicationConstants.AGENCY_LIST+"1", agencyLst  );
        
        return modelMap;
	}
	
	public int addNewProgram(String programName, String userId, String agencyId){
		ProgramNamesBean loPgVo = new ProgramNamesBean(programName, userId, agencyId);
		
		return programNameMapper.addNewProgram(loPgVo);

	}
	
	public int programNameDupCheck(String programName, String userId, String agencyId){
		ProgramNamesBean loPgVo = new ProgramNamesBean(programName, userId, agencyId);
		
		return programNameMapper.programNameDupCheck(loPgVo);
	}


    @Override
    public List<String> getProgramNamesForTypeHead(String searchWord) {
        
        PaginationBean loPagingVo = new PaginationBean();
        loPagingVo.setSearchWord(searchWord);

        List<ProgramNamesBean> lst = programNameMapper.getProgramNamesForTypeHead(loPagingVo);
        List<String> programNameLst = new ArrayList<String> ();

        for(ProgramNamesBean pnb :   lst){
            programNameLst.add(pnb.getProgramName());
        }
        
        return programNameLst;
    }

    @Override
    public int programNameChange(long programId, String programName, String userId ) {
        ProgramNamesBean loPgVo = new ProgramNamesBean(programId, programName, userId);

        return programNameMapper.programNameChange(loPgVo);
    }

    @Override
    public int activateProgram(long programId, String userId) {
        ProgramNamesBean loPgVo = new ProgramNamesBean(programId, null, userId);
        
        return programNameMapper.activateProgram(loPgVo);
    }

    public int inactivateProgram(long programId, String userId){
        ProgramNamesBean loPgVo = new ProgramNamesBean(programId, null, userId);

        return programNameMapper.inactivateProgram(loPgVo);
    }

    @Override
    public PaginationBean extractPagingParamFromRequest(ActionRequest aoRequest) {
        PaginationBean loPageInfo = new PaginationBean();

        if(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_WORD) != null ){
            loPageInfo.setSearchWord(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_WORD));
        }

        if(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_AGINCY_ID) != null ){
            loPageInfo.setSearchAgencyId(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_AGINCY_ID));
        }
        
        if(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_CREATED_FROM) != null 
                && aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_CREATED_TO) != null){
            loPageInfo.setSearchCreatedFrom(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_CREATED_FROM));
            loPageInfo.setSearchCreatedTo(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_CREATED_TO));
        }

        if(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_MODIFIED_FROM) != null 
                && aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_MODIFIED_TO) != null){
            loPageInfo.setSearchModifiedFrom(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_MODIFIED_FROM));
            loPageInfo.setSearchModifiedTo(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_MODIFIED_TO));
        }

        if(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_FILTER_STATUS) != null ){
            loPageInfo.setSearchStatus(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_FILTER_STATUS));
        }

        if(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_SORT_ORDER) != null ){
            String loSearchSortOrder = aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_SEARCH_SORT_ORDER);
            loPageInfo.setSearchSortOrder(loSearchSortOrder);
        }else{
            loPageInfo.setSearchSortOrder(ApplicationConstants.PROGRAM_NAME_SORT_DEFAULT);
        }

        if(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_CUR_PAGE) != null ){
            try {
                long curPageNo = Long.parseLong(aoRequest.getParameter(ApplicationConstants.PROGRAM_NAME_PARAM_CUR_PAGE ));
                loPageInfo.setCurrentPage(curPageNo);
            } catch (NumberFormatException e) {
                //Do nothing
            }
        }else{
            loPageInfo.setCurrentPage(DEFAULT_CUR_PAGE_NUM);
        }
        
        return loPageInfo;
    }
}

