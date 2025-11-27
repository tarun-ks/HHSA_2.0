/**
 * 
 */
package com.nyc.hhs.daomanager.servicetest;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.nyc.hhs.daomanager.service.ContractBudgetService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.util.DAOUtil;


@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class ContractBudgetServiceTestR6
{
	ContractBudgetService contractBudgetService = new ContractBudgetService();
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
		/*
		 * catch (Exception loEx) { lbThrown = true;
		 * assertTrue("Exception thrown", lbThrown); }
		 */
		finally
		{
			/*
			 * moSession.rollback(); moSession.close();
			 */
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void testMockito() throws ApplicationException{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		CBGridBean loCbGrid = new CBGridBean();
		contractBudgetService.fetchSalariedDetailGridData(null,loCbGrid);
	}
	
	/*@Test(expected = Exception.class)
	public void fetchSalariedDetailGridDataCase1() throws ApplicationException
	
	{
		contractBudgetService.fetchSalariedDetailGridData(null,null);
		
	}*/
	@Test(expected = ApplicationException.class)
	public void fetchSalariedDetailGridDataCase2() throws ApplicationException
	
	{
		CBGridBean loCbGrid = new CBGridBean();
		contractBudgetService.fetchSalariedDetailGridData(null,loCbGrid);
		
	}
	@Test
	public void fetchSalariedDetailGridDataCase3() throws ApplicationException
	
	{
		CBGridBean loCbGrid = new CBGridBean();
		loCbGrid.setSubBudgetID("45632");
		contractBudgetService.fetchSalariedDetailGridData(moSession,loCbGrid);
		
	}
	@Test(expected = ApplicationException.class)
	public void fetchHourlyDetailGridDataCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		CBGridBean loCbGrid = new CBGridBean();		
		contractBudgetService.fetchHourlyDetailGridData(null,loCbGrid);
		
	}
	@Test(expected = ApplicationException.class)
	public void fetchHourlyDetailGridDataCase2() throws ApplicationException
	
	{
		CBGridBean loCbGrid = new CBGridBean();
		contractBudgetService.fetchHourlyDetailGridData(null,loCbGrid);
		
	}
	@Test
	public void fetchHourlyDetailGridDataCase3() throws ApplicationException
	
	{
		CBGridBean loCbGrid = new CBGridBean();
		loCbGrid.setSubBudgetID("45632");
		contractBudgetService.fetchHourlyDetailGridData(moSession,loCbGrid);
		
	}
	@Test(expected = ApplicationException.class)
	public void addDetailedEmployeeBudgetCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();	
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		contractBudgetService.addDetailedEmployeeBudget(null,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void addDetailedEmployeeBudgetCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		contractBudgetService.addDetailedEmployeeBudget(null,loPsBudget);
		
	}
	@Test
	public void addDetailedEmployeeBudgetCase3() throws ApplicationException
	
	{
		// [id=new_row, empPosition=2, unit=0, budgetAmount=200, invoicedAmount=0, positionId=null,
		//empType=null, remainingAmount=0, modificationAmount=0, amendmentAmount=0, proposedBudgetAmount=0, 
		//fringeBenifits=Fringe Total, parentId=null, modificationUnit=0, amendmentUnit=0,internalTitle=qq,
		//annualSalary=21.00,hourPerYear=2.00,rate=0,totalPositions=null,totalCityFtes=null]
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setEmpType("1");
		loPsBudget.setEmpPosition("2");
		loPsBudget.setUnit("0");
		loPsBudget.setBudgetAmount("200");
		loPsBudget.setInvoicedAmount("0");
		loPsBudget.setAnnualSalary("21.00");
		loPsBudget.setHourPerYear("2");
		loPsBudget.setRate("0");
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		loPsBudget.setInternalTitle("qqqq");
		loPsBudget.setContractBudgetID("10730");
		loPsBudget.setSubBudgetID("14124");
		contractBudgetService.addDetailedEmployeeBudget(moSession,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void updateSummaryIdCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();	
		contractBudgetService.updateSummaryId(null,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void updateSummaryIdCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.updateSummaryId(null,loPsBudget);
		
	}
	@Test
	public void updateSummaryIdCase3() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setEmpType("1");
		loPsBudget.setSubBudgetID("14124");
		contractBudgetService.updateSummaryId(moSession,loPsBudget);		
	}
	@Test(expected = ApplicationException.class)
	public void delDetailEmployeeBudgetCase1() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		contractBudgetService.delDetailEmployeeBudget(null,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void delDetailEmployeeBudgetCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		contractBudgetService.delDetailEmployeeBudget(null,loPsBudget);
		
	}
	@Test
	public void delDetailEmployeeBudgetCase3() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		loPsBudget.setId("133");
		loPsBudget.setSubBudgetID("14169");
		contractBudgetService.delDetailEmployeeBudget(moSession,loPsBudget);		
	}
	@Test(expected = ApplicationException.class)
	public void editDetailEmployeeBudgetCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		contractBudgetService.editDetailEmployeeBudget(null,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void editDetailEmployeeBudgetCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		contractBudgetService.editDetailEmployeeBudget(null,loPsBudget);
		
	}
	@Test
	public void editDetailEmployeeBudgetCase3() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setEmpType("1");
		loPsBudget.setEmpPosition("2");
		loPsBudget.setUnit("0");
		loPsBudget.setBudgetAmount("200");
		loPsBudget.setInvoicedAmount("0");
		loPsBudget.setAnnualSalary("21.00");
		loPsBudget.setHourPerYear("2");
		loPsBudget.setRate("0");
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		loPsBudget.setInternalTitle("qqqq");
		loPsBudget.setContractBudgetID("10730");
		loPsBudget.setSubBudgetID("14124");
		loPsBudget.setModifyByProvider("3313");
		contractBudgetService.editDetailEmployeeBudget(moSession,loPsBudget);		
	}
	@Test(expected = Exception.class)
	public void fetchFringeBenefitsDetailCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setSubBudgetID("12");
		contractBudgetService.fetchFringeBenefitsDetail(null,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void fetchFringeBenefitsDetailCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setSubBudgetID("12");
		contractBudgetService.fetchFringeBenefitsDetail(null,loPsBudget);
		
	}
	@Test
	public void fetchFringeBenefitsDetailCase3() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setSubBudgetID("34213");
		contractBudgetService.fetchFringeBenefitsDetail(moSession,loPsBudget);		
	}
	@Test
	public void fetchFringeBenefitsDetailCase4() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchFringeBenefitsDetail(moSession,loPsBudget);		
	}
	/*@Test
	public void fetchFringeBenefitsDetailCase5() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchFringeBenefitsDetail(moSession,loPsBudget);		
	}*/
	@Test(expected = ApplicationException.class)
	public void editFringeBenefitsDetailCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.editFringeBenefitsDetail(null,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void editFringeBenefitsDetailCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.editFringeBenefitsDetail(null,loPsBudget);
		
	}
	@Test
	public void editFringeBenefitsDetailCase3() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setEmpType("1");
		loPsBudget.setEmpPosition("2");
		loPsBudget.setUnit("0");
		loPsBudget.setBudgetAmount("200");
		loPsBudget.setInvoicedAmount("0");
		loPsBudget.setAnnualSalary("21.00");
		loPsBudget.setHourPerYear("2");
		loPsBudget.setRate("0");
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		loPsBudget.setInternalTitle("qqqq");
		loPsBudget.setContractBudgetID("10730");
		loPsBudget.setSubBudgetID("14124");
		loPsBudget.setModifyByProvider("3313");
		loPsBudget.setId("1234");
		contractBudgetService.editFringeBenefitsDetail(moSession,loPsBudget);		
	}
	@Test(expected = ApplicationException.class)
	public void fetchFringeBenefitsSummaryCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchFringeBenefitsSummary(null,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void fetchFringeBenefitsSummaryCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchFringeBenefitsSummary(null,loPsBudget);
		
	}
	@Test
	public void fetchFringeBenefitsSummaryCase3() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setEmpType("1");
		loPsBudget.setEmpPosition("2");
		loPsBudget.setUnit("0");
		loPsBudget.setBudgetAmount("200");
		loPsBudget.setInvoicedAmount("0");
		loPsBudget.setAnnualSalary("21.00");
		loPsBudget.setHourPerYear("2");
		loPsBudget.setRate("0");
		loPsBudget.setTransactionName("salariedPositionDetailsGridAdd");
		loPsBudget.setInternalTitle("qqqq");
		loPsBudget.setContractBudgetID("10730");
		loPsBudget.setSubBudgetID("14124");
		loPsBudget.setModifyByProvider("3313");
		loPsBudget.setId("1234");
		contractBudgetService.fetchFringeBenefitsSummary(moSession,loPsBudget);		
	}
	@Test(expected = ApplicationException.class)
	public void fetchNonGridForPSSummaryDataCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchNonGridForPSSummaryData(null,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void fetchNonGridForPSSummaryDataCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchNonGridForPSSummaryData(null,loPsBudget);
		
	}
	@Test
	public void fetchNonGridForPSSummaryDataCase3() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setSubBudgetID("1234");
		contractBudgetService.fetchNonGridForPSSummaryData(moSession,loPsBudget);		
	}
	@Test(expected = ApplicationException.class)
	public void fetchBudgetApprovedDateCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchBudgetApprovedDate(null,loPsBudget);	
	}
	@Test(expected = ApplicationException.class)
	public void fetchBudgetApprovedDateCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchBudgetApprovedDate(null,loPsBudget);
		
	}
	@Test
	public void fetchBudgetApprovedDateCase3() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setContractBudgetID("12343");
		contractBudgetService.fetchBudgetApprovedDate(moSession,loPsBudget);		
	}
	@Test
	public void fetchBudgetApprovedDateCase4() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setContractBudgetID("10783");
		contractBudgetService.fetchBudgetApprovedDate(moSession,loPsBudget);		
	}
	@Test(expected = ApplicationException.class)
	public void checkIfOtherBudgetApprovedCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetService.checkIfBaseBudgetApproved(null,"");
		
	}
	@Test(expected = ApplicationException.class)
	public void checkIfOtherBudgetApprovedCase2() throws ApplicationException
	
	{
		contractBudgetService.checkIfBaseBudgetApproved(null,"");
		
	}
	@Test
	public void checkIfOtherBudgetApprovedCase3() throws ApplicationException
	
	{
		contractBudgetService.checkIfBaseBudgetApproved(moSession,"10732");		
	}
	@Test
	public void checkIfOtherBudgetApprovedCase4() throws ApplicationException
	
	{
		contractBudgetService.checkIfBaseBudgetApproved(moSession,"33750");		
	}
	@Test(expected = ApplicationException.class)
	public void fetchNonGridForPSDetailDataCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchNonGridForPSDetailData(null,loPsBudget);
		
	}
	@Test(expected = ApplicationException.class)
	public void fetchNonGridForPSDetailDataCase2() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		contractBudgetService.fetchNonGridForPSDetailData(null, loPsBudget);
		
	}
	@Test
	public void fetchNonGridForPSDetailDataCase3() throws ApplicationException
	
	{
		PersonnelServiceBudget loPsBudget = new PersonnelServiceBudget();
		loPsBudget.setSubBudgetID("22311");
		contractBudgetService.fetchNonGridForPSDetailData(moSession, loPsBudget);		
	}
	
	@Test
	public void fetchFinancialDocumentsTest1() throws ApplicationException
	
	{
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("returnPaymentDetailId", "417");
		aoParamMap.put("organizationType", "agency_org");
		contractBudgetService.fetchFinancialDocuments(moSession, aoParamMap);
		
	}  
	
	@Test
	public void fetchFinancialDocumentsTest2() throws ApplicationException
	
	{
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("contractId", "11287");
		aoParamMap.put("organizationType", "agency_org");
		contractBudgetService.fetchFinancialDocuments(moSession, aoParamMap);
		
	}  
	
	@Test(expected = ApplicationException.class)
	public void fetchFinancialDocumentsTest3() throws ApplicationException
	
	{
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("contractId", "11287");
		aoParamMap.put("organizationType", "agency_org");
		contractBudgetService.fetchFinancialDocuments(null, aoParamMap);	
		
	}  
	
	@Test
	public void removeFinancialDocsTest1() throws ApplicationException
	
	{
		Map<String, String> aoParameterMap = new HashMap<String, String>();
		
		aoParameterMap.put("hdnTableName", "contract_document");
		aoParameterMap.put("asDocumentSequence", "4428");
		aoParameterMap.put("asDeletedDocumentId", "{7348090B-2B63-4F29-A3B4-D5A9EA81F36B}");
		contractBudgetService.removeFinancialDocs(moSession, aoParameterMap);
		
	}  
	
	@Test
	public void removeFinancialDocsTest2() throws ApplicationException
	
	{
		Map<String, String> aoParameterMap = new HashMap<String, String>();
		
		aoParameterMap.put("hdnTableName", "budget_document");
		aoParameterMap.put("asDocumentSequence", "28903");
		aoParameterMap.put("asDeletedDocumentId", "{4DA79AD1-9F27-45D5-9816-95523D5B5975}");
		contractBudgetService.removeFinancialDocs(moSession, aoParameterMap);
		
	}  
	
	@Test
	public void removeFinancialDocsTest3() throws ApplicationException
	
	{
		Map<String, String> aoParameterMap = new HashMap<String, String>();
		
		aoParameterMap.put("hdnTableName", "RETURN_PAYMENT_DOCUMENT");
		aoParameterMap.put("asDocumentSequence", "417");
		aoParameterMap.put("asDeletedDocumentId", "{BE451300-BFF6-4C95-85BF-57CF8D6E9E46}");
		contractBudgetService.removeFinancialDocs(moSession, aoParameterMap);
		
	}  
	
	@Test
	public void removeFinancialDocsTest4() throws ApplicationException
	
	{
		Map<String, String> aoParameterMap = new HashMap<String, String>();
		
		aoParameterMap.put("asDocumentSequence", "417");
		aoParameterMap.put("asDeletedDocumentId", "{BE451300-BFF6-4C95-85BF-57CF8D6E9E46}");
		contractBudgetService.removeFinancialDocs(moSession, aoParameterMap);
		
	}  
}
