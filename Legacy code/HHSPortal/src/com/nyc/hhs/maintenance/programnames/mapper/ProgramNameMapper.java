package com.nyc.hhs.maintenance.programnames.mapper;

import java.util.List;

import com.nyc.hhs.maintenance.programnames.model.PaginationBean;
import com.nyc.hhs.maintenance.programnames.model.ProgramNamesBean;
import com.nyc.hhs.model.AgencyDetailsBean;


public interface ProgramNameMapper {
	 public List<ProgramNamesBean> getAllProgramNames(PaginationBean aoPageInfo);
	 
	 public PaginationBean getPageInfo( PaginationBean aoPageInfo );

	 public List<ProgramNamesBean>  getProgramNamesForTypeHead(PaginationBean aoPageInfo);

	 public List<AgencyDetailsBean> getAgencyList();

	 public int addNewProgram( ProgramNamesBean programVo );

	 public int programNameDupCheck( ProgramNamesBean programVo );

	 public int programNameChange( ProgramNamesBean programVo );

	 public int inactivateProgram( ProgramNamesBean programVo );

	 public int activateProgram( ProgramNamesBean programVo );
}
