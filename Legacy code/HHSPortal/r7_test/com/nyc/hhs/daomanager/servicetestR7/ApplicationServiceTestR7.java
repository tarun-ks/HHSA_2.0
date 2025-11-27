package com.nyc.hhs.daomanager.servicetestR7;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.nyc.hhs.daomanager.service.ApplicationService;
import com.nyc.hhs.daomanager.service.FinancialsService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.TaskQueue;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class ApplicationServiceTestR7 {
	
	ApplicationService loApplicationService=new ApplicationService();
	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession loUserSession; // FilenetSession

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			loUserSession = getFileNetSession();
		}
		catch (Exception loEx)
		{
			
			  lbThrown = true; assertTrue("Exception thrown", lbThrown);
			 
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
		
		 catch (Exception loEx) { lbThrown = true;
		 assertTrue("Exception thrown", lbThrown); }
		 
		finally
		{
			
			 moSession.rollback(); moSession.close();
			 
		}
	}
	
	public static P8UserSession getFileNetSession() throws ApplicationException
	{
		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));

		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONNECTION_POINT_NAME"));
		loUserSession.setUserId("ceadmin");
		loUserSession.setPassword("Filenet1");

		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		// loP8SecurityService.getPESession(loUserSession);
		// loP8SecurityService.getObjectStore(loUserSession);
		return loUserSession;

	}
	
	@Test
	public void getBusinessApplicationStatusTest() throws ApplicationException{
		
		ArrayList<TaskQueue> aoTaskDetailsList= new ArrayList<TaskQueue>();
		TaskQueue loTaskQueue = new TaskQueue();
		loTaskQueue.setMsApplicationId("test");
		loTaskQueue.setMsProviderId("test");
		aoTaskDetailsList.add(loTaskQueue);
		loApplicationService.getBusinessApplicationStatus(aoTaskDetailsList,moSession );
	
	}
	
	@Test
	public void getBusinessApplicationStatusNullTest() throws ApplicationException{
		
		ArrayList<TaskQueue> aoTaskDetailsList= new ArrayList<TaskQueue>();
		TaskQueue loTaskQueue = new TaskQueue();
		
		loTaskQueue.setMsProviderId("test");
		aoTaskDetailsList.add(loTaskQueue);
		loApplicationService.getBusinessApplicationStatus(aoTaskDetailsList,moSession );
	
	}
	
	@Test(expected = NullPointerException.class)
	public void getBusinessApplicationStatusExceptionTest() throws ApplicationException
	{
		loApplicationService.getBusinessApplicationStatus(null,moSession );
		
	}
	
	@Test
	public void getBusinessApplicationStatusNullTest1() throws ApplicationException{
		
		ArrayList<TaskQueue> aoTaskDetailsList= new ArrayList<TaskQueue>();
		TaskQueue loTaskQueue = new TaskQueue();
		
		loTaskQueue.setMsApplicationId("test");
		aoTaskDetailsList.add(loTaskQueue);
		loApplicationService.getBusinessApplicationStatus(aoTaskDetailsList,moSession );


	}
}
