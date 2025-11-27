package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ProposalStatusInfo;
import com.nyc.hhs.model.ReportBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
/**
 * This class is added for release 5
 * This service is used for calling methods part of Report module. 
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */
public class ReportService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ConfigurationService.class);

	
	/**This method get the report related information for the chart export and data grid 
	 * @param aoMybatisSession sql session as input
	 * @param aoReportBean Bean as input
	 * @return List of bean as output
	 * @throws ApplicationException Exception in case a query fails
	 * 
	 * 
	 * This method will fetch the report dashboard and detail information
	 * the accelerator/provider/agency user from reporting screen.
	 * <li>On the basis of fiscal year, the reporting information are
	 * received from the DataBase by executing the
	 * <code>getBudgetUtlizationDetails ,getBudgetUtlizationDetails_ForGrid ,getBudgetUtlizationDetails_ForGridInvoice ,
	 * getBudgetCatUtilization ,getBudgetCatUtilization_ForGrid ,getFundingSummaryDetails ,getProposalSummaryDetails ,
	 * getFundingSummaryDetails_ForGrid ,getRecievablesDetails ,getRecievablesDetails_ForGrid ,getAdvRecoupment ,
	 * getAdvRecoupment_ForGrid ,getProposalDetails ,getProposalDetails_ForGrid 
	 * </code> query in the
	 * ReportMapper</li>
	 * <li>
	 * For sorting, pagination and filter extended BaseFilter class, which will
	 * pass required information as input.</li>
	 * <li>It returns the values as List of Report Bean</li>
	 * <li>The values returned are used in the
	 * <code>BirtReportController</code> which in turns helps to display
	 * the information on the reportDashBoard.jsp and detailedReport.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<ReportBean> getReportData(SqlSession aoMybatisSession, ReportBean aoReportBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("START  getReportData sql id ",
				aoReportBean.getSqlId() + CommonUtil.getCurrentTimeInMilliSec());
		List<ReportBean> loReportDetails = null;
		try
		{
			if (null != aoReportBean.getProvider())
			{
				StringBuffer loProviderNameSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoReportBean.getProvider()).append(HHSConstants.PERCENT);
				aoReportBean.setProvider(loProviderNameSb.toString());
			}
			if (null != aoReportBean.getContractTitle())
			{
				StringBuffer loContractTitleSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoReportBean.getContractTitle()).append(HHSConstants.PERCENT);
				aoReportBean.setContractTitle(loContractTitleSb.toString());
			}
			if (null != aoReportBean.getProcurementTitle())
			{
				StringBuffer loProcTitleSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoReportBean.getProcurementTitle()).append(HHSConstants.PERCENT);
				aoReportBean.setProcurementTitle(loProcTitleSb.toString());
			}
			if (null != aoReportBean.getProgramName() && !aoReportBean.getProgramName().isEmpty())
			{
				StringBuffer loProgName = new StringBuffer(HHSConstants.PERCENT).append(aoReportBean.getProgramName())
						.append(HHSConstants.PERCENT);
				aoReportBean.setProgramName(loProgName.toString());
			}
			loReportDetails = (List<ReportBean>) DAOUtil.masterDAO(aoMybatisSession, aoReportBean,
					HHSR5Constants.REPORT_MAPPER_CLASS, aoReportBean.getSqlId(), HHSR5Constants.REPORT_BEAN_PATH);
			String lsProviderName = aoReportBean.getProvider();
			if (null != lsProviderName)
			{
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ONE);
				lsProviderName = lsProviderName.substring(HHSConstants.INT_ZERO, lsProviderName.length()
						- HHSConstants.INT_ONE);
				aoReportBean.setProvider(lsProviderName);
			}
			String lsContractTitle = aoReportBean.getContractTitle();
			if (null != lsContractTitle)
			{
				lsContractTitle = lsContractTitle.substring(HHSConstants.INT_ONE);
				lsContractTitle = lsContractTitle.substring(HHSConstants.INT_ZERO, lsContractTitle.length()
						- HHSConstants.INT_ONE);
				aoReportBean.setContractTitle(lsContractTitle);
			}
			String lsProcTitle = aoReportBean.getProcurementTitle();
			if (null != lsProcTitle)
			{
				lsProcTitle = lsProcTitle.substring(HHSConstants.INT_ONE);
				lsProcTitle = lsProcTitle.substring(HHSConstants.INT_ZERO, lsProcTitle.length() - HHSConstants.INT_ONE);
				aoReportBean.setProcurementTitle(lsProcTitle);
			}
			String lsProgName = aoReportBean.getProgramName();
			if (null != lsProgName && !lsProgName.isEmpty())
			{
				lsProgName = lsProgName.substring(HHSConstants.INT_ONE);
				lsProgName = lsProgName.substring(HHSConstants.INT_ZERO, lsProgName.length() - HHSConstants.INT_ONE);
				aoReportBean.setProgramName(lsProgName);
			}
			LOG_OBJECT.Debug("END  OF getReportData ", CommonUtil.getCurrentTimeInMilliSec());
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error in reporting Service while getting " + "reporting information", aoAppEx);
			setMoState("Error occured while fethcing report information");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error in reporting Service while getting " + "reporting information", aoExp);
			throw new ApplicationException("Error occured while fethcing report information", aoExp);
		}
		return loReportDetails;
	}

	/**This method will get the fiscal year information to display in the drop down.
	 * 	This method will fetch the fiscal year drop down information for
	 * the accelerator/provider/agency user from report screen.
	 * <li>Fiscal year information is received from the DataBase by executing the
	 * <code>getFirstFiscalYear</code> query in the
	 * ReportMapper</li>
	 * @param aoMybatisSession sql session as input
	 * @param aoReportBean ReportBean object
	 * @return fiscal year id
	 * @throws ApplicationException Exception  in case a query fails
	 */
	public List<Integer> getFirstFiscalYear(SqlSession aoMybatisSession, ReportBean aoReportBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("START  getReportData sql id ", CommonUtil.getCurrentTimeInMilliSec()); 
		List<Integer> loFiscalYearId = null;
		try
		{
			loFiscalYearId = (List<Integer>) DAOUtil.masterDAO(aoMybatisSession, aoReportBean,
					HHSR5Constants.REPORT_MAPPER_CLASS, aoReportBean.getFiscalYearSqlId(),
					HHSR5Constants.REPORT_BEAN_PATH);

			LOG_OBJECT.Debug("END  OF getReportData ", CommonUtil.getCurrentTimeInMilliSec());
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error in reporting Service while getting "
					+ "fiscal year information", aoAppEx);
			setMoState("Error occured while fethcing fiscal year information");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error in reporting Service while getting "
					+ "fiscal year information", aoExp);
			throw new ApplicationException("Error occured while get fiscal year information in the drop down", aoExp);
		}
		return loFiscalYearId;
	}

	/**This method get the list of proposal status while fetching proposal information
	 * for the accelerator/provider/agency user from report screen.
	 * <li>Proposal status information is received from the DataBase by executing the
	 * <code>getProposalStatusInfo</code> query in the
	 * ReportMapper</li>
	 * @param aoMybatisSession sql session as input
	 * @return List of proposal status as output
	 * @throws ApplicationException Exception in case query fails
	 */
	@SuppressWarnings("unchecked")
	public List<ProposalStatusInfo> getProposalStatusInfo(SqlSession aoMybatisSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("START  getProposalStatusInfo sql id ", CommonUtil.getCurrentTimeInMilliSec()); 
		List<ProposalStatusInfo> loProposalStatusInfo = new ArrayList<ProposalStatusInfo>();
		try
		{
			loProposalStatusInfo = (List<ProposalStatusInfo>) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSR5Constants.REPORT_MAPPER_CLASS, HHSR5Constants.GET_PROPOSAL_STATUS_INFO, null);
			LOG_OBJECT.Debug("END  OF getProposalStatusInfo ", CommonUtil.getCurrentTimeInMilliSec()); 
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error in reporting Service while getting "
					+ "proposal status information", aoAppEx);
			setMoState("Error occured while fethcing proposal status information");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error in reporting Service while getting "
					+ "proposal status information", aoExp);
			throw new ApplicationException("Error occured while getting propsal status information", aoExp);
		}
		return loProposalStatusInfo;
	}

}
