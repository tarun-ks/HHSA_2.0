package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertTrue;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.ProposalService;
import com.nyc.hhs.daomanager.service.RFPReleaseService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;

public class Build231ProposalServiceTest
{

	SqlSession moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
	ProposalService moProposalService = new ProposalService();
	RFPReleaseService moRFPReleaseService = new RFPReleaseService();
	
	@Test
	public void testCheckAllRequiredFieldsCompleted() throws ApplicationException
	{
		Boolean lbRequiredFieldsComplete = moProposalService.checkAllRequiredFieldsCompleted(moMyBatisSession, "1350",
				"2322","11");
		assertTrue(lbRequiredFieldsComplete);
	}
	
   @Test
	public void testCheckAllRequiredFieldsNotCompleted() throws ApplicationException
	{
		Boolean lbRequiredFieldsComplete = moProposalService.checkAllRequiredFieldsCompleted(moMyBatisSession, "1897",
				"2663","9");
		assertTrue(lbRequiredFieldsComplete);
	}

	
    @Test
	public void testCheckAllRequiredFieldsThrowApplicationException() throws ApplicationException
	{
		Boolean lbRequiredFieldsComplete = moProposalService.checkAllRequiredFieldsCompleted(moMyBatisSession, "1350",
				"2322","12");
		assertTrue(lbRequiredFieldsComplete);
	}


	@Test(expected = ApplicationException.class)
	public void testCheckAllRequiredFieldsCase1() throws ApplicationException
	{
		moProposalService.checkAllRequiredFieldsCompleted(moMyBatisSession, "##",
				"2322","11");
	}
	
	@Test(expected = ApplicationException.class)
	public void testCheckAllRequiredFieldsCompletedCase2() throws ApplicationException
	{
		moProposalService.checkAllRequiredFieldsCompleted(moMyBatisSession, "1350","##","11");
	}	

	@Test(expected = ApplicationException.class)
	public void testCheckAllRequiredFieldsCompletedCase3() throws ApplicationException
	{
		moProposalService.checkAllRequiredFieldsCompleted(null, "1350","2322","11");
	}
	
	
}	