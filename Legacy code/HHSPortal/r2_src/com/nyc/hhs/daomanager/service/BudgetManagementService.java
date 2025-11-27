package com.nyc.hhs.daomanager.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.util.DAOUtil;

/**
 * This class will be responsible for fetching Account and Funding Source
 * Allocation details in Accelerator system.
 * 
 */

@Service
public class BudgetManagementService extends ServiceState
{

	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(BudgetManagementService.class);

	/**
	 * Gets Contract related details which needs to be shown on Contract
	 * Certification of Funds Task Screen.
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>Estimated Procurement Value - procurementValue</li>
	 * <li>Contract Value - contractValue</li>
	 * <li>Contract Start Date - contractStartDate</li>
	 * <li>Contract End Date - contractEndDate</li>
	 * <li>Certification of Funds Status - procurementStatus</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the
	 * <code>fetchProcurementCONDetails</code> query in the procurementMapper</li>
	 * <li>It returns the values as ProcurementCOF Bean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the contractFinancials.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @see BMCController
	 * @param aoMybatisSession SqlSession
	 * @param asContractId id on the basis of which Procurement and Contract
	 *            details will be fetched
	 * @return ProcurementCOF Bean
	 * @throws ApplicationException
	 */
	public ProcurementCOF fetchContractCofTaskDetails(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		ProcurementCOF loProcurementCOF = null;
		try
		{
			loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.BMC_FETCH_CONTRACT_COF_TASK_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Contract COF details fetched successfully for Contract Id:" + asContractId);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Contract COF Details");
			throw loExp;
		}
		return loProcurementCOF;
	}

	/**
	 * This method fetched the Amended Amount of an Amendment Contract and the
	 * Contract Amount of its base Contract which needs to be shown on Contract
	 * Certification of funds - Amendment Doc Screen. <li>This query used:
	 * fetchBaseAmendmentContractAmount</li>
	 * @param aoMybatisSession SqlSession object
	 * @param asContractId String
	 * @return loProcurementCOF ProcurementCOF object
	 * @throws ApplicationException ApplicationException object
	 */
	public ProcurementCOF fetchBaseAmendmentContractAmount(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		ProcurementCOF loProcurementCOF = null;
		try
		{
			loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.BMC_FETCH_BASE_AMENDMENT_CONTRACT_AMOUNT,
					HHSConstants.JAVA_LANG_STRING);
			String lsContractValueOrignal =(String)DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.FETCH_BASE_CONTRACT_AMOUNT,
					HHSConstants.JAVA_LANG_STRING);
			if(lsContractValueOrignal!=null)
			{
				loProcurementCOF.setContractValue(lsContractValueOrignal);
			}
			setMoState("Amendement Contract Value and Base Contract Value fetched successfully for Amendement Contract Id:"
					+ asContractId);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACT_ID, asContractId);
			LOG_OBJECT.Error("Error while fetching Amendement Contract Value and Base Contract Value", loExp);
			setMoState("Error while fetching Amendement Contract Value and Base Contract Value for Amendement Contract Id:"
					+ asContractId);

			throw loExp;
		}
		return loProcurementCOF;
	}

	/**
	 * Gets Contract related details which needs to be shown on Print Contract
	 * Certification of Funds Screen.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the <code>fetchR2ContractCofDetails</code>
	 * query in the BudgetMapper</li>
	 * <li>It returns the values as ProcurementCOF Bean object</li>
	 * <li>fetchContractSourceType query is executed to get the contract source
	 * whether R2 contract or R3 Contract</li></li> If R3 contract then
	 * fetchR3ContractCofDetails query is executed to get PROCUREMENT_VALUE and
	 * PROC_EPIN of R3 contracts.
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the contractCOFDoc.jsp</li>
	 * </ul>
	 * </li> </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param asContractId String
	 * @return
	 * @throws ApplicationException ApplicationException object
	 */
	public ProcurementCOF fetchContractCofDocDetails(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		ProcurementCOF loProcurementCOF = null;
		String lsContractSourceType = null;
		Map loProcurementDetails = null;
		try
		{
			loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.FETCH_R2_CONTRACT_COF_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
			// Get Contract Source : R2 Contract or R3 Contract
			lsContractSourceType = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_CONTRACT_SOURCE_TYPE,
					HHSConstants.JAVA_LANG_STRING);
			if (lsContractSourceType.equalsIgnoreCase(HHSConstants.R3_CONTRACT))
			{
				loProcurementDetails = (Map) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.FETCH_R3_CONTRACT_COF_DETAILS,
						HHSConstants.JAVA_LANG_STRING);
				loProcurementCOF.setProcurementValue(String.valueOf(loProcurementDetails
						.get(HHSConstants.PROCUREMENT_VALUE)));
				loProcurementCOF.setProcEpin(String.valueOf(loProcurementDetails.get(HHSConstants.PROC_EPIN_R3)));
			}
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACT_ID, asContractId);
			LOG_OBJECT.Error("Error while fetching Contract Related Values", loExp);
			setMoState("Error while getting Contract COF Details");
			throw loExp;
		}
		return loProcurementCOF;
	}

}
