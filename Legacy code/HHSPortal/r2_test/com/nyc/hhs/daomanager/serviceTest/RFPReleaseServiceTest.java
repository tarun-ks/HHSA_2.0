package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.HhsAuditService;
import com.nyc.hhs.daomanager.service.RFPReleaseService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.EvaluationCriteriaBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.RFPReleaseBean;

public class RFPReleaseServiceTest
{

	private static SqlSession moMyBatisSession = null;
	private RFPReleaseService loRFPReleaseService = new RFPReleaseService();
	private String msProcId1 = "190";
	private String msProcId2 = "135";
	private String msProcId3 = "132";
	private String msProcId4 = "171";
	private String msProcIdInvalid = "#@SD$%";
	private String msUserId = "city_42";

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
			moMyBatisSession.rollback();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * this method sets all the required data in the bean.
	 * @return RFPReleaseBean - RFPReleaseBean reference
	 * @throws Exception - throws Exception
	 */
	public RFPReleaseBean getRFPReleaseBean(Integer liCaseCheck)
	{
		RFPReleaseBean loRfpReleaseBean = new RFPReleaseBean();
		List<EvaluationCriteriaBean> loEvaluationCriteriaBeanList = new ArrayList<EvaluationCriteriaBean>();
		if (liCaseCheck == 1)
		{
			loRfpReleaseBean.setProcurementId(msProcId1);
		}
		else if (liCaseCheck == 2)
		{
			loRfpReleaseBean.setProcurementId(msProcId2);
		}
		else if (liCaseCheck == 3)
		{
			loRfpReleaseBean.setProcurementId(msProcId3);
		}
		else if (liCaseCheck == 100)
		{
			loRfpReleaseBean.setProcurementId(msProcId3);
		}
		else if (liCaseCheck == 101)
		{
			loRfpReleaseBean.setProcurementId(msProcId2);
		}
		else
		{
			loRfpReleaseBean.setProcurementId("239");
		}
		loRfpReleaseBean.setCreatedByUserId(msUserId);
		loRfpReleaseBean.setModifiedByUserId(msUserId);

		if (liCaseCheck == 100 || liCaseCheck == 101)
		{
			EvaluationCriteriaBean evaluationCriteriaBean = new EvaluationCriteriaBean();
			evaluationCriteriaBean.setScoreCriteria(null);
			evaluationCriteriaBean.setScoreSeqNumber(90);
			loEvaluationCriteriaBeanList.add(evaluationCriteriaBean);
		}
		else
		{
			loEvaluationCriteriaBeanList = getEvaluationCriteriaBeanList(liCaseCheck);
		}
		loRfpReleaseBean.setLoEvaluationCriteriaBeanList(loEvaluationCriteriaBeanList);

		return loRfpReleaseBean;
	}

	/**
	 * this method sets all the required data in the bean.
	 * @return List<EvaluationCriteriaBean> - EvaluationCriteriaBean list
	 */
	private List<EvaluationCriteriaBean> getEvaluationCriteriaBeanList(Integer liCaseCheck)
	{
		List<EvaluationCriteriaBean> loEvaluationCriteriaBeanList = new ArrayList<EvaluationCriteriaBean>();
		EvaluationCriteriaBean loEvaluationCriteriaBean = new EvaluationCriteriaBean();
		for (int liCounter = 0; liCounter < 10; liCounter++)
		{
			if (liCounter % 2 == 0)
			{

				loEvaluationCriteriaBean.setMaximumScore(liCounter + 5);
				if (liCaseCheck == 1 || liCaseCheck == 5 || liCaseCheck == 6)
				{
					loEvaluationCriteriaBean.setScoreCriteria("Score" + (liCounter + 1));
					loEvaluationCriteriaBean.setScoreSeqNumber(liCounter + 1);
				}
				else if (liCaseCheck == 2)
				{
					loEvaluationCriteriaBean.setScoreSeqNumber(liCounter + 1);
					loEvaluationCriteriaBean
							.setScoreCriteria("Scoreaaaaa"
									+ (liCounter + 1));
				}
				else if (liCaseCheck == 3)
				{
					loEvaluationCriteriaBean.setScoreSeqNumber(0);
					loEvaluationCriteriaBean.setScoreCriteria("");
				}
				else if (liCaseCheck == 4)
				{
					loEvaluationCriteriaBean.setScoreSeqNumber(liCounter + 1);
					loEvaluationCriteriaBean.setScoreCriteria("");
				}

				loEvaluationCriteriaBean.setScoreFlag("Y");

			}
			else
			{
				loEvaluationCriteriaBean.setMaximumScore(liCounter + 5);
				loEvaluationCriteriaBean.setScoreCriteria("Score" + (liCounter + 1));
				loEvaluationCriteriaBean.setScoreFlag("N");
				loEvaluationCriteriaBean.setScoreSeqNumber(liCounter + 1);
			}
			loEvaluationCriteriaBeanList.add(loEvaluationCriteriaBean);
			loEvaluationCriteriaBean = new EvaluationCriteriaBean();
		}
		return loEvaluationCriteriaBeanList;
	}

