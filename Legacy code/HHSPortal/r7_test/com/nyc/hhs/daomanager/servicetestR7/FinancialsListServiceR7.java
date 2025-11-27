package com.nyc.hhs.daomanager.servicetestR7;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.daomanager.service.FinancialsListService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.util.DAOUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class FinancialsListServiceR7 
{
	FinancialsListService financialsListService = new FinancialsListService();
	private static SqlSession moSession = null; // SQL Session

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			System.out.println("Before");
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession.rollback();
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		finally
		{
			moSession.rollback();
			moSession.close();
		}
	}

	@Test
	public void testFlagContract1() throws ApplicationException
	{
		Map<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSR5Constants.CONTRACT_ID1, "99999");
		loHashMap.put(HHSR5Constants.USER_ID, "city_459");
		loHashMap.put(HHSR5Constants.CONTRACT_MESSAGE, "This is Junit Test");
		loHashMap.put(HHSR5Constants.ACTIVE_FLAG, HHSR5Constants.STRING_ONE);
		financialsListService.flagContract(moSession, loHashMap);
	}

	@Test(expected = ApplicationException.class)
	public void testFlagContract2() throws ApplicationException
	{
		Map<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSR5Constants.CONTRACT_ID1, "99999");
		loHashMap.put(HHSR5Constants.USER_ID, "city_459");
		loHashMap.put(HHSR5Constants.CONTRACT_MESSAGE, "This is Junit Test");
		loHashMap.put(HHSR5Constants.ACTIVE_FLAG, HHSR5Constants.STRING_ONE);
		financialsListService.flagContract(null, loHashMap);
	}

	@Test(expected = ApplicationException.class)
	public void testFlagContract3() throws ApplicationException
	{
		Map<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSR5Constants.CONTRACT_ID1, "99999");
		loHashMap.put(HHSR5Constants.USER_ID, null);
		loHashMap.put(HHSR5Constants.CONTRACT_MESSAGE, "This is Junit Test");
		loHashMap.put(HHSR5Constants.ACTIVE_FLAG, HHSR5Constants.STRING_ONE);
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		financialsListService.flagContract(null, loHashMap);
	}

	@Test
	public void testUnflagContract1() throws ApplicationException
	{
		String lsContractId = "12434";
		String lsUserId = "city_459";
		financialsListService.unflagContract(moSession, lsContractId, lsUserId);
	}

	@Test(expected = ApplicationException.class)
	public void testUnflagContract2() throws ApplicationException
	{
		String lsContractId = "12434";
		String lsUserId = "city_459";
		financialsListService.unflagContract(null, lsContractId, lsUserId);
	}

	@Test
	public void testUnflagContract3() throws ApplicationException
	{
		String lsUserId = "city_459";
		Integer loStatus = financialsListService.unflagContract(null, null, lsUserId);
		assertEquals(loStatus.toString(), "0");
	}

	@Test(expected = ApplicationException.class)
	public void testUnflagContract4() throws ApplicationException
	{
		String lsContractId = "12434";
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		financialsListService.unflagContract(null, lsContractId, null);
	}

	@Test
	public void testUnflagContract5() throws ApplicationException
	{
		String lsUserId = "city_459";
		Integer loStatus = financialsListService.unflagContract(null, "", lsUserId);
		assertEquals(loStatus.toString(), "0");
	}

	@Test
	public void testFetchContractMessageOverlayDetails1() throws ApplicationException
	{
		String lsContractId = "12366"; 
		String lsActionSelected = "flagContract";
		ContractList contractList = financialsListService.fetchContractMessageOverlayDetails(moSession, lsContractId,
				lsActionSelected);
		assertTrue(contractList != null);
	}

	@Test
	public void testFetchContractMessageOverlayDetails2() throws ApplicationException
	{
		String lsContractId = "12366";
		String lsActionSelected = "unflagContract";
		ContractList contractList = financialsListService.fetchContractMessageOverlayDetails(moSession, lsContractId,
				lsActionSelected);
		assertTrue(contractList != null);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractMessageOverlayDetails3() throws ApplicationException
	{
		String lsContractId = "12434";
		String lsActionSelected = "unflagContract";
		ContractList contractList = financialsListService.fetchContractMessageOverlayDetails(null, lsContractId,
				lsActionSelected);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractMessageOverlayDetails4() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String lsContractId = "12434";
		String lsActionSelected = "unflagContract";
		ContractList contractList = financialsListService.fetchContractMessageOverlayDetails(null, lsContractId,
				lsActionSelected);
	}

	@Test
	public void testFetchContractMessageOverlayDetails5() throws ApplicationException
	{
		String lsContractId = "12434";
		String lsActionSelected = null;
		ContractList contractList = financialsListService.fetchContractMessageOverlayDetails(moSession, lsContractId,
				lsActionSelected);
		assertTrue(contractList == null);
	}

	@Test
	public void testFetchContractMessageOverlayDetails6() throws ApplicationException
	{
		String lsContractId = "12434";
		String lsActionSelected = "undefined";
		ContractList contractList = financialsListService.fetchContractMessageOverlayDetails(moSession, lsContractId,
				lsActionSelected);
		assertTrue(contractList == null);
	}
	
	@Test
	public void testFetchContractMessageOverlayDetails7() throws ApplicationException
	{
		String lsContractId = "12434";
		String lsActionSelected = "unflagContract";
		ContractList contractList = financialsListService.fetchContractMessageOverlayDetails(moSession, lsContractId,
				lsActionSelected);
		assertTrue(contractList != null);
	}
}
