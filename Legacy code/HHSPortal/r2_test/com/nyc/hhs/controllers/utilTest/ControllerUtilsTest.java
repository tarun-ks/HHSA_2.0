package com.nyc.hhs.controllers.utilTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.ibatis.session.SqlSession;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.ControllerUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.CBGridBean;



public class ControllerUtilsTest {
	
	SqlSession moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
		       JUnitUtil.getTransactionManager();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	@Test
	public void addAssigneeIsValidTrueTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setModifyByProvider("909");
		aoCBGridBean.setModifyByAgency("agency_21");
		loChannelObj.setData(HHSConstants.S431_CHANNEL_KEY_LB_VALID,true);
		loChannelObj.setData("asBudgetId", "10876");
		loChannelObj.setData("lsVendorId", "0001617199");
		loChannelObj.setData("aoMyBatisSession",moMyBatisSession);
		loChannelObj.setData("loCBGridBean",aoCBGridBean);
		String lsAssignee = ControllerUtils.addAssignee(loChannelObj);
		assertTrue(lsAssignee.equals(""));
	}
	@Test
	public void addAssigneeIsValidFalseTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.S431_CHANNEL_KEY_LB_VALID,false);
		loChannelObj.setData("asBudgetId", "10663");
		loChannelObj.setData("lsVendorId", "0001464360");
		loChannelObj.setData("aoMyBatisSession",moMyBatisSession);
		String lsAssignee = ControllerUtils.addAssignee(loChannelObj);
		assertTrue(!lsAssignee.equals(""));
	}
	@Test(expected = ApplicationException.class)
	public void addAssigneeIsValidExceptionTest() throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.S431_CHANNEL_KEY_LB_VALID,true);
		loChannelObj.setData("aoMyBatisSession",null);
		String lsAssignee = ControllerUtils.addAssignee(loChannelObj);
		assertNotNull(lsAssignee);
	}
}
