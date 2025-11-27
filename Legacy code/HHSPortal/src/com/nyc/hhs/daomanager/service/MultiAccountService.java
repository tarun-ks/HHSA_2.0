package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;

/**
 * @MultiAccountService This Service is for Single User with access to Multiple Accounts functionalities
 * Created for R4
 * 
 */
public class MultiAccountService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(MultiAccountService.class);
	
	/**
	 * This method returns the user list on search using email address in NYC ID field
	 * Created for R4
	 * <ul>
	 * <li> Execute query id <b>searchUserOnEmailId</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL Session
	 * @param aoStaffDetails Staff Details Bean with Search parameters ' Email Address'
	 * @return User list with search suggestions
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> searchUserOnEmailId(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails) throws ApplicationException
	{
		List<StaffDetails> loUsersList = null;
		try
		{
			loUsersList = (List<StaffDetails>) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, HHSConstants.QUERY_ID_SEARCH_USER_ON_EMAIL,
			HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
		}
		catch (ApplicationException loAppEx)
		{
			 LOG_OBJECT.Error("Exception occured while updating role in city user details", loAppEx);
             setMoState("Transaction Failed:: searchUserOnEmailId method -Exception occured while " +
             		"fetching search results for User Email search: /n");
             throw loAppEx;
		}
		setMoState("Transaction Success:: MultiAccountService:searchUserOnEmailId method - User list on search obtained: /n");
		return loUsersList;
	}
	
	/**
	 * <p>
	 * This method is used to get user details , organization Details from Staff details and Staff_Organization table on Provider Login
	 *<ul>
	 *<li>execute query GET_USER_ORG_DETAILS_MULTI_ACCOUNT </li>
	 *</ul>
	 *</p>
	 * Created for R4
	 * @param aoMyBatisSession to connect to database
	 * @param aoStaffDetails organization id and email attribute of bean fetch user organization details 
	 * @return List<StaffDetails> loStaffDetails contains list of user and their organization details
	 * @throws ApplicationException
	 *
	 */  	
	@SuppressWarnings("unchecked")
	public List<StaffDetails> getUserOrgDetailsMultiAccount(SqlSession aoMyBatisSession, StaffDetails aoStaffDetails) throws ApplicationException
	{
		List<StaffDetails> loStaffDetails = null;
		try {
			loStaffDetails =   (List<StaffDetails>) DAOUtil.masterDAO(aoMyBatisSession, aoStaffDetails,
					ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, HHSConstants.QUERY_ID_GET_USER_ORG_DETAILS_MULTI_ACCOUNT, HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
		}catch (ApplicationException loAppEx) {	
			LOG_OBJECT.Error("Exception occured while getting Data from Staff and Organization ", loAppEx);
			setMoState("Transaction Failed:: MultiAccountService:getUserOrgDetails method - failed to get Data from Staff and Organization \n");
			throw loAppEx;
		}
    setMoState("Transaction Success:: MultiAccountService:getUserOrgDetails method - Successfully reterived Data from Staff and Organization \n + ");
		return loStaffDetails;
	}
	
	/**
	 * This method inserts Submit Access Request entry from Accelerator user in Database to tag Provider user with Multiple Organizations.
	 * <ul>
	 * <li> This method sets the StaffDetails object </li>
	 * <li> Execute the query id <b> updateStaffMappingForSubmitAccessRequest </b></li>
	 * <li> Execute the query id <b> insertSubmitAccessRequestProvider </b></li>
	 * </ul>
	 * @param aoMyBatisSession SQL Session
	 * @param aoParamMap This Map contains user information parameters to be inserted into STAFF_ORGANIZATION_MAPPING table
	 * @return liInsertRowCount integer
	 * @throws ApplicationException
	 * Created for R4 
	 */
	public Integer submitAccessRequestProvider(SqlSession aoMyBatisSession, HashMap<String, Object> aoParamMap) throws ApplicationException
	{
		int liInsertRowCount = 0;
		StaffDetails loStaffDetails = null;
		String[] lsOrgId = null;
		try
		{	
			if(null!= aoParamMap)
			{
				String lsStaffId = (String) aoParamMap.get(ApplicationConstants.CHANNEL_PARAM_STAFF_ID);
				String lsCreatorUser = (String) aoParamMap.get(ApplicationConstants.CHANNEL_PARAM_CREATOR_USER);
				lsOrgId = (String[]) aoParamMap.get(ApplicationConstants.ORG_ID);
				loStaffDetails = new StaffDetails();
				loStaffDetails.setMsUserStatus(HHSConstants.PENDING);
				loStaffDetails.setMsMemberStatus(HHSConstants.PENDING);
				loStaffDetails.setMsStaffActiveFlag(ApplicationConstants.SYSTEM_NO);
				loStaffDetails.setMsStaffTitle(HHSConstants.EMPTY_STRING);
				loStaffDetails.setMsPermissionLevel(HHSConstants.EMPTY_STRING);
				loStaffDetails.setMsAdminPermission(HHSConstants.EMPTY_STRING);
				loStaffDetails.setMsSystemUser(ApplicationConstants.SYSTEM_YES);
				loStaffDetails.setMsStaffId(lsStaffId);
				loStaffDetails.setMsCreatedBy(lsCreatorUser);
				loStaffDetails.setMsUserAcctCreationDate(DateUtil.getSqlDate(DateUtil.getCurrentDate()));
				if(null != lsOrgId)
				{
					for(String loCountrer: lsOrgId)
					{
						loStaffDetails.setMsOrgId(loCountrer);
						int liOrgInsertCounter = 0;
						liOrgInsertCounter = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, HHSConstants.QUERY_ID_UPDATE_SUBMIT_ACCESS_REQUEST,
								HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
						if(liOrgInsertCounter==0)
						{
							liOrgInsertCounter = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loStaffDetails, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, HHSConstants.QUERY_ID_INSERT_SUBMIT_ACCESS_REQUEST,
									HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
						}
						liInsertRowCount +=liOrgInsertCounter; 
					}
				}
			}
			
		}
		catch (ApplicationException loAppEx)
		{
			 LOG_OBJECT.Error("Exception occured while updating role in city user details", loAppEx);
             setMoState("Transaction Failed:: searchUserOnEmailId method -Exception occured while " +
             		"fetching search results for User Email search: /n");
             throw loAppEx;
		}
		setMoState("Transaction Success:: MultiAccountService:searchUserOnEmailId method - User list on search obtained: /n");
		return liInsertRowCount;
	}
	
	/**
	 * This method returns the staff details relevant to Organization MApping from database for the given Staff Id on click of Continue button
	 * <ul>
	 * <li> Execute the query id <b> getStaffDetailsFromId </b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL session
	 * @param asStaffId Staff Id
	 * @return List of Staff Organization Mapping
	 * @throws ApplicationException
	 * Created for R4
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> getStaffDetailsFromId(SqlSession aoMyBatisSession, String asStaffId) throws ApplicationException
	{
		List <StaffDetails> loStaffDetailsList = null;
		try
		{
			loStaffDetailsList = (List<StaffDetails>) DAOUtil.masterDAO(aoMyBatisSession, asStaffId, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, HHSConstants.QUERY_ID_GET_STAFF_DETAILS_FROM_ID,
			ApplicationConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loAppEx)
		{
			 LOG_OBJECT.Error("Exception occured while updating role in city user details", loAppEx);
             setMoState("Transaction Failed:: searchUserOnEmailId method -Exception occured while " +
             		"fetching search results for User Email search: /n");
             throw loAppEx;
		}
		setMoState("Transaction Success:: MultiAccountService:searchUserOnEmailId method - User list on search obtained: /n");
		return loStaffDetailsList;
	}

/*[Start] R8.9.0    QC9531   */
	
	/*
	 *   all methods below are up and running on SOS scheduler
	 */
	
	/**
	 *  find Duplicated User DN and clean up  
	 * <ul>
	 * <li> Execute the query id <b> getStaffDetailsFromId </b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL session
	 * @return List of Staff Organization Mapping duplicated
	 * @throws ApplicationException
	 * Created for R8.9.0
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> fetchDupUserDNOnly2Clean(SqlSession aoMyBatisSession ) throws ApplicationException
	{
		List <StaffDetails> loStaffDetailsList = null;
		try
		{
			loStaffDetailsList = (List<StaffDetails>) DAOUtil.masterDAO(aoMyBatisSession, null, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, 
					HHSConstants.FETCH_DUP_USER_DN_ONLY, null);
			
			if( loStaffDetailsList == null || loStaffDetailsList.size() <= 1 ){
				setMoState("Transaction Success:: MultiAccountService:fetchDupUserNOrg method - no Dup data /n");
				return loStaffDetailsList;
			}

			//take USER_DN from first record
			String  loPrevDn = loStaffDetailsList.get(0).getMsUserDN();
			//String  loPrvStaffId= loStaffDetailsList.get(0).getMsStaffId();

			List <StaffDetails> loStaffDupLst = new ArrayList<StaffDetails>()  ;

			/*
			 * take dup user dn and org id put into Q and then clean them up 
			 */
			for( int loInx = 0 ;  loInx  < loStaffDetailsList.size() ; loInx++ ){
				System.out.println(  "  Prev Dn:" +    loPrevDn  + ":loPrvStaffId: "+ loStaffDetailsList.get(loInx).getMsStaffId() + ":Org_id:" + loStaffDetailsList.get(loInx).getMsOrgId()
						+ "  \n    current:" + loStaffDetailsList.get(loInx).getMsUserDN() );

				if( loPrevDn.equalsIgnoreCase( loStaffDetailsList.get(loInx).getMsUserDN() ) ) {
					loStaffDupLst.add( loStaffDetailsList.get(loInx) );
					System.out.println(  " Push :" +      loStaffDetailsList.get(loInx).getMsUserDN() );

					continue;
				}else{
					System.out.println(  " Reset :" +      loStaffDetailsList.get(loInx).getMsUserDN() );
					loPrevDn = loStaffDetailsList.get(loInx).getMsUserDN();

					//List <StaffDetails> loStaffDup_tmp = checkStaffOrgNoLink(aoMyBatisSession, loStaffDupLst); //Case 3
					cleanDupUserDNOnly(aoMyBatisSession, loStaffDupLst); //Case 2

					loStaffDupLst = new ArrayList<StaffDetails>()   ;
					loStaffDupLst.add( loStaffDetailsList.get(loInx) ) ;
				}
			}
			//List <StaffDetails> loStaffDup_tmp = checkStaffOrgNoLink(aoMyBatisSession, loStaffDupLst); //Case 3

			cleanDupUserDNOnly(aoMyBatisSession, loStaffDupLst); //Case 2
		}
		catch (ApplicationException loAppEx)
		{
			 LOG_OBJECT.Error("Exception occured while updating role in city user details", loAppEx);
             setMoState("Transaction Failed:: searchUserOnEmailId method -Exception occured while " +
             		"fetching search results for User Email search: /n");
             throw loAppEx;
		}
		setMoState("Transaction Success:: MultiAccountService:fetchDupUserNOrg method - no Dup data /n");
		return loStaffDetailsList;
	}


	protected  List <StaffDetails>  checkStaffOrgNoLink( SqlSession aoMyBatisSession, List <StaffDetails>  loStaffDupLst) throws ApplicationException{
		List <StaffDetails> loSDetail =  new ArrayList <StaffDetails> ();
		HashMap <String,String> loSid = new HashMap <String,String> ();

		
		for( int loInx = 0 ;  loInx  < loStaffDupLst.size() ; loInx++ ){
			if( !loSid.containsKey( loStaffDupLst.get(loInx).getMsStaffId() )  ){
				loSid.put(loStaffDupLst.get(loInx).getMsStaffId(), loStaffDupLst.get(loInx).getMsUserDN()) ;
			}
			if( loStaffDupLst.get(loInx).getMsOrgId() == null || loStaffDupLst.get(loInx).getMsOrgId().length() ==0 ){
				System.out.println(  "-----REMOVE_DUP_USER_DN No Org:" +    loStaffDupLst.get(loInx).toString() );

 				/* reset user dn */
				DAOUtil.masterDAO(aoMyBatisSession, loStaffDupLst.get(loInx), ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, 
						HHSConstants.REMOVE_DUP_USER_DN, HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
				
				//Add audit 					
				HhsAuditBean aoAudit = getDupUserDnAuditData(loStaffDupLst.get(loInx));
				System.out.println( "_____________  HHSAUDIT_INSERT_FOR_DUP_USER_DN :"  + aoAudit.toString()  );
				DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
						HHSConstants.HHSAUDIT_INSERT_FOR_DUP_USER_DN, HHSConstants.HHS_AUDIT_BEAN_PATH);

				continue;
			}
			loSDetail.add(loStaffDupLst.get(loInx));
		}
		
		if(loSid.size() <= 1) {
			return new ArrayList <StaffDetails>() ;
		}

		return loSDetail ;
	}
	
	
	
	protected void cleanDupUserDNOnly( SqlSession aoMyBatisSession, List<StaffDetails>  aoStaffDupLst) throws ApplicationException{
		if (aoStaffDupLst == null || aoStaffDupLst.size() < 2 ){
			return ;
		}
		System.out.println(  "  Length:" +    aoStaffDupLst.size() );

		HashMap<String,StaffDetails > loRemoveSid = new HashMap<String,StaffDetails >();

		String loStaffId = aoStaffDupLst.get(0).getMsStaffId();
		String loBaseOrgInd = HHSConstants.ZERO;

		for(StaffDetails  loStaffInfo :    aoStaffDupLst){
			System.out.println(  "  Data :" +    loStaffInfo.toString() );

			if( loStaffInfo.getMsStaffId().equalsIgnoreCase(  loStaffId )){
				if( loBaseOrgInd.equalsIgnoreCase(HHSConstants.ZERO) && loStaffInfo.getMsIsBaseOrg().equalsIgnoreCase(HHSConstants.ONE)  ){
					loBaseOrgInd = HHSConstants.ONE;
					loStaffInfo.setMsIsBaseOrgOn();
				} else{
					loStaffInfo.resetMsIsBaseOrg();
				}
				//System.out.println(  "######### Reset user DN :" +    loStaffInfo.toString() );
				
				continue;
			} else{
				if( !loRemoveSid.containsKey(loStaffInfo.getMsStaffId() ) ){
					System.out.println(  "-----REMOVE_DUP_USER_DN :" +    loStaffInfo.toString() );
					loRemoveSid.put(loStaffInfo.getMsStaffId() , loStaffInfo);
	 				/* reset user dn */
					DAOUtil.masterDAO(aoMyBatisSession, loStaffInfo, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, 
							HHSConstants.REMOVE_DUP_USER_DN, HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
					
					//Add audit 					
					HhsAuditBean aoAudit = getDupUserDnAuditData(loStaffInfo);
					System.out.println( "_____________  HHSAUDIT_INSERT_FOR_DUP_USER_DN :"  + aoAudit.toString()  );
					DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
							HHSConstants.HHSAUDIT_INSERT_FOR_DUP_USER_DN, HHSConstants.HHS_AUDIT_BEAN_PATH);
					
				}
				
				if( loStaffInfo.getMsStaffId() != null && loStaffInfo.getMsStaffId().length() > 0  
						&& loStaffInfo.getMsOrgId() != null  &&  loStaffInfo.getMsOrgId().length() > 0    	){
						loStaffInfo.resetMsIsBaseOrg();
						loStaffInfo.setMsStaffId( loStaffId );
						System.out.println(  "-----REPLACE_STAFF_ID_BASE_ORG :" +    loStaffInfo.toString() );
						DAOUtil.masterDAO(aoMyBatisSession, loStaffInfo, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, 
								HHSConstants.REPLACE_STAFF_ID_BASE_ORG, HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
				}

			}
		}
	}

	
	private HhsAuditBean getDupUserDnAuditData( StaffDetails loStaffInfo ){
		
		HhsAuditBean loAudit = new HhsAuditBean();
		loAudit.setEntityId( HHSConstants.HHSAUDIT_PREFIX_FOR_ENTITY_ID + loStaffInfo.getMsStaffId()  );  
		loAudit.setEntityType( HHSConstants.HHSAUDIT_EVENT_NAME_FOR_DUP_USER_DN  );
		loAudit.setEventName( HHSConstants.HHSAUDIT_EVENT_NAME_FOR_DUP_USER_DN  );
		loAudit.setEventType( HHSConstants.HHSAUDIT_EVENT_TYPE_FOR_DUP_USER_DN  );
		loAudit.setData( HHSConstants.HHSAUDIT_DATA_FOR_DUP_USER_DN
				.replace("_USER_DN_", loStaffInfo.getMsUserDN())
				.replace("_STAFF_ID_", loStaffInfo.getMsStaffId())
				);
		loAudit.setUserId( HHSConstants.SYSTEM_USER);
		//  
		return loAudit; 
	}
	
	
	
	/**
	 *  find Duplicated both User DN and Org ID and clean up  
	 * <ul>
	 * <li> Execute the query id <b> getStaffDetailsFromId </b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL session
	 * @return List of Staff Organization Mapping duplicated
	 * @throws ApplicationException
	 * Created for R8.9.0
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> fetchDupUserNOrg2Clean(SqlSession aoMyBatisSession ) throws ApplicationException
	{
		List <StaffDetails> loStaffDetailsList = null;
		try
		{
			loStaffDetailsList = (List<StaffDetails>) DAOUtil.masterDAO(aoMyBatisSession, null, ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, 
					HHSConstants.FETCH_DUP_BOTH_USER_N_ORG, null);

			if( loStaffDetailsList == null || loStaffDetailsList.size() <= 1 ){
				setMoState("Transaction Success:: MultiAccountService:fetchDupUserNOrg method - no Dup data /n");
				return loStaffDetailsList;
			}

			//take USER_DN from first record
			String  loPrevDn = loStaffDetailsList.get(0).getMsUserDN();
			String  loOrgId = loStaffDetailsList.get(0).getMsOrgId();
			List <StaffDetails> loStaffDupLst = new ArrayList<StaffDetails>()  ;

			/*
			 * take dup user dn and org id put into Q and then clean them up 
			 */
			for( int loInx = 0 ;  loInx  < loStaffDetailsList.size() ; loInx++ ){
				if( loPrevDn.equalsIgnoreCase( loStaffDetailsList.get(loInx).getMsUserDN() ) 
						&& loOrgId.equalsIgnoreCase( loStaffDetailsList.get(loInx).getMsOrgId() ) ) {
					loStaffDupLst.add( loStaffDetailsList.get(loInx) );
					continue;
				}else{
					loPrevDn = loStaffDetailsList.get(loInx).getMsUserDN();
					loOrgId = loStaffDetailsList.get(loInx).getMsOrgId();

					HashMap <Integer,Integer > loInxMap = cleanDupBothUserDN_Org(loStaffDupLst);
					
					resetDupAccount(  aoMyBatisSession , loStaffDupLst   );

					loStaffDupLst = new ArrayList<StaffDetails>()   ;
					loStaffDupLst.add( loStaffDetailsList.get(loInx) ) ;
				}
			}

			HashMap <Integer,Integer > loInxMap =  cleanDupBothUserDN_Org(loStaffDupLst);
			System.out.println(  "  Length:" + loInxMap.size() + "--" +  loStaffDupLst.size() );
			resetDupAccount(  aoMyBatisSession , loStaffDupLst   );

		}
		catch (ApplicationException loAppEx)
		{
			 LOG_OBJECT.Error("Exception occured while updating role in city user details", loAppEx);
             setMoState("Transaction Failed:: searchUserOnEmailId method -Exception occured while " +
             		"fetching search results for User Email search: /n");
             throw loAppEx;
		}
		setMoState("Transaction Success:: MultiAccountService:fetchDupUserNOrg method - no Dup data /n");
		return loStaffDetailsList;
	}

	
    /***  index for  column in List***/
    final  int  loActivaFlagInd = 0;
    final  int  loPermissionLevelInd = 1; 
    final  int  loPermissionTypeInd = 2; 
    final  int  loMsAdminInd  = 3;
    final  int  loIsBaseInd = 4;
    final  int  loSum = 5;
    
    /***  weight value for eval ***/
    final  int  loActivaFlagWeight      = 100000;
    final  int  loIsBaseWeight          = 10000;
    final  int  loPermissionTypeWeight  = 1000; 
    final  int  loPermissionLevelWeight = 100; 
    final  int  loMsAdminWeight         = 10;
    
	protected HashMap<Integer,Integer > cleanDupBothUserDN_Org(  List<StaffDetails>  aoStaffDupLst){

        HashMap<Integer,Integer > loEvalLst = new HashMap<Integer,Integer >();
        Integer loMaxInx  = 0;   Integer loMaxVal  = 0;
        
        
        //Set evaluation base on weight value for each column
		for(int loInx = 0; loInx <  aoStaffDupLst.size() ; loInx++){
			StaffDetails loStaffInfo = aoStaffDupLst.get(loInx);
			Integer loEval =  new Integer(0);
			
			if( loStaffInfo.getMsStaffActiveFlag() != null && loStaffInfo.getMsStaffActiveFlag().equalsIgnoreCase(HHSConstants.YES)  ) {
				loEval += loActivaFlagWeight;
			}
			if( loStaffInfo.getMsStaffActiveFlag() != null && loStaffInfo.getMsIsBaseOrg().equalsIgnoreCase("1")  ) {
				loEval += loIsBaseWeight;
			}
			if( loStaffInfo.getMsStaffActiveFlag() != null && loStaffInfo.getMsPermissionType().length() > 1  ) {
				loEval += loPermissionTypeWeight;
			}
			if( loStaffInfo.getMsStaffActiveFlag() != null && loStaffInfo.getMsPermissionLevel().equalsIgnoreCase(HHSConstants.PCFTH_LEVEL_2   ) ) {
				loEval += loPermissionLevelWeight;
			}
			if( loStaffInfo.getMsAdminPermission() != null && loStaffInfo.getMsAdminPermission().equalsIgnoreCase(HHSConstants.YES   ) ) {
				loEval += loMsAdminWeight;
			}
			loEvalLst.put(Integer.valueOf(loInx), loEval);

			System.out.println("loInx"+"[EVal]"+ loEval+":"+ loEvalLst.get(loInx)  +"_____________  loStaffInfo :"  +  loStaffInfo.getMsStaffId()  
					+ "  \n " +  loStaffInfo.toString()  );

			if( loMaxVal.intValue() < loEval.intValue() ) {
				loMaxVal   = loEval ;
				loMaxInx  = loInx;
			}
		}

		System.out.println("[loMaxInx]"+ loMaxInx+":"+  loMaxVal  + "Before Length:" + loEvalLst.size() + "--" +  aoStaffDupLst.size() );
		aoStaffDupLst.get(loMaxInx).resetMsStaffId();
		System.out.println("[loMaxInx]"+ loMaxInx+":"+  loMaxVal  + "After Length:" + loEvalLst.size() + "--" +  aoStaffDupLst.size() );
		
		return loEvalLst ;
	}
	

	protected void resetDupAccount(SqlSession aoMyBatisSession ,  List<StaffDetails>  aoStaffDupLst   ) throws ApplicationException{
		System.out.println(  "  Length:" + aoStaffDupLst.size()   );

		for(int loInx = 0; loInx <  aoStaffDupLst.size() ; loInx++){
			if(aoStaffDupLst.get(loInx).getMsStaffId() ==  null || 
					aoStaffDupLst.get(loInx).getMsStaffId().length() == 0  )  { continue; }
			System.out.println( "_____________  loStaffInfo :"  +  aoStaffDupLst.get(loInx).getMsStaffId()  
					+ "  \n " +  aoStaffDupLst.get(loInx).toString()  );
			HhsAuditBean aoAudit = getDupUserDnAuditData(aoStaffDupLst.get(loInx));
			DAOUtil.masterDAO(aoMyBatisSession, aoStaffDupLst.get(loInx), ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, 
					HHSConstants.REMOVE_DUP_USER_DN, HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);

			System.out.println( "_____________  HHSAUDIT_INSERT_FOR_DUP_USER_DN :"  + aoAudit.toString()  );
			DAOUtil.masterDAO(aoMyBatisSession, aoAudit, HHSConstants.MAPPER_CLASS_HHS_AUDIT_MAPPER,
					HHSConstants.HHSAUDIT_INSERT_FOR_DUP_USER_DN, HHSConstants.HHS_AUDIT_BEAN_PATH);

		}
		
	}

	protected void replaceStaffIdBaseOrg(SqlSession aoMyBatisSession ,  List<StaffDetails>  aoStaffDupLst   ) throws ApplicationException{
		System.out.println(  "[replaceStaffIdBaseOrg]    Length:" + aoStaffDupLst.size()   );
		
		for(int loInx = 0; loInx <  aoStaffDupLst.size() ; loInx++){

			if(aoStaffDupLst.get(loInx).getMsStaffId() ==  null || 
					aoStaffDupLst.get(loInx).getMsStaffId().length() == 0  ||
					aoStaffDupLst.get(loInx).getMsOrgId()  == null  ||
					aoStaffDupLst.get(loInx).getMsOrgId().length() == 0
					)  
			{ continue; }
			System.out.println( "_____________  loStaffInfo :"  +  aoStaffDupLst.get(loInx).getMsStaffId()  
					+ "  \n " +  aoStaffDupLst.get(loInx).toString()  );

			DAOUtil.masterDAO(aoMyBatisSession, aoStaffDupLst.get(loInx), ApplicationConstants.MAPPER_CLASS_NYC_REGISTER, 
					HHSConstants.REPLACE_STAFF_ID_BASE_ORG, HHSConstants.INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN);
		}
	}
	/*[End] R8.9.0    QC9531    */	

}
