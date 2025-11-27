package com.nyc.hhs.maintenance.programnames.model;


public class ProgramNamesBean {

	private Long	programId			= null;	
	private String	programName			= null;
	private String	activeFlag			= null;	
	private String	createdDate			= null;

	private String	createdByUserid		= null;	
	private String	modifiedDate		= null;	
	private String	modifiedByUserid	= null;

	private String  agencyId 			= null;
	private long    ref_cnt             = 0;

	public ProgramNamesBean(){
		super();
	}
	
	public ProgramNamesBean(long programId, String programName,  String userId ){
		this.programId = programId;
		this.programName = programName;
		this.activeFlag = "1";
		this.createdByUserid = userId;
		this.modifiedByUserid = userId;
	}

   public ProgramNamesBean( String programName,  String userId, String agencyId ){
        this.programName = programName;
        this.activeFlag = "1";
        this.agencyId  = agencyId;
        this.createdByUserid = userId;
        this.modifiedByUserid = userId;
    }

	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedByUserid() {
		return createdByUserid;
	}
	public void setCreatedByUserid(String createdByUserid) {
		this.createdByUserid = createdByUserid;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedByUserid() {
		return modifiedByUserid;
	}
	public void setModifiedByUserid(String modifiedByUserid) {
		this.modifiedByUserid = modifiedByUserid;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

    public long getRef_cnt() {
        return ref_cnt;
    }

    public void setRef_cnt(long ref_cnt) {
        this.ref_cnt = ref_cnt;
    }



    
    
}


