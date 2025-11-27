package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.AddressBean;
import com.nyc.hhs.model.AddressValidationBean;
import com.nyc.hhs.util.HHSUtil;

public class UtilityTest
{

	/**
	 * This method adds 50 years to current date and return the resulted date.
	 * 
	 */
	@Test
	public void testGetToDate() throws ApplicationException
	{
		Date loDate = HHSUtil.getToDate();
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string from date if exists and return modified date
	 * 
	 */
	@Test
	public void testSetDateToFrom() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom(null, "03/05/2013", "from");
		assertNotNull(loDate);
	}

	/**
	 * This method modifies string to date if exists and return modified date
	 * 
	 */
	@Test
	public void testSetDateToFromToDate() throws ApplicationException
	{
		Date loDate = HHSUtil.setDateToFrom("03/05/2013", null, "to");
		assertNotNull(loDate);
	}

	/**
	 * This method adds hours, minutes and seconds to input from date and return
	 * the modified date
	 * 
	 */
	@Test
	public void testAddFromClause() throws ApplicationException
	{
		Date loDate = HHSUtil.addFromClause(HHSUtil.getToDate());
		assertNotNull(loDate);
	}

	/**
	 * This method adds hours, minutes and seconds to input to date and return
	 * the modified date
	 * 
	 */
	@Test
	public void testAddToClause() throws ApplicationException
	{
		Date loDate = HHSUtil.addFromClause(HHSUtil.getToDate());
		assertNotNull(loDate);
	}
	
	/**
	 * This method adds hours, minutes and seconds to input to date and return
	 * the modified date
	 * 
	 */
	@Test
	public void testAddToClause1() throws ApplicationException
	{
		Date loDate = HHSUtil.addToClause(HHSUtil.getToDate());
		assertNotNull(loDate);
	}
	
	@Test
	public void testGetZeroTimeDate() throws ApplicationException
	{
		Date loDate = HHSUtil.getZeroTimeDate(HHSUtil.getToDate());
		assertNotNull(loDate);
		
	}
	@Test
	public void testFormatAmount() throws ApplicationException
	{
		String loformatNumber= HHSUtil.formatAmount("1000,123");
		assertEquals(loformatNumber,"1000123");
	}

/*@Test
public void testConvertAddressValidationFields()
{
	String lsAddressRelatedDataArray[] = null;
	AddressValidationBean aoAddressBean = new AddressBean;
	AddressBean.setValStatusDescription("status");
	
}*/
	@Test
	public void testGetCurrentTimestampDate() throws ApplicationException
	{
		Timestamp Timestamp = HHSUtil.getCurrentTimestampDate();
		assertNotNull(Timestamp);
	}
	
	
}