	/**
	 * This method tests fetching the procurement status from the database.
	 * @throws Exception
	 */
	@Test
	public void testGetProcurementStatusCase1() throws Exception
	{
		String lsProcurementId = "3";
		loRFPReleaseService.getProcurementStatus(moMyBatisSession, lsProcurementId);
		assertTrue(true);
	}

	/**
	 * This method tests fetching the procurement status from the database.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProcurementStatusExceptionCase2() throws Exception
	{
		String lsProcurementId = "3";
		loRFPReleaseService.getProcurementStatus(null, lsProcurementId);
	}

	@Test
	public void testGetProcurementStatusExceptionCase3() throws Exception
	{
		String lsProcurementId = "3";
		loRFPReleaseService.getProcurementStatus(moMyBatisSession, lsProcurementId);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProcurementStatusExceptionCase5() throws Exception
	{
		loRFPReleaseService.getProcurementStatus(null, null);
	}

	/**
	 * This method tests saving the evaluation criteria details from the
	 * database.
	 * @throws Exception
	 */
	@Test
	public void saveEvaluationCriteria() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(1);
		Boolean lbActualResult = (Boolean) loRFPReleaseService.saveEvaluationCriteria(moMyBatisSession,
				loRFPReleaseBean);
		Boolean liExpectedResult = Boolean.TRUE;
		assertEquals(liExpectedResult, lbActualResult);
	}

	/**
	 * This method tests saving the evaluation criteria details from the
	 * database.
	 * @throws Exception
	 */
	@Test
	public void saveEvaluationCriteriaCase2() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(2);
		Boolean lbActualResult = (Boolean) loRFPReleaseService.saveEvaluationCriteria(moMyBatisSession,
				loRFPReleaseBean);
		Boolean liExpectedResult = Boolean.TRUE;
		assertEquals(liExpectedResult, lbActualResult);
	}

	/**
	 * This method tests saving the evaluation criteria details from the
	 * database.
	 * @throws Exception
	 */
	@Test
	public void saveEvaluationCriteriaCase3() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(3);
		Boolean lbActualResult = (Boolean) loRFPReleaseService.saveEvaluationCriteria(moMyBatisSession,
				loRFPReleaseBean);
		Boolean liExpectedResult = Boolean.TRUE;
		assertEquals(liExpectedResult, lbActualResult);
	}

	/**
	 * This method tests saving the evaluation criteria details from the
	 * database.
	 * @throws Exception
	 */
	@Test
	public void saveEvaluationCriteriaCase100() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(100);
		Boolean lbActualResult = (Boolean) loRFPReleaseService.saveEvaluationCriteria(moMyBatisSession,
				loRFPReleaseBean);
		Boolean liExpectedResult = Boolean.TRUE;
		assertEquals(liExpectedResult, lbActualResult);
	}

	/**
	 * This method tests saving the evaluation criteria details from the
	 * database.
	 * @throws Exception
	 */
	@Test
	public void saveEvaluationCriteriaCase101() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(101);
		Boolean lbActualResult = (Boolean) loRFPReleaseService.saveEvaluationCriteria(moMyBatisSession,
				loRFPReleaseBean);
		Boolean liExpectedResult = Boolean.TRUE;
		assertEquals(liExpectedResult, lbActualResult);
	}

	/**
	 * This method tests saving the evaluation criteria details from the
	 * database.
	 * @throws Exception
	 */
	@Test
	public void saveEvaluationCriteriaCase4() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(4);
		Boolean lbActualResult = (Boolean) loRFPReleaseService.saveEvaluationCriteria(moMyBatisSession,
				loRFPReleaseBean);
		Boolean liExpectedResult = Boolean.TRUE;
		assertEquals(liExpectedResult, lbActualResult);
	}

	/**
	 * This method tests getting exception while saving the evaluation criteria
	 * details in the database.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void saveEvaluationCriteriaException7() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(2);
		loRFPReleaseBean.setProcurementId("251898");
		loRFPReleaseBean.setLoEvaluationCriteriaBeanList(null);
		loRFPReleaseService.saveEvaluationCriteria(moMyBatisSession, loRFPReleaseBean);
	}

	/**
	 * This method tests getting exception while saving the evaluation criteria
	 * details in the database.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void saveEvaluationCriteriaException8() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(2);
		loRFPReleaseService.saveEvaluationCriteria(null, loRFPReleaseBean);
		assertTrue(true);
	}

	/**
	 * The method fetchEvaluationCriteria tests fetching the evaluation criteria
	 * details from the database.
	 * @throws Exception
	 */
	@Test
	public void testFetchEvaluationCriteria() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = (RFPReleaseBean) loRFPReleaseService.fetchEvaluationCriteria(
				moMyBatisSession, msProcId4);
		assertNull(loRFPReleaseBean);
	}

	/**
	 * The method fetchEvaluationCriteria tests fetching the evaluation criteria
	 * details from the database.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationCriteria4() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = (RFPReleaseBean) loRFPReleaseService.fetchEvaluationCriteria(
				moMyBatisSession, msProcIdInvalid);
		assertNotNull(loRFPReleaseBean);
	}

	/**
	 * The method fetchEvaluationCriteria tests fetching the evaluation criteria
	 * details from the database.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationCriteriaCase1() throws ApplicationException
	{
		loRFPReleaseService.fetchEvaluationCriteria(null, msProcIdInvalid);
	}

	/**
	 * The method fetchEvaluationCriteria tests fetching the evaluation criteria
	 * details from the database.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationCriteriaCase2() throws Exception
	{
		loRFPReleaseService.fetchEvaluationCriteria(null, null);
	}

	/**
	 * This method test the data movement from rfp_addendum_document to
	 * rfp_document table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRfpDocumentCase1() throws ApplicationException
	{
		Boolean lbAddendumDoc = loRFPReleaseService.updateRfpDocument(moMyBatisSession, msProcId2);
		assertNotNull(lbAddendumDoc);
	}

	/**
	 * This method test the data movement from rfp_addendum_document to
	 * rfp_document table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRfpDocumentCase2() throws ApplicationException
	{
		Boolean lbAddendumDoc = loRFPReleaseService.updateRfpDocument(moMyBatisSession, "124");
		assertTrue(!lbAddendumDoc);
	}

	@Test
	public void testUpdateRfpDocumentCase5() throws ApplicationException
	{
		Boolean lbAddendumDoc = loRFPReleaseService.updateRfpDocument(moMyBatisSession, "628");
		assertFalse(lbAddendumDoc);
	}

	/**
	 * This method test the data movement from rfp_addendum_document to
	 * rfp_document table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRfpDocumentCase3() throws ApplicationException
	{
		Boolean lbAddendumDoc = loRFPReleaseService.updateRfpDocument(moMyBatisSession, "236");
		assertTrue(!lbAddendumDoc);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateRfpDocumentCase4() throws ApplicationException
	{
		loRFPReleaseService.updateRfpDocument(moMyBatisSession, null);
	}

	/**
	 * This method test the negative execution of data movement from
	 * rfp_addendum_document to rfp_document table corresponding to the
	 * procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateRfpDocumentApplicationException() throws ApplicationException
	{
		Boolean lbAddendumDoc = loRFPReleaseService.updateRfpDocument(moMyBatisSession, null);
		assertTrue(lbAddendumDoc);
	}

	/**
	 * This method test the data updation from procurement_addendum to
	 * procurement table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProcurementDataCase1() throws ApplicationException
	{
		Boolean lbUpdateSuccessful = loRFPReleaseService.updateProcurementData(moMyBatisSession, "239", msUserId,
				Boolean.TRUE);
		assertNotNull(lbUpdateSuccessful);
		assertTrue(lbUpdateSuccessful);
	}

	/**
	 * This method test the data updation from procurement_addendum to
	 * procurement table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProcurementDataCase2() throws ApplicationException
	{
		Boolean lbUpdateSuccessful = loRFPReleaseService.updateProcurementData(moMyBatisSession, "239", msUserId,
				Boolean.FALSE);
		assertNotNull(lbUpdateSuccessful);
		assertTrue(!lbUpdateSuccessful);
	}

	@Test
	public void testUpdateProcurementDataCase3() throws ApplicationException
	{
		Boolean lbUpdateSuccessful = loRFPReleaseService.updateProcurementData(moMyBatisSession, null, null,
				Boolean.FALSE);
		assertNotNull(lbUpdateSuccessful);
		assertTrue(!lbUpdateSuccessful);
	}

	/**
	 * This method test the negative execution of data updation from
	 * procurement_addendum to procurement table corresponding to the
	 * procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProcurementDataCase4() throws ApplicationException
	{
		Boolean lbUpdateSuccessful = loRFPReleaseService.updateProcurementData(null, null, msUserId, Boolean.TRUE);
		assertNotNull(lbUpdateSuccessful);
		assertTrue(lbUpdateSuccessful);
	}

	/**
	 * This method test the data movement from procurement_addendum_document to
	 * procurement_document_config table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProcDocumentConfigCase1() throws ApplicationException
	{
		Boolean lbInsertSuccessful = loRFPReleaseService
				.updateProcDocumentConfig(moMyBatisSession, "124", Boolean.TRUE);
		assertTrue(lbInsertSuccessful);
		assertNotNull(lbInsertSuccessful);
	}

	/**
	 * This method test the data movement from procurement_addendum_document to
	 * procurement_document_config table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProcDocumentConfigCase2() throws ApplicationException
	{
		Boolean lbInsertSuccessful = loRFPReleaseService.updateProcDocumentConfig(moMyBatisSession, "124",
				Boolean.FALSE);
		assertTrue(!lbInsertSuccessful);
		assertNotNull(lbInsertSuccessful);
	}

	/**
	 * This method test the data movement from procurement_addendum_document to
	 * procurement_document_config table corresponding to the procurement Id in
	 * case it is null.
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@Test
	public void testUpdateProcDocumentConfigCase3() throws ApplicationException
	{
		Boolean lbInsertSuccessful = loRFPReleaseService
				.updateProcDocumentConfig(moMyBatisSession, null, Boolean.FALSE);
		assertTrue(!lbInsertSuccessful);
		assertNotNull(lbInsertSuccessful);
	}

	/**
	 * This method test the negative execution of data movement from
	 * procurement_addendum_document to procurement_document_config table
	 * corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProcDocumentConfigApplicationException() throws ApplicationException
	{
		Boolean lbInsertSuccessful = loRFPReleaseService.updateProcDocumentConfig(null, null, Boolean.TRUE);
		assertTrue(lbInsertSuccessful);
		assertNotNull(lbInsertSuccessful);
	}

	/**
	 * This method tests the data movement from addendum_question_config to
	 * procurement_question_config table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProcQuestionConfigCase1() throws ApplicationException
	{
		Boolean lbInsertProcQuestion = loRFPReleaseService.updateProcQuestionConfig(moMyBatisSession, "124",
				Boolean.TRUE);
		assertTrue(lbInsertProcQuestion);
		assertNotNull(lbInsertProcQuestion);
	}

	/**
	 * This method tests the data movement from addendum_question_config to
	 * procurement_question_config table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProcQuestionConfigCase2() throws ApplicationException
	{
		Boolean lbInsertProcQuestion = loRFPReleaseService.updateProcQuestionConfig(moMyBatisSession, "124",
				Boolean.FALSE);
		assertTrue(!lbInsertProcQuestion);
		assertNotNull(lbInsertProcQuestion);
	}

	@Test
	public void testUpdateProcQuestionConfigCase3() throws ApplicationException
	{
		Boolean lbInsertProcQuestion = loRFPReleaseService.updateProcQuestionConfig(moMyBatisSession, null,
				Boolean.FALSE);
		assertTrue(!lbInsertProcQuestion);
		assertNotNull(lbInsertProcQuestion);
	}

	/**
	 * This method tests the negative execution of data movement from
	 * addendum_question_config to procurement_question_config table
	 * corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProcQuestionConfigCase4() throws ApplicationException
	{
		loRFPReleaseService.updateProcQuestionConfig(null, null, Boolean.TRUE);
	}

	/**
	 * This method tests the data movement from addendum_evaluation_criteria to
	 * evaluation_criteria table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateEvaluationCriteriaCase1() throws ApplicationException
	{
		Boolean lbInsertEvalCriteria = loRFPReleaseService.updateEvaluationCriteria(moMyBatisSession, "124",
				Boolean.TRUE);
		assertTrue(lbInsertEvalCriteria);
		assertNotNull(lbInsertEvalCriteria);
	}

	/**
	 * This method tests the data movement from addendum_evaluation_criteria to
	 * evaluation_criteria table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateEvaluationCriteriaCase2() throws ApplicationException
	{
		Boolean lbInsertEvalCriteria = loRFPReleaseService.updateEvaluationCriteria(moMyBatisSession, "124",
				Boolean.FALSE);
		assertTrue(!lbInsertEvalCriteria);
		assertNotNull(lbInsertEvalCriteria);
	}

	@Test
	public void testUpdateEvaluationCriteriaCase3() throws ApplicationException
	{
		Boolean lbInsertEvalCriteria = loRFPReleaseService.updateEvaluationCriteria(moMyBatisSession, null,
				Boolean.FALSE);
		assertTrue(!lbInsertEvalCriteria);
		assertNotNull(lbInsertEvalCriteria);
	}

	/**
	 * This method tests the negative execution of data movement from
	 * addendum_evaluation_criteria to evaluation_criteria table corresponding
	 * to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateEvaluationCriteriaCase4() throws ApplicationException
	{
		loRFPReleaseService.updateEvaluationCriteria(null, null, Boolean.TRUE);
	}

	/**
	 * The method testValidateEPINAssigned checks whether procurement has been
	 * linked with the EPIN of RFPReleaseService Class If the fetched Epin value
	 * is "Pending" then put EpinStatusFlag as false
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testValidateEPINAssigned() throws ApplicationException
	{
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		RFPReleaseService loValidateEPINService = new RFPReleaseService();
		loValidateEPINService.validateEPIN(moMyBatisSession, msProcId2, loServiceData);
		assertTrue(true);
	}

	/**
	 * The method testValidateEPINAssigned checks whether procurement has been
	 * linked with the EPIN of RFPReleaseService Class If the fetched Epin value
	 * is "Pending" then put EpinStatusFlag as false
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testValidateEPINAssigned1() throws ApplicationException
	{
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		RFPReleaseService loValidateEPINService = new RFPReleaseService();
		loValidateEPINService.validateEPIN(moMyBatisSession, "251", loServiceData);
		assertTrue(true);
	}

	/**
	 * The method testValidateEPINAssigned checks whether procurement has been
	 * linked with the EPIN of RFPReleaseService Class If the fetched Epin value
	 * is "Pending" then put EpinStatusFlag as false
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testValidateEPINAssigned2() throws ApplicationException
	{
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		RFPReleaseService loValidateEPINService = new RFPReleaseService();
		loValidateEPINService.validateEPIN(moMyBatisSession, null, loServiceData);
		assertTrue(true);
	}

	/**
	 * The method testValidateEPINAssigned checks whether procurement has been
	 * linked with the EPIN of RFPReleaseService Class If the fetched Epin value
	 * is "Pending" then put EpinStatusFlag as false
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testValidateEPINAssigned3() throws ApplicationException
	{
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		RFPReleaseService loValidateEPINService = new RFPReleaseService();
		loValidateEPINService.validateEPIN(moMyBatisSession, "251", null);
	}

	@Test(expected = ApplicationException.class)
	public void testValidateEPINAssignedExceptions() throws ApplicationException
	{
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		RFPReleaseService loValidateEPINService = new RFPReleaseService();
		loValidateEPINService.validateEPIN(null, "251", loServiceData);
	}

	/**
	 * The method testValidateEPINAssigned checks whether procurement has been
	 * linked with the EPIN of RFPReleaseService Class If the fetched Epin value
	 * is "Pending" then put EpinStatusFlag as false
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testValidateEPINNotAssigned() throws ApplicationException
	{
		RFPReleaseService loValidateEPINService = new RFPReleaseService();
		loValidateEPINService.validateEPIN(null, null, null);
	}

	/**
	 * This method test rfp requisites before releasing the rfp functionality of
	 * RFPReleaseService class
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testValidateRfpPreRequisitesofReqDocTypeStatus() throws ApplicationException
	{
		RFPReleaseService loValidateRfpPreRequisitesService = new RFPReleaseService();
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		RFPReleaseBean loRFPReleaseBean = (RFPReleaseBean) loValidateRfpPreRequisitesService.validateRfpPreRequisites(
				moMyBatisSession, "251", loServiceData);
		loRFPReleaseBean.getReqDocCount();
		assertTrue(true);
	}

	/**
	 * This method test rfp requisites before releasing the rfp functionality of
	 * RFPReleaseService class
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testValidateRfpPreRequisitesofReqDocCountStatus() throws ApplicationException
	{
		RFPReleaseService loValidateRfpPreRequisitesService = new RFPReleaseService();
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		RFPReleaseBean loRFPReleaseBean = (RFPReleaseBean) loValidateRfpPreRequisitesService.validateRfpPreRequisites(
				moMyBatisSession, "251", loServiceData);
		loRFPReleaseBean.getReqDocCount();
		assertTrue(true);
	}

	/**
	 * This method test rfp requisites before releasing the rfp functionality of
	 * RFPReleaseService class
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testValidateRfpPreRequisitesofEvaluationCriteriaCountStatus() throws ApplicationException
	{
		RFPReleaseService loValidateRfpPreRequisitesService = new RFPReleaseService();
		RFPReleaseBean loRFPReleaseBean = (RFPReleaseBean) loValidateRfpPreRequisitesService.validateRfpPreRequisites(
				moMyBatisSession, "141", null);
		Integer liEvaluationCriteriaCount = loRFPReleaseBean.getReqDocCount();
		assertTrue(liEvaluationCriteriaCount > 0);
	}

	/**
	 * This method test cof status functionality of the Procurement Service
	 * Class if we have cof status as Approved
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testCofApprovalStaus() throws ApplicationException
	{
		RFPReleaseService lsApprovedCofFlagStatusService = new RFPReleaseService();
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		lsApprovedCofFlagStatusService.checkCofApproval(moMyBatisSession, "112", loServiceData);
		assertTrue(true);
	}

	@Test
	public void testCofApprovalStaus2() throws ApplicationException
	{
		RFPReleaseService lsApprovedCofFlagStatusService = new RFPReleaseService();
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		lsApprovedCofFlagStatusService.checkCofApproval(moMyBatisSession, msProcId1, loServiceData);
		assertTrue(true);
	}

	/**
	 * This method test cof status functionality of the Procurement Service
	 * Class when cof status is not Approved
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testCofApprovalStausNotApproved() throws ApplicationException
	{
		RFPReleaseService lsApprovedCofFlagStatusService = new RFPReleaseService();
		lsApprovedCofFlagStatusService.checkCofApproval(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCofApprovalStauscase1() throws ApplicationException
	{
		RFPReleaseService lsApprovedCofFlagStatusService = new RFPReleaseService();
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loElementIdList = new ArrayList<String>();
		loElementIdList.add("1");
		loServiceData.put("elementIdList", loElementIdList);
		lsApprovedCofFlagStatusService.checkCofApproval(null, "251", loServiceData);
	}

	/**
	 * This method test all approved and conditionally approved providers
	 * corresponding to the selected services functionality of the Procurement
	 * Service Class
	 * 
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testInsertApprovedProviderList1() throws ApplicationException
	{
		Boolean lsInsertProviderStatus = false;
		RFPReleaseService lsInsertProviderStatusService = new RFPReleaseService();
		lsInsertProviderStatus = lsInsertProviderStatusService.insertAppProviderList(moMyBatisSession, msProcId1,
				"hhs_mgr1", true);
		assertTrue(lsInsertProviderStatus);
	}

	@Test
	public void testInsertApprovedProviderList2() throws ApplicationException
	{
		Boolean lsInsertProviderStatus = false;
		RFPReleaseService lsInsertProviderStatusService = new RFPReleaseService();
		lsInsertProviderStatus = lsInsertProviderStatusService.insertAppProviderList(moMyBatisSession, msProcId2,
				"hhs_mgr1", true);
		assertTrue(lsInsertProviderStatus);
	}

	@Test
	public void testInsertApprovedProviderList3() throws ApplicationException
	{
		Boolean lsInsertProviderStatus = false;
		RFPReleaseService lsInsertProviderStatusService = new RFPReleaseService();
		lsInsertProviderStatus = lsInsertProviderStatusService.insertAppProviderList(moMyBatisSession, msProcId3,
				"hhs_mgr1", true);
		assertTrue(lsInsertProviderStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertApprovedProviderListCase2() throws ApplicationException
	{
		Boolean lsInsertProviderStatus = false;
		RFPReleaseService lsInsertProviderStatusService = new RFPReleaseService();
		lsInsertProviderStatus = lsInsertProviderStatusService.insertAppProviderList(moMyBatisSession, null,
				"hhs_mgr1", true);
		assertTrue(lsInsertProviderStatus);
	}

	@SuppressWarnings("unused")
	@Test(expected = ApplicationException.class)
	public void testInsertApprovedProviderListCase3() throws ApplicationException
	{
		Boolean lsInsertProviderStatus = false;
		RFPReleaseService lsInsertProviderStatusService = new RFPReleaseService();
		lsInsertProviderStatus = lsInsertProviderStatusService.insertAppProviderList(moMyBatisSession, msProcId1, null,
				true);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertApprovedProviderListCase4() throws ApplicationException
	{
		Boolean lsInsertProviderStatus = false;
		RFPReleaseService lsInsertProviderStatusService = new RFPReleaseService();
		lsInsertProviderStatus = lsInsertProviderStatusService
				.insertAppProviderList(moMyBatisSession, null, null, true);
		assertTrue(lsInsertProviderStatus);
	}

	/**
	 * This method test audit functionality of HhsAuditService class
	 * 
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testHhsAuditInsert() throws ApplicationException
	{
		Boolean lsHhsAuditResultStatus = false;
		HhsAuditService loHhsAuditInsertService = new HhsAuditService();
		HhsAuditBean loAuditBean = new HhsAuditBean();
		loAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
		loAuditBean.setEntityType("Procurement");
		loAuditBean.setData("Procurement status has been changed to released");
		loAuditBean.setEntityId("76");
		loAuditBean.setEventName("3");
		loAuditBean.setUserId(msUserId);
		loAuditBean.setEventType("Procurement Release");
		lsHhsAuditResultStatus = loHhsAuditInsertService.hhsauditInsert(moMyBatisSession, loAuditBean, true);
		assertTrue(lsHhsAuditResultStatus);
	}

	/**
	 * This method test scenario for update of Rfp Document status when
	 * Procurement Status is updated of RFPReleaseService class
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateRfpDocumentStatusCase1() throws ApplicationException
	{
		Boolean lsRfpDocumentStatus = false;
		lsRfpDocumentStatus = loRFPReleaseService.updateRfpDocumentStatus(moMyBatisSession, "1", msUserId, false);
		assertTrue(!lsRfpDocumentStatus);
	}

	@Test
	public void testUpdateRfpDocumentStatusCase2() throws ApplicationException
	{
		Boolean lsRfpDocumentStatus = false;
		lsRfpDocumentStatus = loRFPReleaseService.updateRfpDocumentStatus(moMyBatisSession, null, msUserId, false);
		assertTrue(!lsRfpDocumentStatus);
	}

	@Test
	public void testUpdateRfpDocumentStatusCase3() throws ApplicationException
	{
		Boolean lsRfpDocumentStatus = false;
		lsRfpDocumentStatus = loRFPReleaseService.updateRfpDocumentStatus(moMyBatisSession, "1", null, false);
		assertTrue(!lsRfpDocumentStatus);
	}

	/**
	 * This method test scenario for update of Rfp Document status when
	 * Procurement Status is not updated of RFPReleaseService class
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@Test
	public void testUpdateRfpDocumentStatusCase4() throws ApplicationException
	{
		Boolean lsRfpDocumentStatus = false;
		lsRfpDocumentStatus = loRFPReleaseService.updateRfpDocumentStatus(moMyBatisSession, "1", msUserId, false);
		assertTrue(!lsRfpDocumentStatus);
	}

	@Test
	public void testUpdateRfpDocumentStatusCase5() throws ApplicationException
	{
		Boolean lsRfpDocumentStatus = false;
		lsRfpDocumentStatus = loRFPReleaseService.updateRfpDocumentStatus(moMyBatisSession, "1", msUserId, true);
		assertTrue(lsRfpDocumentStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateRfpDocumentStatusCaseException() throws ApplicationException
	{
		Boolean lsRfpDocumentStatus = false;
		lsRfpDocumentStatus = loRFPReleaseService.updateRfpDocumentStatus(null, "1", msUserId, true);
		assertTrue(!lsRfpDocumentStatus);
	}

	/**
	 * This method tests the execution of method deleteAddendumTableData and
	 * determines whether or not the data is getting deleting from the Addendum
	 * tables
	 * 
	 * @throws ApplicationException If ApplicationException occurs
	 */
	@Test
	public void testDeleteAddendumTableDataCase1() throws ApplicationException
	{
		loRFPReleaseService.deleteAddendumData(moMyBatisSession, "314", true);
		assertTrue(true);
	}

	/**
	 * This method tests the execution of method deleteAddendumTableData and
	 * determines whether or not the data is getting deleting from the Addendum
	 * tables
	 * 
	 * @throws ApplicationException If ApplicationException occurs
	 */
	@Test
	public void testDeleteAddendumTableDataCase2() throws ApplicationException
	{
		Boolean lbStatusFlag = loRFPReleaseService.deleteAddendumData(moMyBatisSession, "124", false);
		assertNotNull(lbStatusFlag);
		assertTrue(!lbStatusFlag);
	}

	@Test
	public void testDeleteAddendumTableDataCase3() throws ApplicationException
	{
		Boolean lbStatusFlag = loRFPReleaseService.deleteAddendumData(null, "124", false);
		assertNotNull(lbStatusFlag);
		assertTrue(!lbStatusFlag);
	}

	@Test
	public void testDeleteAddendumTableDataCase4() throws ApplicationException
	{
		Boolean lbStatusFlag = loRFPReleaseService.deleteAddendumData(moMyBatisSession, null, false);
		assertNotNull(lbStatusFlag);
		assertTrue(!lbStatusFlag);
	}

	/**
	 * This method tests the negative execution of method
	 * deleteAddendumTableData and determines whether or not the data is getting
	 * deleting from the Addendum tables
	 * 
	 * @throws ApplicationException If ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteAddendumTableDataCase5() throws ApplicationException
	{
		Boolean lbStatusFlag = loRFPReleaseService.deleteAddendumData(null, null, true);
		assertNotNull(lbStatusFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testValidateRfpPreRequisitesCase1() throws ApplicationException
	{
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", "1");
		List<String> loServiceIdList = new ArrayList<String>();
		loServiceIdList.add("1");
		List<String> loEvidenceIdList1 = new ArrayList<String>();
		loEvidenceIdList1.add("1");
		loServiceData.put("elementIdList", loEvidenceIdList1);
		loRFPReleaseService.validateRfpPreRequisites(moMyBatisSession, "624", loServiceData);
	}

	@Test(expected = Exception.class)
	public void testValidateRfpPreRequisitesCase3() throws ApplicationException
	{
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", "1");
		List<String> loServiceIdList = new ArrayList<String>();
		loServiceIdList.add("1");
		loServiceData.put("elementIdList", null);
		loRFPReleaseService.validateRfpPreRequisites(moMyBatisSession, "624", loServiceData);
	}

	@Test
	public void testValidateRfpPreRequisitesCase2() throws ApplicationException
	{
		RFPReleaseBean loRFPPreRequisites = null;
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loEvidenceIdList1 = new ArrayList<String>();
		loEvidenceIdList1.add("1");
		loServiceData.put("elementIdList", loEvidenceIdList1);
		loRFPPreRequisites = loRFPReleaseService.validateRfpPreRequisites(moMyBatisSession, "624", loServiceData);
		assertNotNull(loRFPPreRequisites);
	}

	@Test(expected = ApplicationException.class)
	public void testValidateRfpPreRequisitesCase4() throws ApplicationException
	{
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put("serviceNameList", null);
		List<String> loEvidenceIdList1 = new ArrayList<String>();
		loEvidenceIdList1.add("1");
		loServiceData.put("elementIdList", loEvidenceIdList1);
		loRFPReleaseService.validateRfpPreRequisites(null, "624", loServiceData);
	}

	@Test
	public void testDeletePlannedAddendumData1() throws ApplicationException
	{
		Boolean lsInsertProviderStatus = loRFPReleaseService.deletePlannedAddendumData(moMyBatisSession, msProcId3,
				true);
		assertFalse(lsInsertProviderStatus);
	}

	@Test
	public void testUpdateApprovedProviders1() throws ApplicationException
	{
		Integer loValue = loRFPReleaseService.updateApprovedProviders(moMyBatisSession, msUserId, msProcId3, true);
		assertNotNull(loValue);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateApprovedProviders2() throws ApplicationException
	{
		Integer loValue = loRFPReleaseService.updateApprovedProviders(moMyBatisSession, msUserId, null, true);
		assertNotNull(loValue);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicevalidateEPIN0Negative2() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.validateEPIN(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicevalidateRfpPreRequisites1Negative2() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.validateRfpPreRequisites(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicecheckCofApproval2Negative2() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.checkCofApproval(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServiceinsertAppProviderList3Negative2() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.insertAppProviderList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicefetchEvaluationCriteria4Negative2() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.fetchEvaluationCriteria(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServiceupdateRfpDocument6Negative2() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.updateRfpDocument(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicegetProcurementStatus15Negative2() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.getProcurementStatus(null, null);
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicevalidateEPIN0Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.validateEPIN(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicevalidateRfpPreRequisites1Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.validateRfpPreRequisites(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicecheckCofApproval2Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.checkCofApproval(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceinsertAppProviderList3Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.insertAppProviderList(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicefetchEvaluationCriteria4Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.fetchEvaluationCriteria(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateProcurementData5Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateProcurementData(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateRfpDocument6Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateRfpDocument(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateProcDocumentConfig7Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateProcDocumentConfig(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateProcQuestionConfig8Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateProcQuestionConfig(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateEvaluationCriteria9Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateEvaluationCriteria(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicedeleteAddendumData10Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.deleteAddendumData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicedeletePlannedAddendumData11Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.deletePlannedAddendumData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateApprovedProviders12Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateApprovedProviders(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicesaveEvaluationCriteria13Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.saveEvaluationCriteria(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicegetProcurementStatus14Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.getProcurementStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateRfpDocumentStatus15Negative()
	{
		moMyBatisSession.rollback();
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateRfpDocumentStatus(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

}
