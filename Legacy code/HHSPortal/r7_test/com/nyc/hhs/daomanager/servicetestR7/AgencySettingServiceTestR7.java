package com.nyc.hhs.daomanager.servicetestR7;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.sql.DATE;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlsoap.schemas.soap.encoding.Int;

import com.nyc.hhs.daomanager.service.AgencySettingService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AgencySettingsBean;
import com.nyc.hhs.model.AutoApprovalConfigBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

public class AgencySettingServiceTestR7
{
	AgencySettingService agencySettingService = new AgencySettingService();
	private static SqlSession moSession = null; // SQL Session

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		}
		catch (Exception loEx)
		{
			/*
			 * lbThrown = true; assertTrue("Exception thrown", lbThrown);
			 */
		}
	}

	/**
	 * Close the SQL session created at the beginning
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession.rollback();
			moSession.close();
		}
		finally
		{
			moSession.rollback();
			moSession.close();
		}
	}

	@Test
	public void testDelAutoApprovalDetailsCase1() throws ApplicationException
	{
		Boolean delStatus = false;
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		aoAutoApprovalConfigBean.setAgencyId("DOC");
		agencySettingService.delAutoApprovalDetails(aoAutoApprovalConfigBean, moSession);
	}

	@Test(expected=ApplicationException.class)
	public void testDelAutoApprovalDetailsCase2() throws ApplicationException
	{
		Boolean delStatus = false;
		AutoApprovalConfigBean aoAutoApprovalConfigBean=null;
		agencySettingService.delAutoApprovalDetails(aoAutoApprovalConfigBean, null);
	}
	@Test(expected=NullPointerException.class)
	public void testDelAutoApprovalDetailsCase3() throws ApplicationException
	{
		AutoApprovalConfigBean aoAutoApprovalConfigBean=null;
		Boolean delStatus = false;
		agencySettingService.delAutoApprovalDetails(aoAutoApprovalConfigBean, null); 
	}
	
	
	@Test
	public void testEditAutoApprovalDetailsCase1() throws ApplicationException
	{
		Boolean editStatus = false;
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		aoAutoApprovalConfigBean.setAgencyId("ACS");
		aoAutoApprovalConfigBean.setThresholdPercentage(10);
		aoAutoApprovalConfigBean.setModifiedbByUserName("Org_515");
		aoAutoApprovalConfigBean.setId("247");
        aoAutoApprovalConfigBean.setModifiedDate("02-11-17 03:06:20.017000000 PM");
		agencySettingService.editAutoApprovalDetails(aoAutoApprovalConfigBean, moSession);
	}

	@Test(expected = ApplicationException.class)
	public void testEditAutoApprovalDetailsCase2() throws ApplicationException
	{
		Boolean editStatus = true;
		agencySettingService.editAutoApprovalDetails(null, null);
	}
	@Test(expected = ApplicationException.class)
	public void testEditAutoApprovalDetailsCase3() throws ApplicationException
	{
		AutoApprovalConfigBean aoAutoApprovalConfigBean=null;
		Boolean editStatus = true;
		agencySettingService.editAutoApprovalDetails(aoAutoApprovalConfigBean, moSession);
	}
	
	@Test
	public void testAddAutoApprovalDetailsCase1() throws ApplicationException
	{
		Boolean addStatus = false; 
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		aoAutoApprovalConfigBean.setAgencyId("DOC");
		aoAutoApprovalConfigBean.setThresholdPercentage(15);
		aoAutoApprovalConfigBean.setModifiedbByUserName("city_459");
		aoAutoApprovalConfigBean.setCreatedByUserId("city_459");
		//aoAutoApprovalConfigBean.setModifiedDate(Date.);
		aoAutoApprovalConfigBean.setOrganizationId("Org_515");
		aoAutoApprovalConfigBean.setOrganizationName("Debi Kalachashma3");
		aoAutoApprovalConfigBean.setReviewProcessId(5);
		agencySettingService.addAutoApprovalDetails(aoAutoApprovalConfigBean, moSession);
	}
	
	@Test
	public void testAddAutoApprovalDetailsCase2() throws ApplicationException
	{
		Boolean addStatus = false; 
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		aoAutoApprovalConfigBean.setAgencyId("ABC");
		aoAutoApprovalConfigBean.setThresholdPercentage(15);
		aoAutoApprovalConfigBean.setModifiedbByUserName("city_459");
		aoAutoApprovalConfigBean.setCreatedByUserId("city_459");
		//aoAutoApprovalConfigBean.setModifiedDate(Date.);
		aoAutoApprovalConfigBean.setOrganizationId("Org_515");
		aoAutoApprovalConfigBean.setOrganizationName("Debi Kalachashma3");
		aoAutoApprovalConfigBean.setReviewProcessId(5);
		agencySettingService.addAutoApprovalDetails(aoAutoApprovalConfigBean, moSession);
	}
	
	@Test
	public void testAddAutoApprovalDetailsCase7() throws ApplicationException
	{
		Boolean addStatus = false; 
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		aoAutoApprovalConfigBean.setAgencyId("ABC");
		aoAutoApprovalConfigBean.setThresholdPercentage(15);
		aoAutoApprovalConfigBean.setModifiedbByUserName("city_459");
		aoAutoApprovalConfigBean.setCreatedByUserId("city_459");
		//aoAutoApprovalConfigBean.setModifiedDate(Date.);
		aoAutoApprovalConfigBean.setOrganizationId("Org_515");
		
	    aoAutoApprovalConfigBean.setReviewProcessId(5);
		agencySettingService.addAutoApprovalDetails(aoAutoApprovalConfigBean, moSession);
	}
	
	@Test
	public void testAddAutoApprovalDetailsCase6() throws ApplicationException
	{
		Boolean addStatus = false; 
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		aoAutoApprovalConfigBean.setAgencyId("ACS");
		aoAutoApprovalConfigBean.setThresholdPercentage(15);
		aoAutoApprovalConfigBean.setModifiedbByUserName("city_459");
		aoAutoApprovalConfigBean.setCreatedByUserId("city_459");
		//aoAutoApprovalConfigBean.setModifiedDate(Date.);
		aoAutoApprovalConfigBean.setOrganizationId("Org_515");
		aoAutoApprovalConfigBean.setOrganizationName("Debi Kalachashma3");
		aoAutoApprovalConfigBean.setReviewProcessId(5);
		agencySettingService.addAutoApprovalDetails(aoAutoApprovalConfigBean, moSession);
	}
	
	
	@Test
	public void testAddAutoApprovalDetailsCase8() throws ApplicationException
	{
		Boolean addStatus = false; 
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		aoAutoApprovalConfigBean.setAgencyId("ACS");
		aoAutoApprovalConfigBean.setThresholdPercentage(115);
		aoAutoApprovalConfigBean.setModifiedbByUserName("city_459");
		aoAutoApprovalConfigBean.setCreatedByUserId("city_459");
		//aoAutoApprovalConfigBean.setModifiedDate(Date.);
		aoAutoApprovalConfigBean.setOrganizationId("Org_515");
		aoAutoApprovalConfigBean.setOrganizationName("Debi Kalachashma3");
		aoAutoApprovalConfigBean.setReviewProcessId(5);
		agencySettingService.addAutoApprovalDetails(aoAutoApprovalConfigBean, moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void testAddAutoApprovalDetailsCase3() throws ApplicationException
	{
		
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		agencySettingService.addAutoApprovalDetails(aoAutoApprovalConfigBean, null);
	}
	@Test(expected = Exception.class)
	public void testAddAutoApprovalDetailsCase4() throws ApplicationException
	{
		
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		agencySettingService.addAutoApprovalDetails(null, null);
	}
	
	@Test(expected = Exception.class)
	public void testAddAutoApprovalDetailsCase5() throws ApplicationException
	{
		
		AutoApprovalConfigBean aoAutoApprovalConfigBean=new AutoApprovalConfigBean();
		agencySettingService.addAutoApprovalDetails(aoAutoApprovalConfigBean, moSession);
	}



    @Test
	public void testFetchAgencyListCase1() throws Exception
	{
		AgencySettingsBean agencySettingsBean=new AgencySettingsBean();
		agencySettingsBean =agencySettingService.fetchAgencyList(moSession);
		assertTrue(agencySettingsBean !=null);
		 
	}

     @Test(expected = ApplicationException.class)
	public void testFetchAgencyListCase2() throws Exception
	{
    	 AgencySettingsBean agencySettingsBean=new AgencySettingsBean();
		 agencySettingsBean =agencySettingService.fetchAgencyList(null);
		
	}
    
     @Test(expected = ApplicationException.class)
 	public void testFetchAgencyListCase3() throws Exception
 	{
     	 AgencySettingsBean agencySettingsBean=new AgencySettingsBean();
 		 agencySettingsBean =agencySettingService.fetchAgencyList(null);
 		
 	}

     
     @Test
     public void testfetchAgencyProviderscase1() throws ApplicationException
     {
     	List<OrganizationBean> Providers=new ArrayList<OrganizationBean>();
     	Providers=agencySettingService.fetchAgencyProviders("ACS",moSession);
     	
     }

     @Test(expected = ApplicationException.class)
     public void testfetchAgencyProviderscase2() throws ApplicationException
     {
     	List<OrganizationBean> Providers=new ArrayList<OrganizationBean>();
     	Providers=agencySettingService.fetchAgencyProviders(null,null);
     	
     }

     @Test(expected = ApplicationException.class)
     public void testfetchAgencyProviderscase3() throws ApplicationException
     {
     	List<OrganizationBean> Providers=new ArrayList<OrganizationBean>();
     	Providers=agencySettingService.fetchAgencyProviders("ACS",moSession);
     }



@Test
public void testfetchAutoApprovalThresholdcase1() throws ApplicationException

{
        AutoApprovalConfigBean autoApprovalConfigBean=new AutoApprovalConfigBean();
        autoApprovalConfigBean =agencySettingService.fetchAutoApprovalThreshold("ACS",moSession);
        assertTrue(autoApprovalConfigBean!=null);

}

@Test(expected = ApplicationException.class)
	public void testfetchAutoApprovalThresholdcase2() throws ApplicationException
	{
	AutoApprovalConfigBean autoApprovalConfigBean=new AutoApprovalConfigBean();
    autoApprovalConfigBean =agencySettingService.fetchAutoApprovalThreshold(null,null);
		
	}
    @Test(expected = ApplicationException.class)
	public void testfetchAutoApprovalThresholdcase3() throws ApplicationException
	{
    	AutoApprovalConfigBean autoApprovalConfigBean=new AutoApprovalConfigBean();
        autoApprovalConfigBean =agencySettingService.fetchAutoApprovalThreshold("ACS",moSession);
		
	}





@Test
public void testfetchAutoApprovalDetailsListcase1() throws ApplicationException
{
	List<AutoApprovalConfigBean> Detaillist=new ArrayList<AutoApprovalConfigBean>();
	Detaillist=agencySettingService.fetchAutoApprovalDetailsList("ACS",moSession);
	assertTrue(Detaillist!=null);
}

@Test(expected = ApplicationException.class)
public void testfetchAutoApprovalDetailsListcase2() throws ApplicationException
{
	List<AutoApprovalConfigBean> Detaillist=new ArrayList<AutoApprovalConfigBean>();
	Detaillist=agencySettingService.fetchAutoApprovalDetailsList(null,null);
	
}

@Test(expected = ApplicationException.class)
public void testfetchAutoApprovalDetailsListcase3() throws ApplicationException
{
	List<AutoApprovalConfigBean> Detaillist=new ArrayList<AutoApprovalConfigBean>();
	Detaillist=agencySettingService.fetchAutoApprovalDetailsList("ACS",moSession);
}


@Test
public void testsaveAutoApprovalThresholdCase1() throws ApplicationException
{
	Boolean SaveStatus = false;
	String asAgencyId = "ACS";
	String asUserId = "city_459";
	int aiThresholdValue = 10;
	agencySettingService.saveAutoApprovalThreshold("ACS",10,"city_459", moSession);
	
}

@Test
public void testsaveAutoApprovalThresholdCase2() throws ApplicationException
{
	Boolean SaveStatus = false;
	String asAgencyId = "DOC";
	String asUserId = "city_459";
	int aiThresholdValue = 115;
	agencySettingService.saveAutoApprovalThreshold("DOC",115,"city_459", moSession);
	
}


@Test
public void testsaveAutoApprovalThresholdCase6() throws ApplicationException
{
	Boolean SaveStatus = false;
	String asAgencyId = "ABC";
	String asUserId = "city_459";
	int aiThresholdValue = 10;
	agencySettingService.saveAutoApprovalThreshold("ABC",10,"city_459", moSession);
	
}

@Test
public void testsaveAutoApprovalThresholdCase7() throws ApplicationException
{
	Boolean SaveStatus = false;
	String asAgencyId = "DOC";
	String asUserId = "city_459";
	int aiThresholdValue = -5;
	agencySettingService.saveAutoApprovalThreshold("DOC",-5,"city_459", moSession);
	
}

@Test
public void testsaveAutoApprovalThresholdCase8() throws ApplicationException
{
	Boolean SaveStatus = false;
	String asAgencyId = "HPD";
	String asUserId = "city_459";
	int aiThresholdValue = 5;
	agencySettingService.saveAutoApprovalThreshold("HPD",5,"city_459", moSession);
	
}
@Test(expected = ApplicationException.class)
public void testsaveAutoApprovalThresholdCase3() throws ApplicationException
{
	
	agencySettingService.saveAutoApprovalThreshold("ACS",10,"city_459", moSession);
}
@Test(expected = Exception.class)
public void testsaveAutoApprovalThresholdCase4() throws ApplicationException
{
	
	
	agencySettingService.saveAutoApprovalThreshold(null, null,null,null);
}

@Test(expected = Exception.class)
public void testsaveAutoApprovalThresholdCase5() throws ApplicationException
{
	
	agencySettingService.saveAutoApprovalThreshold("ACS",10,"city_459", null);
}
@Test
public void testfetchAutoApproverUserListcase1() throws ApplicationException
{
	List<String> Approverlist=new ArrayList<String>();
	Approverlist=agencySettingService.fetchAutoApproverUserList(moSession);
	assertTrue(Approverlist!=null);
}

@Test(expected = ApplicationException.class)
public void testfetchAutoApproverUserListcase2() throws ApplicationException
{
	List<String> Approverlist=new ArrayList<String>();
	Approverlist=agencySettingService.fetchAutoApproverUserList(null);
	
}

@Test(expected = ApplicationException.class)
public void testfetchAutoApproverUserListcase3() throws ApplicationException
{
	List<String> Approverlist=new ArrayList<String>();
	Approverlist=agencySettingService.fetchAutoApproverUserList(moSession);
}


}
     

