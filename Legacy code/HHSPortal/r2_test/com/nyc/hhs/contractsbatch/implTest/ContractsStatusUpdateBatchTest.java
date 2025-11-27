package com.nyc.hhs.contractsbatch.implTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.contractsbatch.impl.ContractsStatusUpdateBatch;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;

public class ContractsStatusUpdateBatchTest
{

	private static SqlSession moSession = null; // SQL Session

	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception
	 */
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
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
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
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	ContractsStatusUpdateBatch moContractsStatusUpdateBatch = new ContractsStatusUpdateBatch();

	/**
	 * @TODO - place comments
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetQueue() throws ApplicationException
	{
		List<ContractsStatusUpdateBatch> loContractsStatusUpdateBatchList = moContractsStatusUpdateBatch.getQueue(null);
		assertTrue(null == loContractsStatusUpdateBatchList);
	}

	/**
	 * @TODO - place comments
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testExecuteQueue() throws ApplicationException
	{
		moContractsStatusUpdateBatch.executeQueue(new ArrayList<Object>());
	}

}
