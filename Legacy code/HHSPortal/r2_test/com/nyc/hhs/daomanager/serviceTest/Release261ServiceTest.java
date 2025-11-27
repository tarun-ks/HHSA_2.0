package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.util.HHSUtil;

public class Release261ServiceTest
{

	static SqlSession moMyBatisSession = null;
	static SqlSession loFilenetPEDBSession = null;
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
			moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
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
			moMyBatisSession.close();
			loFilenetPEDBSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	/**Below are the JUnits for new method checkContractFiscalYearsSpan
	 * in HHSUtil
	 *
	 * */
	@Test
	public void testCheckContractFiscalYearsSpan() throws ApplicationException
	{
		boolean lbStatus = HHSUtil.checkContractFiscalYearsSpan("01/01/2014", "01/01/2044");
		assertFalse(lbStatus);
	}
	/**Below are the JUnits for new method checkContractFiscalYearsSpan
	 * in HHSUtil
	 * These JUnits are tested with a constant imposed 31 years as max term (without cache load)
	 * */
	@Test
	public void testCheckContractFiscalYearsSpan1() throws ApplicationException
	{
		boolean lbStatus = HHSUtil.checkContractFiscalYearsSpan("01/01/2014", "01/01/2032");
		assertFalse(lbStatus);
	}
	/**Below are the JUnits for new method checkContractFiscalYearsSpan
	 * in HHSUtil
	 * These JUnits are tested with a constant imposed 31 years as max term (without cache load)
	 * */
	@Test
	public void testCheckContractFiscalYearsSpan2() throws ApplicationException
	{
		boolean lbStatus = HHSUtil.checkContractFiscalYearsSpan("01/01/2014", "01/01/2052");
		assertTrue(lbStatus);
	}
	/**Below are the JUnits for new method checkContractFiscalYearsSpan
	 * in HHSUtil
	 * These JUnits are tested with a constant imposed 31 years as max term (without cache load)
	 * */
	@Test
	public void testCheckContractFiscalYearsSpan3() throws ApplicationException
	{
		boolean lbStatus = HHSUtil.checkContractFiscalYearsSpan(null, "01/01/2042");
		assertFalse(lbStatus);
	}
	/**Below are the JUnits for new method checkContractFiscalYearsSpan
	 * in HHSUtil
	 * These JUnits are tested with a constant imposed 31 years as max term (without cache load)
	 * */
	@Test
	public void testCheckContractFiscalYearsSpan4() throws ApplicationException
	{
		boolean lbStatus = HHSUtil.checkContractFiscalYearsSpan("01/01/2014", null);
		assertFalse(lbStatus);
	}
	/**Below are the JUnits for new method checkContractFiscalYearsSpan
	 * in HHSUtil
	 * These JUnits are tested with a constant imposed 31 years as max term (without cache load)
	 * */
	@Test
	public void testCheckContractFiscalYearsSpan5() throws ApplicationException
	{
		boolean lbStatus = HHSUtil.checkContractFiscalYearsSpan("01/01/2014", "");
		assertFalse(lbStatus);
	}
	/**Below are the JUnits for new method checkContractFiscalYearsSpan
	 * in HHSUtil
	 * These JUnits are tested with a constant imposed 31 years as max term (without cache load)
	 * */
	@Test
	public void testCheckContractFiscalYearsSpan6() throws ApplicationException
	{
		boolean lbStatus = HHSUtil.checkContractFiscalYearsSpan("", "01/01/2014");
		assertFalse(lbStatus);
	}
	/**Below are the JUnits for new method checkContractFiscalYearsSpan
	 * in HHSUtil
	 * These JUnits are tested with a constant imposed 31 years as max term (without cache load)
	 * */
	@Test
	public void testCheckContractFiscalYearsSpan7() throws ApplicationException
	{
		boolean lbStatus = HHSUtil.checkContractFiscalYearsSpan("07/01/2014", "07/01/2032");
		assertFalse(lbStatus);
	}
	/**Below are the JUnits for new method checkContractFiscalYearsSpan
	 * in HHSUtil
	 * These JUnits are tested with a constant imposed 31 years as max term (without cache load)
	 * */
	@Test(expected=ApplicationException.class)
	public void testCheckContractFiscalYearsSpan8() throws ApplicationException
	{
		boolean lbStatus = HHSUtil.checkContractFiscalYearsSpan("abc", "ljsdalj");
		assertFalse(lbStatus);
	}
}