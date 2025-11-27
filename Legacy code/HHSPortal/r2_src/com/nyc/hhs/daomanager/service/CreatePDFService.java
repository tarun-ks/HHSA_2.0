package com.nyc.hhs.daomanager.service;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.session.SqlSession;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBudgetSummary;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.PDFBatch;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8ContentService;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class generates PDF for Contract Budget Summary, Contract Budget
 * Amendment Summary, COF details, and COF Amendment.
 * 
 */

public class CreatePDFService extends ServiceState
{
	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(CreatePDFService.class);

	/**
	 * This method is used to fetch the Financial Details of Contract PDF. <li>
	 * This service class is invoked through transaction id
	 * <b>getGridBeanDetailforProcAllocation<b> for Contract budget Summary and
	 * Amendment PDF screen</li> <li>This method getSubBudgetDetails will get
	 * the subBudgetDetails Information on the basis of budgetId & contractId</li>
	 * <ul>
	 * <li>A hashmap is passed to get the data.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession - Object
	 * @param asContractId String - Object
	 * @return loContractFyMap Map<String, String> -Object
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchContractFYDetails(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		Map<String, String> loContractFyMap = null;
		try
		{
			// Get the financial details of the contract
			loContractFyMap = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_CONTRACT_FISCAL_YEARS,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACT_ID, asContractId);
			LOG_OBJECT.Error("Error while fetching Contract Related Values", loExp);
			setMoState("Error while getting Contract COF Details");
			throw loExp;
		}
		return loContractFyMap;
	}

	/**
	 * This method is triggered to get the GridBeanDetail for ProcAllocation
	 * PDF. <li>This service class is invoked through transaction id
	 * <b>getGridBeanDetailforProcAllocation<b> for Contract budget Summary and
	 * Amendment PDF screen</li> <li>This method getSubBudgetDetails will get
	 * the subBudgetDetails Information on the basis of budgetId & contractId</li>
	 * <ul>
	 * <li>A hashmap is passed to get the data.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Object.
	 * @param asProcurementId String object.
	 * @param aoContractFyMap Map object.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loCBGridBean CBGridBean object
	 */

	public CBGridBean getGridBeanDetailforProcAllocation(SqlSession aoMybatisSession, String asProcurementId,
			Map<String, String> aoContractFyMap) throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		try
		{
			ProcurementCOF loTempProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.FETCH_PROC_COF_DETAILS_FINANCIALS_ORIGINAL, HHSConstants.JAVA_LANG_STRING);

			BaseControllerUtil.getContractFiscalYearsUtil(loTempProcurementCOF.getOrigContractStartDate(),
					loTempProcurementCOF.getOrigContractEndDate(), aoContractFyMap);
			int liFiscalStartYr = Integer.valueOf(String.valueOf(aoContractFyMap.get(HHSConstants.LI_START_YEAR)));
			loCBGridBean.setFiscalYearID(String.valueOf(liFiscalStartYr));
			loCBGridBean.setProcurementID(asProcurementId);

		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			LOG_OBJECT.Error("Error while fetching Contract Related Values", loExp);
			setMoState("Error while getting Contract COF Details");
			throw loExp;
		}
		return loCBGridBean;
	}

	/**
	 * This method is triggered to get the subBudgetDetails. <li>This service
	 * class is invoked through getSubBudgetDetails transaction id for Contract
	 * budget Summary and Amendment PDF screen</li> <li>This method
	 * getSubBudgetDetails will get the subBudgetDetails Information on the
	 * basis of budgetId & contractId</li>
	 * <ul>
	 * <li>A hashmap is passed to get the data.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Object.
	 * @param aoHashMap HashMap object.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loSubBudgetList CBGridBean object
	 */

	@SuppressWarnings("unchecked")
	public List<CBGridBean> getSubBudgetDetails(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<CBGridBean> loSubBudgetDetailsList = null;
		try
		{

			if (aoHashMap.get(HHSConstants.SUBBUDGET_ID) != null)
			{
				loSubBudgetDetailsList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_FETCH_SUB_BUDGET_SUMMARY_PRINT, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				loSubBudgetDetailsList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_SUB_BUDGET_SUMMARY,
						HHSConstants.JAVA_UTIL_HASH_MAP);

			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.BUDGET_ID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing SubBudget Information in CreatePDFService ", loAppEx);
			setMoState("Transaction Failed:: CreatePDFService: getSubBudgetDetails method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in CreatePDFService ", loAppEx);
			setMoState("Transaction Failed:: CreatePDFService: getSubBudgetDetails method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary", loAppEx);
		}
		return loSubBudgetDetailsList;
	}

	/**
	 * This method is used to fetch the Contract Title, Epin and Fisical Year
	 * details for Header Section in Amendment budgetSummary detail PDF.
	 * 
	 * @param aoMybatisSession SqlSession - Object
	 * @param aoHashMap HashMap - Object
	 * @return loAmendmnetFiscialEpinDetails ContractList - Object
	 * @throws ApplicationException -ApplicationException Object
	 */
	public ContractList getAmendmentFiscialEPINDetails(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		ContractList loAmendmnetFiscialEpinDetails = null;
		try
		{

			loAmendmnetFiscialEpinDetails = (ContractList) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_FETCH_AMENDMENT_FISCIAL_EPIN_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.BUDGET_ID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Title, Epin and Fisical Year details ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchSubBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchSubBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary", loAppEx);
		}
		return loAmendmnetFiscialEpinDetails;
	}

	/**
	 * Gets Contract budget summary amendmentPDF and Contract budget summaryPDF
	 * related details.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, header label ,asOutputFilePath and
	 * loPDFBatch the Contract Budget Summary PDF would be display.
	 * 
	 * @param aoMyBatisSession SqlSession - Object
	 * @param asOutputFilePath String - Object
	 * @param asHeaderLabel String - Object
	 * @param aoPDFBatch PDFBatch - Object
	 * @return loPathList List<String> - Object
	 * @throws ApplicationException ApplicationException - Object
	 */

	@SuppressWarnings("unchecked")
	public List<String> getContractBudgetSummaryDetails(SqlSession aoMyBatisSession, String asOutputFilePath,
			String asHeaderLabel, PDFBatch aoPDFBatch) throws ApplicationException
	{
		List<String> loPathList = new ArrayList<String>();
		if (aoPDFBatch.getSubEntityType().equalsIgnoreCase(HHSConstants.BUDGETLIST_BUDGET))
		{

			Font loCatFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD, BaseColor.BLUE);
			Font loLabelFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);
			Font loHeaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
			int liTotalIndirectCosts = 0;

			StringBuffer loSBfRealPath = new StringBuffer();
			loSBfRealPath.append(asOutputFilePath).append(HHSConstants.FORWARD_SLASH)
					.append(HHSConstants.CONTRACT_BUDGET_SUMMARY_DETAILS).append(HHSConstants.UNDERSCORE)
					.append(aoPDFBatch.getSubEntityId());
			HashMap<String, String> loBudgetSummaryMap = new HashMap<String, String>();
			String lsFieldXpath = HHSConstants.FIELD_X_PATH;
			Paragraph loBudgetSummaryPara = null;
			NumberFormat loCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
			Document loDocument = new Document();
			int liMaxTableCount = 0;
			File loPdfFile = null;
			FileOutputStream loFileOutputStream = null;
			HashMap loHashMap = new HashMap();
			try
			{
				loPdfFile = new File(loSBfRealPath.toString());
				if (!loPdfFile.exists())
				{
					loPdfFile.createNewFile();
				}
				loFileOutputStream = new FileOutputStream(loPdfFile);
				PdfWriter.getInstance(loDocument, loFileOutputStream);
				loDocument.open();
				loBudgetSummaryPara = new Paragraph(asHeaderLabel, loCatFont);
				loBudgetSummaryPara.setAlignment(Element.ALIGN_LEFT);
				PdfPTable loPdfTable = new PdfPTable(4);
				PdfPTable loPdfTable2 = new PdfPTable(4);
				loPdfTable.setWidths(new int[]
				{ 30, 10, 10, 10 });
				loPdfTable2.setWidths(new int[]
				{ 10, 10, 10, 10 });
				org.jdom.Document loFieldDocumentMapping = XMLUtil.getDomObj(CreatePDFService.class
						.getResourceAsStream(HHSConstants.AMENDMENT_BUDGET_SCREEN_FIELD_MAPPING));
				org.jdom.Element loFieldElement = (org.jdom.Element) XMLUtil.getElement(lsFieldXpath,
						loFieldDocumentMapping);
				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoPDFBatch.getEntityId());
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoPDFBatch.getSubEntityId());
				//Start Emergency release 4.0.2 made changes for pdf generation batch
				loHashMap.put(HHSConstants.USER_ID_2, HHSConstants.SYSTEM_USER);
				//End Emergency release 4.0.2 made changes for pdf generation batch
				ContractBudgetService loContractBudgetService = new ContractBudgetService();
				ContractList loCBGridBeanAmendmentFiscialEpin = loContractBudgetService.fetchContractSummary(
						aoMyBatisSession, loHashMap);
				getHeaderContent(loPdfTable2, loFieldDocumentMapping, loCBGridBeanAmendmentFiscialEpin, loDocument);
				loPdfTable2.setSpacingAfter(20);
				loDocument.add(loPdfTable2);
				BudgetDetails loBudgetDetails = loContractBudgetService.fetchFyBudgetSummary(aoMyBatisSession,
						loHashMap);

				PdfPTable loPdfTable3 = new PdfPTable(6);
				loPdfTable3.setWidths(new int[]
				{ 10, 10, 10, 10, 10, 10 });
				getFiscalYearBudgetInformationHeaderContent(loPdfTable3, loFieldDocumentMapping, loBudgetDetails,
						loDocument);

				liMaxTableCount = Integer.valueOf(loFieldElement.getAttributeValue(HHSConstants.TABLE_COUNT));
				loBudgetSummaryMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoPDFBatch.getSubEntityId());
				loBudgetSummaryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoPDFBatch.getEntityId());

				getTableContent(aoMyBatisSession, aoPDFBatch.getSubEntityId(), loCatFont, loLabelFont, loHeaderFont,
						liTotalIndirectCosts, loBudgetSummaryMap, loBudgetSummaryPara, loCurrencyFormatter,
						liMaxTableCount, loHashMap, loPdfTable, loFieldDocumentMapping, loDocument);
				loDocument.add(loBudgetSummaryPara);
				loDocument.close();
				loFileOutputStream.close();
			}
			catch (ApplicationException aoAppExp)
			{
				LOG_OBJECT.Error("Exception occured while fetching contract budget amendment budget summary", aoAppExp);
				setMoState("Exception occured while fetching contract budget amendment budget summary");
				throw aoAppExp;
			}
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Exception occured while fetching contract budget amendment budget summary", aoExp);
				setMoState("Exception occured while fetching contract budget amendment budget summary");
				throw new ApplicationException(
						"Exception occured while fetching contract budget amendment budget summary", aoExp);
			}
			loPathList.add(loSBfRealPath.toString());

		}
		return loPathList;
	}

	/**
	 * This method is used to get the Contract Title, Epin and Fisical Year
	 * details for Header Section in Amendment budgetSummary detail PDF.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of loPdfTable2, loFieldDocumentMapping ,and
	 * aoCBGridBeanAmendmentFiscialEpin .
	 * 
	 * @param aoPdfTable2 PdfPTable- Object
	 * @param aoFieldDocumentMapping org.jdom.Document - Object
	 * @param aoCBGridBeanAmendmentFiscialEpin ContractList - Object
	 * @param aoDocument Document - Object
	 * @throws ApplicationException ApplicationException - Object
	 */

	@SuppressWarnings("rawtypes")
	private void getHeaderContent(PdfPTable aoPdfTable2, org.jdom.Document aoFieldDocumentMapping,
			ContractList aoCBGridBeanAmendmentFiscialEpin, Document aoDocument) throws ApplicationException
	{
		String lsSubFieldHeadingXpath;
		String lsColumnVal = HHSConstants.EMPTY_STRING;
		String lsKeyValue = null;
		org.jdom.Element loFieldElement2 = null;
		List<org.jdom.Element> loHeaderFieldElements;
		lsSubFieldHeadingXpath = HHSConstants.SUB_FIELD_HEADING_X_PATH;
		Boolean loIsCurrencyType;
		try
		{
			loHeaderFieldElements = XMLUtil.getElementList(lsSubFieldHeadingXpath, aoFieldDocumentMapping);
			String asParagraphHeaderText = HHSConstants.CONTRACT_INFORMATION;
			Font loCatFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
			Paragraph loContactInfo = new Paragraph();
			Paragraph loContactInfoPara = new Paragraph(asParagraphHeaderText, loCatFont);
			loContactInfoPara.setAlignment(Paragraph.ALIGN_CENTER);
			loContactInfo.add(loContactInfoPara);
			aoDocument.add(loContactInfo);
			loContactInfo.setSpacingAfter(10);
			for (Iterator loElementItr1 = loHeaderFieldElements.iterator(); loElementItr1.hasNext();)
			{
				loFieldElement2 = (org.jdom.Element) loElementItr1.next();
				loIsCurrencyType = Boolean.valueOf(loFieldElement2.getAttributeValue(HHSConstants.IS_CURRENCY));
				String lsFieldKey = loFieldElement2.getAttributeValue(HHSConstants.FEILD_VALUE);
				StringBuilder loBuildTemp = new StringBuilder();
				if (!lsFieldKey.equalsIgnoreCase(HHSConstants.CONTRACT_START_DATE)
						&& !lsFieldKey.equalsIgnoreCase(HHSConstants.CONTRACT_END_DATE))
				{
					lsColumnVal = (String) new PropertyDescriptor(lsFieldKey,
							aoCBGridBeanAmendmentFiscialEpin.getClass()).getReadMethod().invoke(
							aoCBGridBeanAmendmentFiscialEpin);
				}
				else
				{

					if (lsFieldKey.equalsIgnoreCase(HHSConstants.CONTRACT_START_DATE))
					{
						lsColumnVal = DateUtil.getDateMMDDYYYYFormat(aoCBGridBeanAmendmentFiscialEpin
								.getContractStartDate());

						loBuildTemp.append(new PropertyDescriptor(lsFieldKey, aoCBGridBeanAmendmentFiscialEpin
								.getClass()).getReadMethod().invoke(aoCBGridBeanAmendmentFiscialEpin));
					}
					if (lsFieldKey.equalsIgnoreCase(HHSConstants.CONTRACT_END_DATE))
					{
						lsColumnVal = DateUtil.getDateMMDDYYYYFormat(aoCBGridBeanAmendmentFiscialEpin
								.getContractEndDate());

						loBuildTemp.append(new PropertyDescriptor(lsFieldKey, aoCBGridBeanAmendmentFiscialEpin
								.getClass()).getReadMethod().invoke(aoCBGridBeanAmendmentFiscialEpin));

					}

				}

				if (null != lsColumnVal)
				{
					lsKeyValue = lsColumnVal;
				}
				else
				{
					lsKeyValue = HHSConstants.EMPTY_STRING;
				}
				setTableContent1(loFieldElement2.getAttributeValue(HHSConstants.LABEL), lsKeyValue, aoPdfTable2,
						aoDocument, loIsCurrencyType);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching Contract Information for  budgetSummary detail", aoExp);
			throw new ApplicationException(
					"Exception occured while fetching Contract Information for  budgetSummary detail", aoExp);
		}

	}

	/**
	 * This Method is used to show the Header Information of current Fiscial
	 * year details on the basis of Budget Details.
	 * @param aoPdfTable3 - PdfPTable object
	 * @param aoFieldDocumentMapping - org.jdom.Document Object
	 * @param aoBudgetDetails - BudgetDetails Object
	 * @param aoDocument - Document Object
	 * @throws ApplicationException - ApplicationException - Object
	 */
	private void getFiscalYearBudgetInformationHeaderContent(PdfPTable aoPdfTable3,
			org.jdom.Document aoFieldDocumentMapping, BudgetDetails aoBudgetDetails, Document aoDocument)
			throws ApplicationException
	{
		Paragraph loBudgetSummaryPara = new Paragraph();
		Font loLabelFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);
		NumberFormat loCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
		String lsCellOneText = null;
		String lsCellTwoText = null;
		String lsCellthreeText = null;
		String lsCellfourText = null;
		String lsCellFiveText = null;
		String lsSixfourText = null;
		try
		{
			String asParagraphHeaderText = HHSConstants.FISCAL_YEAR_BUDGET_INFORMATION;
			Paragraph loContactInfoPara = new Paragraph(asParagraphHeaderText, loLabelFont);
			loContactInfoPara.setAlignment(Paragraph.ALIGN_CENTER);
			loContactInfoPara.setSpacingAfter(10);
			aoDocument.add(loContactInfoPara);
			setFiscalYearHeaderInBudgetSummaryTable(HHSConstants.START_DATE, HHSConstants.END_DATE,
					HHSConstants.FY_BUDGET, HHSConstants.YTD_INVOIVE_AMOUNT, HHSConstants.REMAINING_AMOUNT,
					HHSConstants.YTD_ACTUAL_PAID_AMOUNT, aoPdfTable3, loLabelFont);
			loBudgetSummaryPara.add(aoPdfTable3);
			loContactInfoPara.setSpacingAfter(10);
			aoDocument.add(loBudgetSummaryPara);
			PdfPTable loPdfTable4 = new PdfPTable(6);
			loPdfTable4.setWidths(new int[]
			{ 10, 10, 10, 10, 10, 10 });

			setFiscalYearInformation(lsCellOneText, lsCellTwoText, lsCellthreeText, lsCellfourText, lsCellFiveText,
					lsSixfourText, loPdfTable4, loLabelFont, aoBudgetDetails, aoDocument, loCurrencyFormatter);
			Paragraph aoFinancialsSummaryPara = new Paragraph();
			aoFinancialsSummaryPara.add(loPdfTable4);
			aoFinancialsSummaryPara.setSpacingAfter(10);
			aoDocument.add(aoFinancialsSummaryPara);

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching Contract Information for  budgetSummary detail", aoExp);
			throw new ApplicationException(
					"Exception occured while fetching Contract Information for  budgetSummary detail", aoExp);
		}

	}

	/**
	 * This Method is used to show the current Fiscial year details on the basis
	 * of Budget Details.
	 * @param asCellOneText - String Object
	 * @param asCellTwoText - String Object
	 * @param asCellthreeText - String Object
	 * @param asCellfourText - String Object
	 * @param asCellFiveText - String Object
	 * @param asSixfourText - String Object
	 * @param aoPdfTable4 - PdfPTable Object
	 * @param aoLabelFont - Font Object
	 * @param aoBudgetDetails - BudgetDetails Object
	 * @param aoDocument - Document Object
	 * @param aoCurrencyFormatter - NumberFormat Object
	 * @throws ApplicationException - ApplicationException - Object
	 */
	private static void setFiscalYearInformation(String asCellOneText, String asCellTwoText, String asCellthreeText,
			String asCellfourText, String asCellFiveText, String asSixfourText, PdfPTable aoPdfTable4,
			Font aoLabelFont, BudgetDetails aoBudgetDetails, Document aoDocument, NumberFormat aoCurrencyFormatter)
			throws ApplicationException
	{
		Paragraph loCellOneParaGraph = new Paragraph();
		Paragraph loCellTwoParaGraph = new Paragraph();
		Paragraph loCellThreeParaGraph = new Paragraph();
		Paragraph loCellFourParaGraph = new Paragraph();
		Paragraph loCellFiveParaGraph = new Paragraph();
		Paragraph loCellSixParaGraph = new Paragraph();
		PdfPCell loCell;
		try
		{
			String lsBudgetStartDate;
			lsBudgetStartDate = DateUtil.getDateMMddYYYYFormat(aoBudgetDetails.getStartDate());
			loCellOneParaGraph.setFont(aoLabelFont);
			loCellOneParaGraph.add(lsBudgetStartDate);
			loCellOneParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
			loCell = new PdfPCell(loCellOneParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			aoPdfTable4.addCell(loCell);

			String loBudgetEndDate;
			loBudgetEndDate = DateUtil.getDateMMDDYYYYFormat(aoBudgetDetails.getEndDate());
			loCellTwoParaGraph.setFont(aoLabelFont);
			loCellTwoParaGraph.add(loBudgetEndDate);
			loCellTwoParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
			loCell = new PdfPCell(loCellTwoParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			aoPdfTable4.addCell(loCell);

			loCellThreeParaGraph.setFont(aoLabelFont);
			String lsAppBud = aoBudgetDetails.getApprovedBudget().toString();
			lsAppBud = getFiscalAmount(aoCurrencyFormatter, aoBudgetDetails.getApprovedBudget());
			loCellThreeParaGraph.add(lsAppBud);
			loCellThreeParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
			loCell = new PdfPCell(loCellThreeParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			aoPdfTable4.addCell(loCell);

			lsAppBud = aoBudgetDetails.getYtdInvoicedAmount().toString();
			lsAppBud = getFiscalAmount(aoCurrencyFormatter, aoBudgetDetails.getYtdInvoicedAmount());
			loCellFourParaGraph.setFont(aoLabelFont);
			loCellFourParaGraph.add(lsAppBud);
			loCellFourParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
			loCell = new PdfPCell(loCellFourParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			aoPdfTable4.addCell(loCell);

			lsAppBud = aoBudgetDetails.getRemainingAmount().toString();
			lsAppBud = getFiscalAmount(aoCurrencyFormatter, aoBudgetDetails.getRemainingAmount());
			aoBudgetDetails.getRemainingAmount();
			loCellFiveParaGraph.setFont(aoLabelFont);
			loCellFiveParaGraph.add(lsAppBud);
			loCellFiveParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
			loCell = new PdfPCell(loCellFiveParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			aoPdfTable4.addCell(loCell);

			lsAppBud = aoBudgetDetails.getYtdActualPaid().toString();
			lsAppBud = getFiscalAmount(aoCurrencyFormatter, aoBudgetDetails.getYtdActualPaid());
			aoBudgetDetails.getYtdActualPaid();
			loCellSixParaGraph.setFont(aoLabelFont);
			loCellSixParaGraph.add(lsAppBud);
			loCellSixParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
			loCell = new PdfPCell(loCellSixParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			aoPdfTable4.addCell(loCell);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching Header Budget Sumary Details  ", aoExp);
			throw new ApplicationException("error occured while setting table content", aoExp);
		}
	}

	/**
	 * This method is used to formate the Bigdecimal Approved budget, Bigdecimal
	 * YtdInvoicedAmount Amount, Bigdecimal RemainingAmount, Bigdecimal
	 * YtdActualPaid into string.
	 * @param aoCurrencyFormatter - NumberFormat Object
	 * @param loAppBud - BigDecimal Object
	 * @return - String Object
	 */
	private static String getFiscalAmount(NumberFormat aoCurrencyFormatter, BigDecimal loAppBud)
	{
		String lsAppBud;
		if ((loAppBud).compareTo(new BigDecimal(0)) < 0)
		{
			lsAppBud = HHSConstants.LEFT_BRACKET + aoCurrencyFormatter.format((loAppBud).multiply(new BigDecimal(-1)))
					+ HHSConstants.RIGHT_BRACKET;
		}
		else
		{
			lsAppBud = aoCurrencyFormatter.format(loAppBud);
		}
		return lsAppBud;
	}

	/**
	 * This method is used to set the header of Fiscical year information.
	 * @param asCellOneText
	 * @param asCellTwoText
	 * @param asCellthreeText
	 * @param asCellfourText
	 * @param asCellFiveText
	 * @param asSixfourText
	 * @param aoPdfTable
	 * @param aoLabelFont
	 * @throws ApplicationException
	 */
	private static void setFiscalYearHeaderInBudgetSummaryTable(String asCellOneText, String asCellTwoText,
			String asCellthreeText, String asCellfourText, String asCellFiveText, String asSixfourText,
			PdfPTable aoPdfTable, Font aoLabelFont) throws ApplicationException
	{
		Paragraph loCellOneParaGraph = new Paragraph();
		Paragraph loCellTwoParaGraph = new Paragraph();
		Paragraph loCellThreeParaGraph = new Paragraph();
		Paragraph loCellFourParaGraph = new Paragraph();
		Paragraph loCellFiveParaGraph = new Paragraph();
		Paragraph loCellSixParaGraph = new Paragraph();
		PdfPCell loCell;
		try
		{
			loCellOneParaGraph.setFont(aoLabelFont);
			loCellOneParaGraph.add(asCellOneText);
			loCellOneParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
			loCell = new PdfPCell(loCellOneParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);

			loCellTwoParaGraph.setFont(aoLabelFont);
			loCellTwoParaGraph.add(asCellTwoText);
			loCell = new PdfPCell(loCellTwoParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);
			loCellThreeParaGraph.setFont(aoLabelFont);
			loCellThreeParaGraph.add(asCellthreeText);
			loCell = new PdfPCell(loCellThreeParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);
			loCellFourParaGraph.setFont(aoLabelFont);
			loCellFourParaGraph.add(asCellfourText);
			loCell = new PdfPCell(loCellFourParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);
			loCellFiveParaGraph.setFont(aoLabelFont);
			loCellFiveParaGraph.add(asCellFiveText);
			loCell = new PdfPCell(loCellFiveParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);
			loCellSixParaGraph.setFont(aoLabelFont);
			loCellSixParaGraph.add(asSixfourText);
			loCell = new PdfPCell(loCellSixParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching Header Budget Sumary Details  ", aoExp);
			throw new ApplicationException("error occured while setting table content", aoExp);
		}
	}

	/**
	 * This method is used to print the table on basis of these inputs
	 * <ul>
	 * <li>asBudgetId, aoCatFont, aoLabelFont, aoHeaderFont,
	 * ioTotalIndirectCosts, aoBudgetSummaryMap, aoBudgetSummaryPara,
	 * aoCurrencyFormatter, ioMaxTableCount, aoHashMap, aoPdfTable,
	 * aoFieldDocumentMapping</li>
	 * </ul>
	 * for Contract budget summary amendmentPDF and Contract budget summaryPDF .
	 * 
	 * @param aoMyBatisSession SqlSession- object
	 * @param asBudgetId String - object
	 * @param loCatFont Font - object
	 * @param loLabelFont Font - object
	 * @param loHeaderFont Font - object
	 * @param liTotalIndirectCosts int- Object
	 * @param loBudgetSummaryMap HashMap<String, String> - Object
	 * @param loBudgetSummaryPara Paragraph - Object
	 * @param loCurrencyFormatter NumberFormat- - Object
	 * @param liMaxTableCount int- Object
	 * @param aoHashMap HashMap - Object
	 * @param loPdfTable PdfPTable - Object
	 * @param loFieldDocumentMapping org.jdom.Document - Object
	 * @throws ApplicationException ApplicationException - Object
	 */
	private void getTableContent(SqlSession aoMyBatisSession, String asBudgetId, Font aoCatFont, Font aoLabelFont,
			Font aoHeaderFont, int ioTotalIndirectCosts, HashMap<String, String> aoBudgetSummaryMap,
			Paragraph aoBudgetSummaryPara, NumberFormat aoCurrencyFormatter, int ioMaxTableCount, HashMap aoHashMap,
			PdfPTable aoPdfTable, org.jdom.Document aoFieldDocumentMapping, Document aoDocument)
			throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList;
		String lsSubFieldXpath;
		String lsIndirectRate;
		List<org.jdom.Element> loFieldElements;
		try
		{
			CreatePDFService loCreatePdfService = new CreatePDFService();
			loSubBudgetList = loCreatePdfService.getSubBudgetDetails(aoMyBatisSession, aoHashMap);

			for (Iterator loSubBudgetItr = loSubBudgetList.iterator(); loSubBudgetItr.hasNext();)

			{
				aoPdfTable.flushContent();
				CBGridBean loSubBudgetBean = (CBGridBean) loSubBudgetItr.next();
				String asParagraphHeaderText = loSubBudgetBean.getSubBudgetName();
				Font loCatFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
				Paragraph loContactInfo = new Paragraph();
				Paragraph loContactInfoPara = new Paragraph(asParagraphHeaderText, loCatFont);
				loContactInfoPara.setAlignment(Paragraph.ALIGN_CENTER);
				loContactInfo.add(loContactInfoPara);
				loContactInfo.setSpacingAfter(10);
				// aoDocument.add(loContactInfo);
				aoBudgetSummaryPara.add(loContactInfo);
				setHeaderInBudgetSummaryTable(HHSConstants.LINE_ITEM, HHSConstants.FY_BUDGET,
						HHSConstants.YTD_INVOIVE_AMOUNT, HHSConstants.REMAINING_AMOUNT, aoPdfTable, aoLabelFont);

				aoBudgetSummaryPara.add(aoPdfTable);

				ContractBudgetService loContractBudgetService = new ContractBudgetService();
				ContractBudgetSummary loContractBudgetSummary = null;

				CBGridBean loCBGridBean = loContractBudgetService.getCbGridDataForSession(aoMyBatisSession,
						aoBudgetSummaryMap);
				// Check if status check is required.
				loCBGridBean.setBudgetStatusId(HHSConstants.CBL_86);
				loCBGridBean.setSubBudgetID(loSubBudgetBean.getSubBudgetID());
				loContractBudgetSummary = loContractBudgetService.fetchBudgetSummary(aoMyBatisSession, loCBGridBean);
				lsIndirectRate = loContractBudgetService.updateIndirectRatePercentage(aoMyBatisSession, loCBGridBean,
						null);

				for (int liTabCount = 1; liTabCount <= ioMaxTableCount; liTabCount++)
				{
					PdfPTable loPdfTable1 = new PdfPTable(4);

					loPdfTable1.setWidths(new int[]
					{ 30, 10, 10, 10 });

					lsSubFieldXpath = HHSConstants.FIELD_X_PATH_TAB + liTabCount
							+ HHSConstants.FORWARDSLASH_RIGHT_SQUARE_BRACKET;
					loFieldElements = XMLUtil.getElementList(lsSubFieldXpath, aoFieldDocumentMapping);
					for (Iterator loElementItr = loFieldElements.iterator(); loElementItr.hasNext();)
					{
						org.jdom.Element loSubFieldElement = (org.jdom.Element) loElementItr.next();
						Object loBeanObject = null;
						String lsFirstCellLabel = loSubFieldElement.getAttributeValue(HHSConstants.LABEL);
						String lsFieldKeys = loSubFieldElement.getAttributeValue(HHSConstants.VALUE_FOR_PDF);
						String lsBeanName = loSubFieldElement.getAttributeValue(HHSConstants.BEAN_VALUE);
						if (null != lsBeanName && !lsBeanName.isEmpty())
						{
							loBeanObject = (Object) new PropertyDescriptor(lsBeanName,
									loContractBudgetSummary.getClass()).getReadMethod().invoke(loContractBudgetSummary);
						}
						if (lsFirstCellLabel.equalsIgnoreCase(HHSConstants.SPACE_INDIRECT_RATE_PERCENTGE))
						{

							lsFirstCellLabel = lsFirstCellLabel + HHSConstants.DOUBLE_SPACE + lsIndirectRate
									+ HHSConstants.PERCENT;

							setDataIntoBudgetSummaryTable(lsFirstCellLabel, HHSConstants.EMPTY_STRING,
									HHSConstants.EMPTY_STRING, HHSConstants.EMPTY_STRING, loPdfTable1, aoCatFont,
									aoLabelFont, aoHeaderFont, ioTotalIndirectCosts);
						}
						else
						{
							getFiledContentData(aoCurrencyFormatter, loPdfTable1, loBeanObject, lsFirstCellLabel,
									lsFieldKeys, aoCatFont, aoLabelFont, aoHeaderFont, ioTotalIndirectCosts);
						}

					}
					aoBudgetSummaryPara.add(loPdfTable1);
					loPdfTable1.setSpacingAfter(2);
					// Check for set space after 11th coloumn
					if (liTabCount == 11)
					{
						loPdfTable1.setSpacingAfter(5);
					}
					// Check for set space after Table.
					if (liTabCount == 12)
					{
						loPdfTable1.setSpacingAfter(20);
					}
				}
				ioTotalIndirectCosts = 0;

			}

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while setting Table content ", aoExp);
			throw new ApplicationException("error occured while setting table content", aoExp);
		}
	}

	/**
	 * This method is used to set the Data of Table on basis of
	 * asTDLabel,asTDValue,aoPdfTable. for Header Section in Amendment
	 * budgetSummary detail PDF.
	 * 
	 * @param asTDLabel String Object
	 * @param asTDValue String Object
	 * @param aoPdfTable PdfPTable Object
	 * @throws ApplicationException ApplicationException - Object
	 */

	private static void setTableContent1(String asTDLabel, String asTDValue, PdfPTable aoPdfTable, Document aoDocument,
			Boolean aoIsCurrencyType) throws ApplicationException
	{
		try
		{
			Font loLabelFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);
			Font loValueFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.NORMAL);
			Paragraph loCellOneParaGraph = new Paragraph();
			Paragraph loCellTwoParaGraph = new Paragraph();
			PdfPCell loCell;

			NumberFormat loCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

			if (!asTDLabel.isEmpty() && asTDLabel.equalsIgnoreCase(HHSConstants.CONTRACT_AMOUNT_PDF))

			{

				if (aoIsCurrencyType || (null != asTDValue && !asTDValue.isEmpty()))
				{
					if ((new BigDecimal(asTDValue)).compareTo(new BigDecimal(0)) < 0)
					{
						asTDValue = HHSConstants.LEFT_BRACKET
								+ loCurrencyFormatter.format((new BigDecimal(asTDValue)).multiply(new BigDecimal(-1)))
								+ HHSConstants.RIGHT_BRACKET;
					}
					else
					{
						asTDValue = loCurrencyFormatter.format((new BigDecimal(asTDValue)));
					}
				}
			}
			loCellOneParaGraph.setFont(loLabelFont);
			loCellOneParaGraph.add(asTDLabel);
			loCellOneParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
			loCell = new PdfPCell(loCellOneParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			loCell.setBorder(0);

			aoPdfTable.addCell(loCell);
			loCellTwoParaGraph.setFont(loValueFont);
			loCellTwoParaGraph.add(asTDValue);
			loCell = new PdfPCell(loCellTwoParaGraph);
			loCell.setBorder(0);
			aoPdfTable.addCell(loCell);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while Table content For Contract Information ", aoExp);
			throw new ApplicationException("error occured while setting table content For Contract Information ", aoExp);
		}
	}

	/**
	 * This method is used to set the data of Table for Contract Budget and
	 * Amendment detail pdf.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asCellOneText, asCellTwoText ,asCellthreeText,
	 * asCellfourText, aoPdfTable aoCatFont, aoLabelFont, aoHeaderFont,
	 * liTotalIndirectCosts.
	 * 
	 * @param asCellOneText String Object
	 * @param asCellTwoText String Object
	 * @param asCellthreeText String Object
	 * @param asCellfourText String Object
	 * @param aoPdfTable PdfPTable Object
	 * @param aoCatFont Font Object
	 * @param aoLabelFont Font Object
	 * @param aoHeaderFont Font Object
	 * @param aiTotalIndirectCosts int Object
	 * @throws ApplicationException ApplicationException - Object
	 */

	private static void setDataIntoBudgetSummaryTable(String asCellOneText, String asCellTwoText,
			String asCellthreeText, String asCellfourText, PdfPTable aoPdfTable, Font aoCatFont, Font aoLabelFont,
			Font aoHeaderFont, int liTotalIndirectCosts) throws ApplicationException
	{
		Paragraph loCellOneParaGraph = new Paragraph();
		Paragraph loCellTwoParaGraph = new Paragraph();
		Paragraph loCellThreeParaGraph = new Paragraph();
		Paragraph loCellFourParaGraph = new Paragraph();

		PdfPCell loCell;

		try
		{
			loCell = new PdfPCell(loCellOneParaGraph);
			if (asCellOneText.isEmpty() || asCellOneText.equalsIgnoreCase(HHSConstants.SPACE_TOTAL_DIRECT_COST)
					|| asCellOneText.toLowerCase().contains((HHSConstants.SPACE_INDIRECT)))

			{
				loCellOneParaGraph.setFont(aoCatFont);
				loCell.setBorder(0);

			}
			else
			{
				loCellOneParaGraph.setFont(aoLabelFont);
			}

			if (asCellOneText.isEmpty() || asCellOneText.equalsIgnoreCase(HHSConstants.SPACE_TOTAL_INDIRECT_COSTS)
					&& (liTotalIndirectCosts == 0))
			{
				loCellOneParaGraph.setFont(aoCatFont);
				loCell.setBorder(0);

			}

			if (asCellOneText.isEmpty() || asCellOneText.equalsIgnoreCase(HHSConstants.SPACE_TOTAL_SALARY_AND_FRINGE)
					|| asCellOneText.equalsIgnoreCase(HHSConstants.SPACE_TOTAL_OTPS)
					|| asCellOneText.equalsIgnoreCase(HHSConstants.SPACE_TOTAL_RATE_BASED)
					|| asCellOneText.equalsIgnoreCase(HHSConstants.SPACE_TOTAL_MILESTONE_BASED)
					|| asCellOneText.equalsIgnoreCase(HHSConstants.SPACE_TOTAL_UNALLOCATED_FUNDS)
					|| asCellOneText.equalsIgnoreCase(HHSConstants.TOTAL_CITY_FUNDED_BUDGET)
					|| asCellOneText.equalsIgnoreCase(HHSConstants.TOTAL_PROGRAME_INCOME)
					|| asCellOneText.equalsIgnoreCase(HHSConstants.TOTAL_PROGRAME_BUDGET))

			{

				loCellOneParaGraph.setFont(aoHeaderFont);

			}

			loCellOneParaGraph.add(asCellOneText);
			loCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			aoPdfTable.addCell(loCell);
			loCellTwoParaGraph.setFont(aoLabelFont);
			loCellTwoParaGraph.add(asCellTwoText);
			loCell = new PdfPCell(loCellTwoParaGraph);
			if (asCellTwoText.isEmpty())
			{
				loCell.setBorder(0);
			}

			loCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			aoPdfTable.addCell(loCell);
			aoPdfTable.setSpacingAfter(100);
			loCellThreeParaGraph.setFont(aoLabelFont);
			loCellThreeParaGraph.add(asCellthreeText);

			loCell = new PdfPCell(loCellThreeParaGraph);
			if (asCellthreeText.isEmpty())
			{
				loCell.setBorder(0);
			}
			loCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			aoPdfTable.addCell(loCell);
			loCellFourParaGraph.setFont(aoLabelFont);
			loCellFourParaGraph.add(asCellfourText);
			loCell = new PdfPCell(loCellFourParaGraph);
			if (asCellfourText.isEmpty())
			{
				loCell.setBorder(0);
			}

			loCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			aoPdfTable.addCell(loCell);

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching Budget Sumary Details ", aoExp);
			throw new ApplicationException("error occured while setting table content", aoExp);
		}
	}

	/**
	 * This method is used to set the Header of Contract Budget Summary PDF. It
	 * takes below input parameter.
	 * <ul>
	 * <li>asCellOneText, asCellTwoText, asCellthreeText, asCellfourText,
	 * aoPdfTable, loLabelFont</li>
	 * </ul>
	 * 
	 * @param asCellOneText String Object
	 * @param asCellTwoText String Object
	 * @param asCellthreeText String Object
	 * @param asCellfourText String Object
	 * @param aoPdfTable PdfPTable Object
	 * @param aoLabelFont Font Object
	 * @throws ApplicationException ApplicationException - Object
	 */

	private static void setHeaderInBudgetSummaryTable(String asCellOneText, String asCellTwoText,
			String asCellthreeText, String asCellfourText, PdfPTable aoPdfTable, Font loLabelFont)
			throws ApplicationException
	{
		Paragraph loCellOneParaGraph = new Paragraph();
		Paragraph loCellTwoParaGraph = new Paragraph();
		Paragraph loCellThreeParaGraph = new Paragraph();
		Paragraph loCellFourParaGraph = new Paragraph();
		PdfPCell loCell;
		try
		{
			loCellOneParaGraph.setFont(loLabelFont);
			loCellOneParaGraph.add(asCellOneText);
			loCellOneParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
			loCell = new PdfPCell(loCellOneParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);
			loCellTwoParaGraph.setFont(loLabelFont);
			loCellTwoParaGraph.add(asCellTwoText);
			loCell = new PdfPCell(loCellTwoParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);
			loCellThreeParaGraph.setFont(loLabelFont);
			loCellThreeParaGraph.add(asCellthreeText);
			loCell = new PdfPCell(loCellThreeParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);
			loCellFourParaGraph.setFont(loLabelFont);
			loCellFourParaGraph.add(asCellfourText);
			loCell = new PdfPCell(loCellFourParaGraph);
			loCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			loCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			aoPdfTable.addCell(loCell);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching Header Budget Sumary Details  ", aoExp);
			throw new ApplicationException("error occured while setting table content", aoExp);
		}
	}

	/**
	 * This method fetch the table content for Contract Budget Summary and
	 * Contract Budget Amendment Summary Pdf. It takes below input parameter.
	 * <ul>
	 * <li>aoCurrencyFormatter, aoPdfTable1, aoBeanObject, asFirstCellLabel,
	 * asFieldKeys, aoCatFont, aoLabelFont, aoHeaderFont, aiTotalIndirectCosts</li>
	 * </ul>
	 * 
	 * @param aoCurrencyFormatter - NumberFormat Object
	 * @param aoPdfTable1 - PdfPTable Object
	 * @param aoBeanObject - Object Object
	 * @param asFirstCellLabel - String Object
	 * @param asFieldKeys - String Object
	 * @param aoCatFont - Font Object
	 * @param aoLabelFont - Font Object
	 * @param aoHeaderFont - Font Object
	 * @param aiTotalIndirectCosts - int Object
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	private static void getFiledContentData(NumberFormat aoCurrencyFormatter, PdfPTable aoPdfTable1,
			Object aoBeanObject, String asFirstCellLabel, String asFieldKeys, Font aoCatFont, Font aoLabelFont,
			Font aoHeaderFont, int aiTotalIndirectCosts) throws ApplicationException
	{
		String lsFyBudget = null;
		String lsRemaingValue = null;
		String lsYTDValue = null;
		String lsFyBudgetValueLabel = HHSConstants.EMPTY_STRING;
		String lsYTDValueValueLabel = HHSConstants.EMPTY_STRING;
		String lsRemaingValueLabel = HHSConstants.EMPTY_STRING;
		try
		{

			if (null != asFieldKeys && !asFieldKeys.isEmpty() && asFieldKeys.contains(HHSConstants.COMMA))
			{
				String[] loMapKeyArray = asFieldKeys.split(HHSConstants.COMMA);
				lsFyBudget = String.valueOf(new PropertyDescriptor(loMapKeyArray[0], aoBeanObject.getClass())
						.getReadMethod().invoke(aoBeanObject));

				lsFyBudgetValueLabel = aoCurrencyFormatter.format(new BigDecimal(lsFyBudget));

				lsYTDValue = String.valueOf(new PropertyDescriptor(loMapKeyArray[1], aoBeanObject.getClass())
						.getReadMethod().invoke(aoBeanObject));

				lsYTDValueValueLabel = aoCurrencyFormatter.format(new BigDecimal(lsYTDValue));

				lsRemaingValue = String.valueOf(new PropertyDescriptor(loMapKeyArray[2], aoBeanObject.getClass())
						.getReadMethod().invoke(aoBeanObject));

				aoCurrencyFormatter.format(new BigDecimal(lsRemaingValue));

				lsRemaingValue = String.valueOf(new PropertyDescriptor(loMapKeyArray[2], aoBeanObject.getClass())
						.getReadMethod().invoke(aoBeanObject));

				lsRemaingValueLabel = aoCurrencyFormatter.format(new BigDecimal(lsRemaingValue));
				setDataIntoBudgetSummaryTable(asFirstCellLabel, lsFyBudgetValueLabel, lsYTDValueValueLabel,
						lsRemaingValueLabel, aoPdfTable1, aoCatFont, aoLabelFont, aoHeaderFont, aiTotalIndirectCosts);

			}
			else
			{
				setDataIntoBudgetSummaryTable(asFirstCellLabel, HHSConstants.EMPTY_STRING, HHSConstants.EMPTY_STRING,
						HHSConstants.EMPTY_STRING, aoPdfTable1, aoCatFont, aoLabelFont, aoHeaderFont,
						aiTotalIndirectCosts);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching Table Content  ", aoExp);
			throw new ApplicationException("error occured while fetching field content data ", aoExp);
		}
	}

	/** 
	 * This method is added in R5
	 * This method will fetch the filed content data.
	 * 
	 * This method will populate the data of table 
	 * for the File
	 * @param aoCurrencyFormatter
	 * @param aoPdfTable1
	 * @param aoBeanObject
	 * @param asFirstCellLabel
	 * @param asFieldKeys
	 * @param aoCatFont
	 * @param aoLabelFont
	 * @param aoHeaderFont
	 * @param aiTotalIndirectCosts
	 * @throws ApplicationException
	 */
	private static void getFiledContentDataBudgetAmendment(NumberFormat aoCurrencyFormatter, PdfPTable aoPdfTable1,
			Object aoBeanObject, String asFirstCellLabel, String asFieldKeys, Font aoCatFont, Font aoLabelFont,
			Font aoHeaderFont, int aiTotalIndirectCosts) throws ApplicationException
	{
		String lsFyBudget = null;
		String lsRemaingValue = null;

		String lsModificationValue = null;
		String lsFyBudgetValueLabel = HHSConstants.EMPTY_STRING;

		String lsRemaingValueLabel = HHSConstants.EMPTY_STRING;
		String lsModValueValueLabel = HHSConstants.EMPTY_STRING;
		try
		{

			if (null != asFieldKeys && !asFieldKeys.isEmpty() && asFieldKeys.contains(HHSConstants.COMMA))
			{
				String[] lsMapKeyArray = asFieldKeys.split(HHSConstants.COMMA);
				lsFyBudget = String.valueOf(new PropertyDescriptor(lsMapKeyArray[0], aoBeanObject.getClass())
						.getReadMethod().invoke(aoBeanObject));

				lsFyBudgetValueLabel = aoCurrencyFormatter.format(new BigDecimal(lsFyBudget));

				lsRemaingValue = String.valueOf(new PropertyDescriptor(lsMapKeyArray[2], aoBeanObject.getClass())
						.getReadMethod().invoke(aoBeanObject));

				lsRemaingValueLabel = aoCurrencyFormatter.format(new BigDecimal(lsRemaingValue));

				lsModificationValue = String.valueOf(new PropertyDescriptor(lsMapKeyArray[3], aoBeanObject.getClass())
						.getReadMethod().invoke(aoBeanObject));

				if (lsModificationValue.contains(HHSConstants.HYPHEN))
				{
					lsModValueValueLabel = HHSConstants.LEFT_BRACKET
							+ aoCurrencyFormatter.format(new BigDecimal(-1)
									.multiply(new BigDecimal(lsModificationValue))) + HHSConstants.RIGHT_BRACKET;
				}
				else
				{
					lsModValueValueLabel = aoCurrencyFormatter.format(new BigDecimal(lsModificationValue));

				}

				setDataIntoBudgetSummaryTable(asFirstCellLabel, lsFyBudgetValueLabel, lsRemaingValueLabel,
						lsModValueValueLabel, aoPdfTable1, aoCatFont, aoLabelFont, aoHeaderFont, aiTotalIndirectCosts);

			}
			else
			{
				setDataIntoBudgetSummaryTable(asFirstCellLabel, HHSConstants.EMPTY_STRING, HHSConstants.EMPTY_STRING,
						HHSConstants.EMPTY_STRING, aoPdfTable1, aoCatFont, aoLabelFont, aoHeaderFont,
						aiTotalIndirectCosts);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching Table Content  ", aoExp);
			throw new ApplicationException("error occured while fetching field content data ", aoExp);
		}
	}

	/**
	 * This method uploads document to FileNet and return the List of uploaded
	 * files.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of aoUserSession, aoOutputFilePathList ,asContractId,
	 * asProcurementId, AND asContractTitle the Contract Budget Summary PDF
	 * would be display.</li>
	 * </ul>
	 * 
	 * @param aoUserSession - P8UserSession Object
	 * @param aoOutputFilePathList - List<String> Object
	 * @param aoPDFBatch - String Object
	 * @param asProcurementId - String Object
	 * @return loListOfDocId - an object of List<String>
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public List<String> uploadFinancialDocumentToFilenet(P8UserSession aoUserSession,
			List<String> aoOutputFilePathList, PDFBatch aoPDFBatch, String asProcurementId) throws ApplicationException
	{
		String lsDocId = null;
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		String lsFileName = null;
		FileInputStream loFileInputStreamObj = null;
		String lsFullFolderPath = null;
		List<String> loListOfDocId = new ArrayList<String>();
		try
		{
			lsFullFolderPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					P8Constants.PREDEFINED_FOLDER_PATH_FINANCIAL_DOC)
					+ HHSConstants.FORWARD_SLASH
					+ aoPDFBatch.getEntityId();
			for (Iterator<String> loOutputFilePathItr = aoOutputFilePathList.iterator(); loOutputFilePathItr.hasNext();)
			{
				loParamMap.clear();
				String lsOuputFilePath = (String) loOutputFilePathItr.next();
				lsFileName = lsOuputFilePath.substring(lsOuputFilePath.lastIndexOf(HHSConstants.FORWARD_SLASH) + 1,
						lsOuputFilePath.length());
				loFileInputStreamObj = new FileInputStream(new File(lsOuputFilePath));
				loParamMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, P8Constants.APPLICATION_PDF);
				loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, lsFileName);
				loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, HHSConstants.SYSTEM_USER);
				loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, HHSConstants.SYSTEM_USER);
				loParamMap.put(P8Constants.PROPERTY_CE_CONTRACT_ID, aoPDFBatch.getEntityId());
				loParamMap.put(P8Constants.PROPERTY_CE_PROCUREMENT_ID, asProcurementId);
				loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TYPE, lsFileName);
				loParamMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSConstants.PDF_FILE_TYPE);
				loParamMap.put(P8Constants.PROPERTY_CE_CONTRACT_TITLE, aoPDFBatch.getSubEntityType());
				// Adding for Emergency Release 4.0.2 for PDf-creation Batch
				loParamMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
				lsDocId = new P8ContentService().financialPDFDocumentCreation(aoUserSession, loFileInputStreamObj,
						loParamMap, lsFullFolderPath);
				loFileInputStreamObj.close();
				loListOfDocId.add(lsDocId);
			}
		}
		catch (ApplicationException aoEx)
		{
			String lsMessage = aoEx.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M04");
				throw new ApplicationException(lsMessage, aoEx);
			}
			aoEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.createDVdocument()::", aoEx);
			throw aoEx;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While creating document");
			ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, "M04"), aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While creating document", aoEx);
			throw loAppex;
		}
		finally
		{
			if (null != loFileInputStreamObj)
			{
				try
				{
					loFileInputStreamObj.close();
				}
				catch (IOException aoIoExp)
				{
					throw new ApplicationException("Error Occured While Closing Input Stream::", aoIoExp);
				}
			}
		}
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Info("P8ContentService: document uploaded. method:createDVdocument. Time Taken(seconds):: "
				+ liTimediff);
		LOG_OBJECT.Info("Exiting P8ContentService.createDVdocument() ");
		return loListOfDocId;
	}

	/**
	 * This method fetches Award documents corresponding to the parameter map
	 * 
	 * <ul>
	 * <li>1. Retrieve Map containing procurement Id and user org Id from the
	 * Channel object</li>
	 * <li>2. Create context data HashMap and populate the same with the
	 * parameter map</li>
	 * <li>3. If the fetched parameter map is not null then execute query
	 * <b>fetchAwardDocuments</b> to fetch the required award Id</li>
	 * <li>4. Return the fetched list of award documents</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param loParamMap an award map object
	 * @return loAwardDocumentList - an object of type List<ExtendedDocument>
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchAwardDocuments(SqlSession aoMybatisSession, String asAwardId, String asProviderOrgId,
			String asProcurementId, String asEvaluationPoolMappingId) throws ApplicationException
	{
		List<String> loAwardDocumentList = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.AWARD_ID, asAwardId);
		loHMContextData.put(HHSConstants.PROVIDER_ORG_ID, asProviderOrgId);
		loHMContextData.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loHMContextData.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		LOG_OBJECT.Debug("Entered into fetching award documements::" + loHMContextData.toString());
		// checking if the param map contains data or not
		if (asAwardId != null)
		{
			try
			{
				loAwardDocumentList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loHMContextData,
						HHSConstants.MAPPER_CLASS_AWARDS_MAPPER, HHSConstants.FETCH_AWARD_DOCUMENTSLIST,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				setMoState("Award Documents List fetched successfully for award Id:" + asAwardId);
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				setMoState("Transaction Failed:: CreatePDFService:fetchAwardDocuments method - Error while fetching Award Documents List for award Id:"
						+ asAwardId);
				aoExp.setContextData(loHMContextData);
				LOG_OBJECT.Error("Error while fetching Award Documents List:", aoExp);
				throw aoExp;
			}
			// handling exception other than Application Exception
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Error while fetching Award Documents List for proposal Id:", aoExp);
				setMoState("Transaction Failed:: CreatePDFService:fetchAwardDetails method - Error while fetching Award Details for award Id:"
						+ asAwardId);
				throw new ApplicationException("Error occurred while fetching award documents", aoExp);
			}
		}
		return loAwardDocumentList;
	}

	/**
	 * This method fetches Document Title for Contract Budget summary details
	 * and Contract Certification of Funds Pdf It takes the aoMyBatisSession,
	 * asContractId, asProcurementId as Input parameters and return the Title of
	 * documents.
	 * 
	 * @param aoMyBatisSession - SqlSession Object
	 * @param asContractId - String Object
	 * @param asProcurementId - String Object
	 * @return lsDocumentsTitle - an object of String
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public String getDocumentTitle(SqlSession aoMyBatisSession, String asContractId, String asProcurementId,
			String asBudgetId) throws ApplicationException
	{
		StringBuffer lsDocumentsTitle = new StringBuffer(HHSConstants.STR);
		if (null != asBudgetId && !asBudgetId.isEmpty())
		{
			lsDocumentsTitle.append(HHSConstants.CONTRACT_BUDGET_SUMMARY_DETAILS).append(HHSConstants.UNDERSCORE)
					.append(asBudgetId).append(HHSConstants.STR).append(HHSConstants.COMMA).append(HHSConstants.STR);
		}
		if (null != asContractId && !asContractId.isEmpty())
		{
			lsDocumentsTitle.append(HHSConstants.CONTRACT_CERTIFICATION_FUNDS).append(HHSConstants.UNDERSCORE)
					.append(asContractId).append(HHSConstants.STR);
		}
		return lsDocumentsTitle.toString();
	}

	/**
	 * Changed method - Build 3.1.0, Enhancement id: 6025 This method fetches
	 * All documents List of Award page. It takes loAwardDocumentList,
	 * loListOfDocId as input parameter and return List of documents.
	 * 
	 * @param loAwardDocumentList - List Object
	 * @param loListOfDocId - List Object
	 * @return loAllDocumentList - an object of type List<String>
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("null")
	public List<String> fetchAllDocumentsIds(List<String> loAwardDocumentList, List<String> loListOfDocId,
			List<String> loListOfAgencyDocId) throws ApplicationException
	{

		List<String> loAllDocumentList = new ArrayList<String>();
		if (null != loAwardDocumentList)
		{
			loAllDocumentList.addAll(loAwardDocumentList);
		}
		if (null != loListOfDocId)
		{
			loAllDocumentList.addAll(loListOfDocId);
		}
		if (null != loListOfAgencyDocId)
		{
			loAllDocumentList.addAll(loListOfAgencyDocId);
		}
		return loAllDocumentList;
	}

	/**
	 * This method is used to get the ContractCertificationOfFundDetails or
	 * ContractCertificationFundDetails- Amendment on the basis of contractID.
	 * <ul>
	 * <li>Execute <b>addContractCertificationDataForContract</b> Method and get
	 * the Contract certification Details.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession -SqlSession Object
	 * @param asOutputFilePath -String Object
	 * @param asHeaderLabel -String Object
	 * @param aoPDFBatch -PDFBatch object
	 * @return loPathList - List<String> Object
	 * @throws ApplicationException ApplicationException object
	 */

	public List<String> addContractCertificationOfFundDetails(SqlSession aoMyBatisSession, String asOutputFilePath,
			String asHeaderLabel, PDFBatch aoPDFBatch) throws ApplicationException
	{
		List<String> loPathList = new ArrayList<String>();
		try
		{
			if (aoPDFBatch.getSubEntityType().equalsIgnoreCase(HHSConstants.CONTRACT))
			{
				addContractCertificationDataForContract(aoMyBatisSession, asOutputFilePath,
						HHSConstants.CONTRACT_CERTIFICATION_FUNDS, aoPDFBatch.getEntityId(), loPathList);

			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching contract budget amendment budget summary", aoAppExp);
			setMoState("Exception occured while fetching contract budget amendment budget summary");
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching contract budget amendment budget summary", aoExp);
			setMoState("Exception occured while fetching contract budget amendment budget summary");
			throw new ApplicationException("Exception occured while fetching contract budget amendment budget summary",
					aoExp);
		}
		return loPathList;
	}

	/**
	 * This method will generate the contract certification Data for Contract
	 * <ul>
	 * <li>It will read the configuration for contract certification of fund
	 * table</li>
	 * <li>It will return the temporary path to the service which will be later
	 * used to upload the pdf file into filenet</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Sql session to connect to Database
	 * @param asOutputFilePath string value of output file path
	 * @param asHeaderLabel header label for contract certification of fund
	 *            table
	 * @param asContractId String value of Contract ID
	 * @param aoProcCofBean procurement cof bean object
	 * @param aoPathList list of temporary file path
	 * @throws ApplicationException when any exception occurred.
	 */
	private void addContractCertificationDataForContract(SqlSession aoMyBatisSession, String asOutputFilePath,
			String asHeaderLabel, String asContractId, List<String> aoPathList) throws ApplicationException
	{
		Font loCatFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD, BaseColor.BLUE);

		String lsFieldXpath = HHSConstants.CONTRACT_COF_FIELD;
		Map<String, String> loFiscalYrMap = new HashMap<String, String>();
		Font loLabelFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);
		File loPdfFile = null;
		Document loDocument = new Document();
		StringBuffer lsBfRealPath = new StringBuffer();
		lsBfRealPath.append(asOutputFilePath).append(HHSConstants.FORWARD_SLASH)
				.append(HHSConstants.CONTRACT_CERTIFICATION_FUNDS).append(HHSConstants.UNDERSCORE).append(asContractId);
		FileOutputStream loFileOutputStream = null;

		try
		{
			loPdfFile = new File(lsBfRealPath.toString());
			if (!loPdfFile.exists())
			{
				loPdfFile.createNewFile();
			}
			loFileOutputStream = new FileOutputStream(loPdfFile);
			PdfWriter.getInstance(loDocument, loFileOutputStream);
			loDocument.open();
			org.jdom.Document loFieldDocumentMapping = XMLUtil.getDomObj(CreatePDFService.class
					.getResourceAsStream(HHSConstants.AMENDMENT_BUDGET_SCREEN_FIELD_MAPPING));
			List<org.jdom.Element> loFieldElements = (List<org.jdom.Element>) XMLUtil.getElementList(lsFieldXpath,
					loFieldDocumentMapping);
			ProcurementCOF loProcCofBean = getContractAmmendDetails(asContractId, aoMyBatisSession, false);
			// Second parameter is the number of the chapter
			Paragraph loAmmendmentPara = new Paragraph(asHeaderLabel, loCatFont);
			loAmmendmentPara.setAlignment(Element.ALIGN_LEFT);
			PdfPTable loTable = new PdfPTable(2);
			loTable.setWidths(new int[]
			{ 20, 100 });
			getElementsForContractCOFPdf(loLabelFont, loFieldElements, loProcCofBean, loTable);
			loAmmendmentPara.add(loTable);
			loDocument.add(loAmmendmentPara);
			Map<String, List> loContractAllocationMap = getContractAllocationDetails(asContractId, loFiscalYrMap,
					aoMyBatisSession, false);
			createAllocationDataTableWithoutFont(HHSConstants.ACCOUNT_ALLOCTAION_CHART, HHSConstants.CHART_ACCOUNTS,
					HHSConstants.ACC_TOTAL, loContractAllocationMap.get(HHSConstants.COA), loFiscalYrMap, loDocument);
			createAllocationDataTableWithoutFont(HHSConstants.FUNDING_SOURCE_ALLOCATION, HHSConstants.FUNDING_SOURCE,
					HHSConstants.ACC_TOTAL, loContractAllocationMap.get(HHSConstants.FUND), loFiscalYrMap, loDocument);
			loDocument.close();
			loFileOutputStream.close();
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while Displaying ContractCertification Funding Details ", aoExp);
			throw new ApplicationException("error occured while Displaying ContractCertification Funding Details",
					aoExp);

		}
		aoPathList.add(lsBfRealPath.toString());
	}

	/**
	 * This method is used to get the ContractAllocationDetails on the basis of
	 * ContractId and ContractFyMap , which needs to be shown on PDF.
	 * <ul>
	 * <li>Execute <b>fetchContractAccountAllocationFYDetails<b> transaction and
	 * gets the ContractFyDetails Map.</li> and
	 * <li>By Executing <b>fetchContractAccountAllocationDetails<b>will gets the
	 * COA and Fund List.</li>
	 * </ul>
	 * 
	 * @param asContractId String Object
	 * @param aoContractFyMap Map Object
	 * @return loAllocationMaps Map Object
	 * @throws ApplicationException -ApplicationException Object
	 * 
	 */

	private static Map<String, List> getContractAllocationDetails(String asContractId,
			Map<String, String> aoContractFyMap, SqlSession aoMyBatisSession, boolean lbIsAmendment)
			throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		Map<String, String> loFiscalYrMap = null;
		CBGridBean loCBGridBean = new CBGridBean();
		List loCOAList = null;
		List loFundingList = null;
		Map<String, List> loAllocationMaps = new HashMap<String, List>();
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, asContractId);
			FinancialsListService loFinancialsListService = new FinancialsListService();
			// code change for high defect
			List<ContractList> loContractList = loFinancialsListService.fetchBaseAmendmentContractDetails(
					aoMyBatisSession, asContractId);
			ContractList loContract = null;
			if (lbIsAmendment)
			{
				for (ContractList loContractListBean : loContractList)
				{
					if (loContractListBean.getContractId().equalsIgnoreCase(asContractId))
					{
						loContract = loContractListBean;
						break;
					}
				}
			}
			else
			{
				loContract = (ContractList) loContractList.get(0);
			}
			String lsStartDate = "", lsEndDate = "";

			if (null != loContract && null != loContract.getContractStartDate())
			{
				lsStartDate = DateUtil.getDateMMDDYYYYFormat(loContract.getContractStartDate());
			}
			if (null != loContract && null != loContract.getContractEndDate())
			{
				lsEndDate = DateUtil.getDateMMDDYYYYFormat(loContract.getContractEndDate());
			}
			BaseControllerUtil.getContractFiscalYearsUtil(lsStartDate, lsEndDate, aoContractFyMap);
			int liFiscalStartYr = Integer.valueOf(String.valueOf(aoContractFyMap.get(HHSConstants.LI_START_YEAR)));
			loCBGridBean.setFiscalYearID(String.valueOf(liFiscalStartYr));
			loCBGridBean.setContractID(asContractId);
			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			loCBGridBean.setContractID(asContractId);
			loCOAList = (List<AccountsAllocationBean>) loConfigurationService.fetchContractCOFCOA(loCBGridBean,
					aoMyBatisSession);
			loFundingList = (List<FundingAllocationBean>) loConfigurationService.fetchContractConfFundingDetails(
					aoMyBatisSession, loCBGridBean);
			loAllocationMaps.put(HHSConstants.COA, loCOAList);
			loAllocationMaps.put(HHSConstants.FUND, loFundingList);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching the contract allocation details ", aoExp);
			throw new ApplicationException("error occured while fetching the contract allocation details. ", aoExp);
		}
		return loAllocationMaps;
	}

	/**
	 * This method is used to Set the data for Account allocation Amendment and
	 * Funding source Amnedmnet on basis of asParagraphHeaderText,
	 * asLastColumnText, aoTableContentList and aoFiscalYrMap .
	 * 
	 * @param asParagraphHeaderText String Object
	 * @param asFirstColumnText String Object
	 * @param asLastColumnText String Object
	 * @param aoTableContentList List Object
	 * @param aoFiscalYrMap Map Object
	 * @throws ApplicationException -ApplicationException Object
	 * 
	 */

	private static void createAllocationDataTableWithoutFont(String asParagraphHeaderText, String asFirstColumnText,
			String asLastColumnText, List<Object> aoTableContentList, Map<String, String> aoFiscalYrMap,
			Document aoDocumentObj) throws ApplicationException
	{
		Font loCatFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD, BaseColor.BLUE);
		Font loLabelFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);

		Paragraph loChartOfAccPara = new Paragraph();
		Paragraph loChartOfAccLabelPara = new Paragraph(asParagraphHeaderText, loCatFont);
		Paragraph loChartOfAccTablePara = new Paragraph();
		BigDecimal loTotalVal = new BigDecimal(0);
		NumberFormat loCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
		try
		{
			addEmptyLine(loChartOfAccLabelPara, 1);
			int liFiscalCount = Integer.valueOf(String.valueOf(aoFiscalYrMap.get(HHSConstants.LI_FYCOUNT)));
			PdfPTable loFundAllocationTableObject = new PdfPTable(liFiscalCount + 2);
			Paragraph loParaGraphCA = new Paragraph(asFirstColumnText, loLabelFont);
			PdfPCell loCAPdfCell = new PdfPCell();
			loCAPdfCell.addElement(loParaGraphCA);
			loCAPdfCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			loFundAllocationTableObject.addCell(loCAPdfCell);
			Integer loStartFyYear = Integer
					.valueOf(String.valueOf(aoFiscalYrMap.get(HHSConstants.LI_START_FY_COUNTER)));
			for (int liCount = 0; liCount < liFiscalCount; liCount++)
			{
				Paragraph loHeaderParagraph = new Paragraph(HHSConstants.BASE_FY
						+ HHSUtil.getFiscalYearCounter(loStartFyYear), loLabelFont);
				PdfPCell loPdfCell = new PdfPCell();
				loPdfCell.addElement(loHeaderParagraph);
				loPdfCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				loFundAllocationTableObject.addCell(loPdfCell);
				loStartFyYear++;
			}
			Paragraph loTotalHeaderParagraph = new Paragraph(asLastColumnText, loLabelFont);
			PdfPCell loPdfCellTotal = new PdfPCell();
			loPdfCellTotal.addElement(loTotalHeaderParagraph);
			loPdfCellTotal.setBackgroundColor(BaseColor.LIGHT_GRAY);
			loFundAllocationTableObject.addCell(loPdfCellTotal);
			calculateOverall(aoTableContentList, loLabelFont, loTotalVal, loCurrencyFormat, liFiscalCount,
					loFundAllocationTableObject);
			for (Iterator loIterator = aoTableContentList.iterator(); loIterator.hasNext();)
			{
				setAllocationDataDetails(loLabelFont, loTotalVal, loCurrencyFormat, liFiscalCount,
						loFundAllocationTableObject, loIterator);
			}
			loChartOfAccPara.add(loChartOfAccLabelPara);
			loChartOfAccTablePara.add(loFundAllocationTableObject);
			loChartOfAccPara.add(loChartOfAccTablePara);
			aoDocumentObj.add(loChartOfAccPara);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while Creating Allocation Data Table ", aoExp);
			throw new ApplicationException("error occured while Creating Allocation Data Table", aoExp);
		}
	}

	/**
	 * This calculate overall value fot contract cof and amendment cof chart of
	 * account allocation and funding for pdf
	 * 
	 * @param aoTableContentList List of table content as input
	 * @param loLabelFont label font as input
	 * @param loTotalVal total val as input
	 * @param loCurrencyFormat currency format as input
	 * @param liFiscalCount count as input
	 * @param loFundAllocationTableObject table object as input
	 * @throws ApplicationException -ApplicationException Object
	 */
	private static void calculateOverall(List<Object> aoTableContentList, Font loLabelFont, BigDecimal loTotalVal,
			NumberFormat loCurrencyFormat, int liFiscalCount, PdfPTable loFundAllocationTableObject)
			throws ApplicationException
	{
		BigDecimal[][] liNums = new BigDecimal[aoTableContentList.size()][liFiscalCount];
		int liTableCount = 0;
		Paragraph loIDTdParagraph = new Paragraph(HHSConstants.OVERALL, loLabelFont);
		PdfPCell loIDPdfCell = new PdfPCell();
		loIDPdfCell.addElement(loIDTdParagraph);
		loFundAllocationTableObject.addCell(loIDPdfCell);
		for (Iterator loIterator = aoTableContentList.iterator(); loIterator.hasNext(); liTableCount++)
		{
			liNums = setAllocationDataDetailsOverall(loLabelFont, loTotalVal, loCurrencyFormat, liFiscalCount,
					loFundAllocationTableObject, loIterator, liNums, liTableCount);
		}
		BigDecimal loFyValTotal = new BigDecimal(0);
		for (int liCount = 0; liCount < liFiscalCount; liCount++)
		{
			BigDecimal loFyVal = new BigDecimal(0);
			for (int liCountTable = 0; liCountTable < aoTableContentList.size(); liCountTable++)
			{
				loFyVal = loFyVal.add(liNums[liCountTable][liCount]);
			}
			String lsFiscalValueLabel;
			if (loFyVal.compareTo(new BigDecimal(0)) < 0)
			{
				lsFiscalValueLabel = HHSConstants.LEFT_BRACKET
						+ loCurrencyFormat.format(loFyVal.multiply(new BigDecimal(-1))) + HHSConstants.RIGHT_BRACKET;
			}
			else
			{
				lsFiscalValueLabel = loCurrencyFormat.format(loFyVal);
			}
			loFyValTotal = loFyValTotal.add(loFyVal);
			Paragraph loTdParagraph = new Paragraph(lsFiscalValueLabel, loLabelFont);
			PdfPCell loPdfCell = new PdfPCell();
			loPdfCell.addElement(loTdParagraph);
			loFundAllocationTableObject.addCell(loPdfCell);
		}
		String lsTotalFiscalValueLabel;
		if (loFyValTotal.compareTo(new BigDecimal(0)) < 0)
		{
			lsTotalFiscalValueLabel = HHSConstants.LEFT_BRACKET
					+ loCurrencyFormat.format(loFyValTotal.multiply(new BigDecimal(-1))) + HHSConstants.RIGHT_BRACKET;
		}
		else
		{
			lsTotalFiscalValueLabel = loCurrencyFormat.format(loFyValTotal);
		}
		Paragraph loTotalTdParagraph = new Paragraph(lsTotalFiscalValueLabel, loLabelFont);
		PdfPCell loTotalPdfCell = new PdfPCell();
		loTotalPdfCell.addElement(loTotalTdParagraph);
		loFundAllocationTableObject.addCell(loTotalPdfCell);
	}
	
	/**
	 * This method will fetch the Overall Data for the data table.
	 * 
	 * @param aoLabelFont Font
	 * @param aoTotalVal BigDecimal
	 * @param aoCurrencyFormatter
	 * @param aiFiscalCount
	 * @param aoFundAllocationTable PdfPTable
	 * @param aoIterator
	 * @param liNums
	 * @param loTableCount
	 * @return
	 * @throws ApplicationException
	 */
	private static BigDecimal[][] setAllocationDataDetailsOverall(Font aoLabelFont, BigDecimal aoTotalVal,
			NumberFormat aoCurrencyFormatter, int aiFiscalCount, PdfPTable aoFundAllocationTable, Iterator aoIterator,
			BigDecimal[][] liNums, int loTableCount) throws ApplicationException
	{
		try
		{
			Object loBeanObj = aoIterator.next();
			if (loBeanObj instanceof AccountsAllocationBean)
			{
				loBeanObj = (AccountsAllocationBean) loBeanObj;
			}
			else if (loBeanObj instanceof FundingAllocationBean)
			{
				loBeanObj = (FundingAllocationBean) loBeanObj;
			}
			for (int liCount = 0; liCount < aiFiscalCount;)
			{

				String lsMethodName = HHSConstants.SMALL_FY + (++liCount);
				BigDecimal loFyVal = new BigDecimal(BeanUtils.getProperty(loBeanObj, lsMethodName));
				liNums[loTableCount][liCount - 1] = loFyVal;
			}
			return liNums;

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while setting the allocation table data ", aoExp);
			throw new ApplicationException("Error Occured while setting the allocation table data:", aoExp);
		}
	}

	/**
	 * This method will be used to set the Allocation data details into the
	 * paragraph
	 * <ul>
	 * <li>calculate the total value for the allocation</li>
	 * <li>format the negative value and set it in paragraph</li>
	 * </ul>
	 * 
	 * @param aoLabelFont Label itext font object
	 * @param aoTotalVal total value
	 * @param aoCurrencyFormatter currency formatter object
	 * @param aiFiscalCount fiscal year count
	 * @param aoFundAllocationTable fund allocation pdf table object
	 * @param aoIterator iterator for fiscal year
	 * @throws ApplicationException if any error occurred.
	 */
	private static void setAllocationDataDetails(Font aoLabelFont, BigDecimal aoTotalVal,
			NumberFormat aoCurrencyFormatter, int aiFiscalCount, PdfPTable aoFundAllocationTable, Iterator aoIterator)
			throws ApplicationException
	{
		String lsFiscalValueLabel;
		String lsTotalFiscalValueLabel;
		try
		{
			Object loBeanObj = aoIterator.next();
			Paragraph loIDTdParagraph = null;
			String lsId = BeanUtils.getProperty(loBeanObj, HHSConstants.ID);
			if (loBeanObj instanceof AccountsAllocationBean)
			{
				loBeanObj = (AccountsAllocationBean) loBeanObj;
				loIDTdParagraph = new Paragraph(lsId, aoLabelFont);
			}
			else if (loBeanObj instanceof FundingAllocationBean)
			{
				loBeanObj = (FundingAllocationBean) loBeanObj;
				lsId = lsId.substring(lsId.lastIndexOf(HHSConstants.HYPHEN) + 1, lsId.length());
				loIDTdParagraph = new Paragraph(lsId, aoLabelFont);
			}
			PdfPCell loIDPdfCell = new PdfPCell();
			loIDPdfCell.addElement(loIDTdParagraph);
			aoFundAllocationTable.addCell(loIDPdfCell);
			for (int liCount = 1; liCount <= aiFiscalCount; liCount++)
			{
				String lsMethodName = HHSConstants.SMALL_FY + liCount;
				BigDecimal loFyVal = new BigDecimal(BeanUtils.getProperty(loBeanObj, lsMethodName));
				if (loFyVal.compareTo(new BigDecimal(0)) < 0)
				{
					lsFiscalValueLabel = HHSConstants.LEFT_BRACKET
							+ aoCurrencyFormatter.format(loFyVal.multiply(new BigDecimal(-1)))
							+ HHSConstants.RIGHT_BRACKET;
				}
				else
				{
					lsFiscalValueLabel = aoCurrencyFormatter.format(loFyVal);
				}
				Paragraph loTdParagraph = new Paragraph(lsFiscalValueLabel, aoLabelFont);
				PdfPCell loPdfCell = new PdfPCell();
				loPdfCell.addElement(loTdParagraph);
				aoFundAllocationTable.addCell(loPdfCell);
				aoTotalVal = aoTotalVal.add(loFyVal);
			}

			if (aoTotalVal.compareTo(new BigDecimal(0)) < 0)
			{
				lsTotalFiscalValueLabel = HHSConstants.LEFT_BRACKET
						+ aoCurrencyFormatter.format(aoTotalVal.multiply(new BigDecimal(-1)))
						+ HHSConstants.RIGHT_BRACKET;
			}
			else
			{
				lsTotalFiscalValueLabel = aoCurrencyFormatter.format(aoTotalVal);
			}
			Paragraph loTotalTdParagraph = new Paragraph(lsTotalFiscalValueLabel, aoLabelFont);
			PdfPCell loTotalPdfCell = new PdfPCell();
			loTotalPdfCell.addElement(loTotalTdParagraph);
			aoFundAllocationTable.addCell(loTotalPdfCell);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while setting the allocation table data ", aoExp);
			throw new ApplicationException("Error Occured while setting the allocation table data:", aoExp);
		}
	}

	/**
	 * This method is used to add EmptyLine.
	 * 
	 * @param paragraph Paragraph
	 * @param number int
	 */

	private static void addEmptyLine(Paragraph aoParagraph, int number)
	{
		for (int liCount = 0; liCount < number; liCount++)
		{
			aoParagraph.add(new Paragraph(" "));
		}
	}

	/**
	 * This method returns the fiscal years based on contract start and end date
	 * <ul>
	 * <li>Get Fiscal Start Year</li>
	 * <li>Get Fiscal End Year</li>
	 * </ul>
	 * 
	 * @param asContStartDate Contract Start Date
	 * @param asContEndDate Contract End Date
	 * @param aoContMap Contract Map
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void getContractFiscalYearsUtil(String asContStartDate, String asContEndDate, Map aoContMap)
			throws ApplicationException
	{
		try
		{
			String[] loStartDateArray = asContStartDate.split(HHSConstants.FORWARD_SLASH);
			String[] loEndDateArray = asContEndDate.split(HHSConstants.FORWARD_SLASH);
			int liStartMonth = Integer.parseInt(loStartDateArray[0]);
			int liStartYear = Integer.parseInt(loStartDateArray[2]);
			int liEndMonth = Integer.parseInt(loEndDateArray[0]);
			int liEndYear = Integer.parseInt(loEndDateArray[2]);
			int liYearCount = 0;
			if (liStartMonth > HHSConstants.INT_SIX)
			{
				liStartYear = liStartYear + 1;
			}
			if (liEndMonth > HHSConstants.INT_SIX)
			{
				liEndYear = liEndYear + 1;
			}
			liYearCount = (liEndYear - liStartYear) + 1;
			int liFYCounter = (Integer.parseInt(String.valueOf(liStartYear).substring(2)));
			aoContMap.put(HHSConstants.LI_START_FY_COUNTER, liFYCounter);
			aoContMap.put(HHSConstants.LI_FYCOUNT, liYearCount);
			aoContMap.put(HHSConstants.LI_START_YEAR, liStartYear);
			aoContMap.put(HHSConstants.LI_END_YEAR, liEndYear);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured in CreatePDFService While returning the fiscal years based on contract start and end date",
					aoEx);
			LOG_OBJECT
					.Error("Error:: CreatePDFService:"
							+ "getContractFiscalYearsUtil method - "
							+ "Error Occured in CreatePDFService While returning the fiscal years based on contract start and end date",
							loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is used to Fetch contract budget amendment budget summary and
	 * converting it to pdf.
	 * <ul>
	 * <li>Calls<b>addContractBudgetAmendmentSummary</b> method for Contract
	 * Amendment budget summary detail pdf. Call this method
	 * <b>addContractCertificationOfFundDetails</b> for COF Amendment pdf.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL session as input.
	 * @param loUserSession Filenet session as input
	 * @param asOutputFilePath Path where to download the files
	 * @param loPDFBatch PDFBatch as input.
	 * @return
	 * @throws ApplicationException exception in case a query fails.
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<String> fetchDocumentsAndConvertToPDF(SqlSession aoMyBatisSession, P8UserSession loUserSession,
			String asOutputFilePath, PDFBatch loPDFBatch) throws ApplicationException
	{
		Font loCatFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD, BaseColor.BLUE);
		Font loLabelFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);
		Font loHeaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
		ArrayList<String> loPathList = new ArrayList<String>();
		int liTotalIndirectCosts = 0;
		StringBuffer asBfRealPath = new StringBuffer();
		asBfRealPath.append(asOutputFilePath);
		asBfRealPath.append(HHSConstants.FORWARD_SLASH);
		asBfRealPath.append(HHSConstants.CONTRACT_BUDGET_AMENDMENT_SUMMARY_DETAILS);
		asBfRealPath.append(HHSConstants.UNDERSCORE);
		try
		{
			if (loPDFBatch.getSubEntityType().equalsIgnoreCase(HHSConstants.BUDGET_TYPE1))
			{
				// code for contract budget amendment budget summary
				addContractBudgetAmendmentSummary(aoMyBatisSession, loPDFBatch.getEntityId(), loUserSession, loCatFont,
						loLabelFont, loHeaderFont, liTotalIndirectCosts, asBfRealPath, loPathList,
						loPDFBatch.getSubEntityId());
			}
			else if (loPDFBatch.getSubEntityType().equalsIgnoreCase(HHSConstants.CONTRACT_AMENDMENT))
			{
				// code for amendment COF
				addContractCertificationOfFundDetailsForAmendment(asOutputFilePath,
						HHSConstants.CONTRACT_CERTIFICATION_FUND_AMENDMENT, loPDFBatch.getEntityId(), aoMyBatisSession,
						loCatFont, loLabelFont, loPathList);
			}

		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error(
					"Exception occured in CreatePDFService while fetching contract budget amendment budget summary",
					aoAppExp);
			setMoState("Exception occured in CreatePDFService while fetching contract budget amendment budget summary");
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error(
					"Exception occured in CreatePDFService while fetching contract budget amendment budget summary",
					aoExp);
			setMoState("Exception occured in CreatePDFService while fetching contract budget amendment budget summary");
			throw new ApplicationException(
					"Exception occured in CreatePDFService while fetching contract budget amendment budget summary",
					aoExp);
		}
		return loPathList;
	}

	/**
	 * This method is used to fetch Contract budget Amendment summary details.
	 * It takes following as input parameter
	 * <ul>
	 * <li>aoMyBatisSession,aoContractFilterBean,
	 * loUserSession,loCatFont,loLabelFont,loHeaderFont,liTotalIndirectCosts
	 * asBfRealPath,loPathList,asBudgetId</li>
	 * This will calls<b>getElementForContractBudgetPdf</b>method for Contract
	 * budget pdf.
	 * </ul>
	 * 
	 * @param aoMyBatisSession SqlSession object
	 * @param asContractId ContractList object
	 * @param loUserSession P8UserSession object
	 * @param loCatFont Font object
	 * @param loLabelFont Font object
	 * @param loHeaderFont Font object
	 * @param liTotalIndirectCosts int object
	 * @param asBfRealPath StringBuffer object
	 * @param loPathList ArrayList<String> object
	 * @param asBudgetId String object
	 * @return loPathList ArrayList<String> object
	 * @throws ApplicationException Application Object
	 */
	private ArrayList<String> addContractBudgetAmendmentSummary(SqlSession aoMyBatisSession, String asContractId,
			P8UserSession loUserSession, Font loCatFont, Font loLabelFont, Font loHeaderFont, int liTotalIndirectCosts,
			StringBuffer asBfRealPath, ArrayList<String> loPathList, String asBudgetId) throws ApplicationException
	{
		HashMap<String, String> loBudgetSummaryMap = new HashMap<String, String>();
		ContractBudgetSummary loContractBeanSummary = null;
		List<CBGridBean> loSubBudgetList = null;
		String lsFieldXpath = HHSConstants.FIELD_X_PATH;
		String lsSubFieldHeadingXpath = HHSConstants.EMPTY_STRING;
		String lsIndirectRate = null;
		Paragraph loBudgetSummaryPara = null;
		NumberFormat loCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
		Document loDocument = new Document();
		int liMaxTableCount = 0;
		new ArrayList<org.jdom.Element>();
		List<org.jdom.Element> loHeaderFieldElements = null;
		asBfRealPath.append(asBudgetId);
		try
		{
			FileOutputStream loFileOutputStream = new FileOutputStream(asBfRealPath.toString());
			PdfWriter.getInstance(loDocument, loFileOutputStream);
			loPathList.add(asBfRealPath.toString());
			loDocument.open();
			loBudgetSummaryPara = new Paragraph(HHSConstants.SPACE, loCatFont);
			loBudgetSummaryPara.setAlignment(Element.ALIGN_LEFT);
			PdfPTable loPdfTable = new PdfPTable(4);
			PdfPTable loPdfTable2 = new PdfPTable(4);
			loPdfTable.setWidths(new int[]
			{ 30, 10, 10, 10 });
			loPdfTable2.setWidths(new int[]
			{ 10, 10, 10, 10 });
			org.jdom.Document loFieldDocumentMapping = XMLUtil.getDomObj(CreatePDFService.class
					.getResourceAsStream(HHSConstants.AMENDMENT_BUDGET_SCREEN_FIELD_MAPPING));
			org.jdom.Element loFieldElement = (org.jdom.Element) XMLUtil.getElement(lsFieldXpath,
					loFieldDocumentMapping);
			HashMap aoHashMap = new HashMap();
			aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, asBudgetId);
			aoHashMap.put(HHSConstants.BUDGET_TYPE, HHSConstants.ONE);
			//Start Emergency release 4.0.2 made changes for pdf generation batch
			aoHashMap.put(HHSConstants.USER_ID_2, HHSConstants.SYSTEM_USER);
			//End Emergency release 4.0.2 made changes for pdf generation batch
			ContractBudgetService loContractBudgetService = new ContractBudgetService();
			ContractList loCBGridBeanAmendmentFiscialEpin = loContractBudgetService.fetchContractSummary(
					aoMyBatisSession, aoHashMap);
			lsSubFieldHeadingXpath = HHSConstants.AMENDMENT_FIELD_HEADING_X_PATH;
			loHeaderFieldElements = XMLUtil.getElementList(lsSubFieldHeadingXpath, loFieldDocumentMapping);
			getElementsForPdf(loLabelFont, loHeaderFieldElements, loPdfTable2, loCBGridBeanAmendmentFiscialEpin,
					loDocument);
			loPdfTable2.setSpacingAfter(20);
			loDocument.add(loPdfTable2);
			liMaxTableCount = Integer.valueOf(loFieldElement.getAttributeValue(HHSConstants.TABLE_COUNT));
			loBudgetSummaryMap.put(HHSConstants.BUDGET_ID_WORKFLOW, asBudgetId);
			loBudgetSummaryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
			MasterBean loMasterBean = loContractBudgetAmendmentService.generateMasterBeanObjectFromXML(
					aoMyBatisSession, asBudgetId, loUserSession);
			CBGridBean loCBGridBean1 = new CBGridBean();
			loCBGridBean1.setBudgetStatusId(HHSConstants.CBL_86);
			BudgetDetails loBudgetDetails = loContractBudgetAmendmentService.fetchFyBudgetSummary(aoMyBatisSession,
					null, loMasterBean, loCBGridBean1);

			PdfPTable loPdfTable3 = new PdfPTable(6);
			loPdfTable3.setWidths(new int[]
			{ 10, 10, 10, 10, 10, 10 });
			getFiscalYearBudgetInformationHeaderContent(loPdfTable3, loFieldDocumentMapping, loBudgetDetails,
					loDocument);

			loSubBudgetList = loContractBudgetAmendmentService.fetchCMSubBudgetSummary(aoMyBatisSession,
					loBudgetSummaryMap);
			for (Iterator loSubBudgetItr = loSubBudgetList.iterator(); loSubBudgetItr.hasNext();)

			{
				loPdfTable.flushContent();

				CBGridBean loSubBudgetBean = (CBGridBean) loSubBudgetItr.next();
				String asParagraphHeaderText = loSubBudgetBean.getSubBudgetName();
				Font loCatFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
				Paragraph loContactInfo = new Paragraph();
				Paragraph loContactInfoPara = new Paragraph(asParagraphHeaderText, loCatFont1);
				loContactInfoPara.setAlignment(Paragraph.ALIGN_CENTER);
				loContactInfo.add(loContactInfoPara);
				loContactInfo.setSpacingAfter(10);
				// loDocument.add(loContactInfo);
				loBudgetSummaryPara.add(loContactInfo);
				// loContactInfo.setSpacingAfter(0);
				setHeaderInBudgetSummaryTable(HHSConstants.LINE_ITEM, HHSConstants.APPROVED_FY_BUDGET,
						HHSConstants.REMAINING_AMOUNT, HHSConstants.AMENDMENT_AMOUNT, loPdfTable, loLabelFont);
				loBudgetSummaryPara.add(loPdfTable);
				CBGridBean loCBGridBean = loContractBudgetAmendmentService.getCbGridDataForSession(aoMyBatisSession,
						loBudgetSummaryMap);
				loCBGridBean.setBudgetStatusId(HHSConstants.CBL_86);
				loCBGridBean.setSubBudgetID(loSubBudgetBean.getSubBudgetID());
				loContractBeanSummary = loContractBudgetService.fetchModificationBudgetSummary(aoMyBatisSession,
						loCBGridBean, loMasterBean);
				lsIndirectRate = loContractBudgetService.updateIndirectRatePercentage(aoMyBatisSession, loCBGridBean,
						loMasterBean);
				for (int liTabCount = 1; liTabCount <= liMaxTableCount; liTabCount++)
				{
					getElementForContractBudgetPdf(loCatFont, loLabelFont, loHeaderFont, liTotalIndirectCosts,
							loContractBeanSummary, lsIndirectRate, loBudgetSummaryPara, loCurrencyFormatter,
							loFieldDocumentMapping, liTabCount);
				}
				liTotalIndirectCosts = 0;
			}
			loDocument.add(loBudgetSummaryPara);
			loDocument.close();
			loFileOutputStream.close();
			return loPathList;
		}

		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error:: CreatePDFService:" + "addContractBudgetAmendmentSummary - "
					+ "Error Occured in CreatePDFService While fetching contract budget amendment summary details",
					aoExp);
			throw new ApplicationException("error occured While fetching contract budget amendment summary details",
					aoExp);

		}
	}

	/**
	 * This Method is used to get the contract budget details. It takes
	 * following as input parameter
	 * <ul>
	 * <li>loCatFont,loLabelFont,
	 * loHeaderFont,liTotalIndirectCosts,loContractBeanSummary
	 * ,lsIndirectRate,loBudgetSummaryPara
	 * loCurrencyFormatter,loFieldDocumentMapping,liTabCount</li>
	 * <li>This will calls<b>setDataIntoBudgetSummaryTable</b>method for
	 * Contract budgetSummary pdf.</li>
	 * <li>This will calls<b>getFiledContentData</b>method for table content
	 * data for Contract budgetSummary pdf.
	 * <li>
	 * </ul>
	 * 
	 * @param aoCatFont -Font Object
	 * @param aoLabelFont -Font Object
	 * @param aoHeaderFont -Font Object
	 * @param aiTotalIndirectCosts -int Object
	 * @param aoContractBeanSummary -ContractBudgetSummary Object
	 * @param asIndirectRate -String Object
	 * @param aoBudgetSummaryPara -Paragraph Object
	 * @param aoCurrencyFormatter -NumberFormat Object
	 * @param aoFieldDocumentMapping -org.jdom.Document Object
	 * @param aiTabCount -int Object
	 * @throws ApplicationException -ApplicationException Object
	 */
	private void getElementForContractBudgetPdf(Font aoCatFont, Font aoLabelFont, Font aoHeaderFont,
			int aiTotalIndirectCosts, ContractBudgetSummary aoContractBeanSummary, String asIndirectRate,
			Paragraph aoBudgetSummaryPara, NumberFormat aoCurrencyFormatter, org.jdom.Document aoFieldDocumentMapping,
			int aiTabCount) throws ApplicationException
	{
		String lsSubFieldXpath;
		List<org.jdom.Element> loFieldElements;
		PdfPTable loPdfTable1 = new PdfPTable(4);
		try
		{
			loPdfTable1.setWidths(new int[]
			{ 30, 10, 10, 10 });
			lsSubFieldXpath = HHSConstants.FIELD_X_PATH_TAB + aiTabCount
					+ HHSConstants.FORWARDSLASH_RIGHT_SQUARE_BRACKET;
			loFieldElements = XMLUtil.getElementList(lsSubFieldXpath, aoFieldDocumentMapping);
			for (Iterator loElementItr = loFieldElements.iterator(); loElementItr.hasNext();)
			{
				org.jdom.Element loSubFieldElement = (org.jdom.Element) loElementItr.next();
				Object loBeanObject = null;
				String lsFirstCellLabel = loSubFieldElement.getAttributeValue(HHSConstants.LABEL);
				String lsFieldKeys = loSubFieldElement.getAttributeValue(HHSConstants.VALUE_FOR_PDF);
				String lsBeanName = loSubFieldElement.getAttributeValue(HHSConstants.BEAN_VALUE);
				if (null != lsBeanName && !lsBeanName.isEmpty())
				{
					loBeanObject = (Object) new PropertyDescriptor(lsBeanName, aoContractBeanSummary.getClass())
							.getReadMethod().invoke(aoContractBeanSummary);
				}
				if (lsFirstCellLabel.equalsIgnoreCase(HHSConstants.SPACE_INDIRECT_RATE_PERCENTGE))
				{
					lsFirstCellLabel = lsFirstCellLabel + HHSConstants.DOUBLE_SPACE + asIndirectRate
							+ HHSConstants.PERCENT;
					setDataIntoBudgetSummaryTable(lsFirstCellLabel, HHSConstants.EMPTY_STRING,
							HHSConstants.EMPTY_STRING, HHSConstants.EMPTY_STRING, loPdfTable1, aoCatFont, aoLabelFont,
							aoHeaderFont, aiTotalIndirectCosts);
				}
				else
				{

					getFiledContentDataBudgetAmendment(aoCurrencyFormatter, loPdfTable1, loBeanObject,
							lsFirstCellLabel, lsFieldKeys, aoCatFont, aoLabelFont, aoHeaderFont, aiTotalIndirectCosts);
				}
			}
			aoBudgetSummaryPara.add(loPdfTable1);
			loPdfTable1.setSpacingAfter(2);
			// Check for set space after 11th coloumn
			if (aiTabCount == 11)
			{
				loPdfTable1.setSpacingAfter(5);
			}
			// Check for set space after Table.
			if (aiTabCount == 12)
			{
				loPdfTable1.setSpacingAfter(20);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error:: CreatePDFService:" + "getElementForContractBudgetPdf - "
					+ "Error Occured in CreatePDFService While fetching contract budget amendment summary details",
					aoExp);
			throw new ApplicationException("error occured while Displaying ContractBudget Summary detail Pdf", aoExp);

		}
	}

	/**
	 * This method is used to get the PDF elemnt on basis of
	 * loLabelFont,loHeaderFieldElements
	 * ,loPdfTable2,loCBGridBeanAmendmentFiscialEpin. This will display all
	 * feild values of Budget summary PDF and throw Application Exception in
	 * case of Exception
	 * 
	 * @param aoLabelFont -Font Object
	 * @param aoHeaderFieldElements -List<org.jdom.Element> Object
	 * @param aoPdfTable2 -PdfPTable Object
	 * @param aoCBGridBeanAmendmentFiscialEpin ContractList Object
	 * @throws ApplicationException -ApplicationException Object
	 */
	private void getElementsForPdf(Font aoLabelFont, List<org.jdom.Element> aoHeaderFieldElements,
			PdfPTable aoPdfTable2, ContractList aoCBGridBeanAmendmentFiscialEpin, Document aoDocument)
			throws ApplicationException
	{
		Boolean loIsCurrencyType;
		String lsColumnVal = HHSConstants.EMPTY_STRING;
		String lsKeyValue;
		try
		{
			String asParagraphHeaderText = HHSConstants.CONTRACT_INFORMATION;
			Font loCatFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
			Paragraph loContactInfo = new Paragraph();
			Paragraph loContactInfoPara = new Paragraph(asParagraphHeaderText, loCatFont);
			loContactInfoPara.setAlignment(Paragraph.ALIGN_CENTER);
			loContactInfo.add(loContactInfoPara);
			loContactInfo.setSpacingAfter(5);
			aoDocument.add(loContactInfo);

			for (Iterator loElementItr1 = aoHeaderFieldElements.iterator(); loElementItr1.hasNext();)
			{
				org.jdom.Element loFieldElement2 = (org.jdom.Element) loElementItr1.next();
				loIsCurrencyType = Boolean.valueOf(loFieldElement2.getAttributeValue(HHSConstants.IS_CURRENCY));
				String lsFieldKey = loFieldElement2.getAttributeValue(HHSConstants.FEILD_VALUE);
				StringBuilder loBuildTemp = new StringBuilder();
				if (!lsFieldKey.equalsIgnoreCase(HHSConstants.CONTRACT_START_DATE)
						&& !lsFieldKey.equalsIgnoreCase(HHSConstants.CONTRACT_END_DATE))
				{
					lsColumnVal = (String) new PropertyDescriptor(lsFieldKey,
							aoCBGridBeanAmendmentFiscialEpin.getClass()).getReadMethod().invoke(
							aoCBGridBeanAmendmentFiscialEpin);
				}
				else
				{

					if (lsFieldKey.equalsIgnoreCase(HHSConstants.CONTRACT_START_DATE))
					{
						lsColumnVal = DateUtil.getDateMMDDYYYYFormat(aoCBGridBeanAmendmentFiscialEpin
								.getContractStartDate());

						loBuildTemp.append(new PropertyDescriptor(lsFieldKey, aoCBGridBeanAmendmentFiscialEpin
								.getClass()).getReadMethod().invoke(aoCBGridBeanAmendmentFiscialEpin));
					}
					if (lsFieldKey.equalsIgnoreCase(HHSConstants.CONTRACT_END_DATE))
					{
						lsColumnVal = DateUtil.getDateMMDDYYYYFormat(aoCBGridBeanAmendmentFiscialEpin
								.getContractEndDate());

						loBuildTemp.append(new PropertyDescriptor(lsFieldKey, aoCBGridBeanAmendmentFiscialEpin
								.getClass()).getReadMethod().invoke(aoCBGridBeanAmendmentFiscialEpin));

					}

				}
				if (null != lsColumnVal)
				{
					lsKeyValue = lsColumnVal;
				}
				else
				{
					lsKeyValue = HHSConstants.EMPTY_STRING;
				}
				setTableContentWithLabel(loFieldElement2.getAttributeValue(HHSConstants.LABEL), lsKeyValue,
						aoPdfTable2, loIsCurrencyType);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error:: CreatePDFService:" + "k - "
					+ "Error Occured in CreatePDFService While fetching contract budget amendment summary details",
					aoExp);
			throw new ApplicationException("error occured while Displaying element for Pdf", aoExp);

		}
	}

	/**
	 * This method is used to set the Data of Table for Budget summary and
	 * Amendment summary Details Pdf. It takes as input
	 * asTDLabel,asTDValue,aoPdfTable,lolabelFont and set the table content.
	 * 
	 * @param asTDLabel String Object
	 * @param asTDValue String Object
	 * @param aoPdfTable PdfPTable Object
	 * @param aolabelFont label font as input
	 * @throws ApplicationException Exception in case a query fails.
	 */
	private void setTableContentWithLabel(String asTDLabel, String asTDValue, PdfPTable aoPdfTable,
			Boolean aoIsCurrencyType) throws ApplicationException
	{
		Font loLabelFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);
		Font loValueFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.NORMAL);
		Paragraph loCellOneParaGraph = new Paragraph();
		Paragraph loCellTwoParaGraph = new Paragraph();
		PdfPCell loCell;

		NumberFormat loCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

		if (!asTDLabel.isEmpty()
				&& (asTDLabel.equalsIgnoreCase(HHSConstants.CONTRACT_AMOUNT_PDF) || asTDLabel
						.equalsIgnoreCase(HHSConstants.AMENDMENT_AMOUNT_PDF)))
		{

			if (aoIsCurrencyType || (null != asTDValue && !asTDValue.isEmpty()))
			{
				if ((new BigDecimal(asTDValue)).compareTo(new BigDecimal(0)) < 0)
				{
					asTDValue = HHSConstants.LEFT_BRACKET
							+ loCurrencyFormatter.format((new BigDecimal(asTDValue)).multiply(new BigDecimal(-1)))
							+ HHSConstants.RIGHT_BRACKET;
				}
				else
				{
					asTDValue = loCurrencyFormatter.format((new BigDecimal(asTDValue)));
				}
			}
		}

		loCellOneParaGraph.setFont(loLabelFont);
		loCellOneParaGraph.add(asTDLabel);
		loCellOneParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
		loCell = new PdfPCell(loCellOneParaGraph);
		loCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		loCell.setBorder(0);
		aoPdfTable.addCell(loCell);

		loCellTwoParaGraph.setFont(loValueFont);
		loCellTwoParaGraph.add(asTDValue);
		loCell = new PdfPCell(loCellTwoParaGraph);
		loCell.setBorder(0);
		aoPdfTable.addCell(loCell);

	}

	/**
	 * Method to download contract COF for amendment.
	 * <ul>
	 * <li>This will calls<b>getElementsForContractCOFPdf</b>Method for COF Pdf.
	 * </li>
	 * </ul>
	 * 
	 * @param asOutputFilePath -String Object
	 * @param asHeaderLabel -String Object
	 * @param asContractId -String Object
	 * @param aoMyBatisSession -SqlSession Object
	 * @param aoCatFont -Font Object
	 * @param aoLabelFont -Font Object
	 * @throws ApplicationException -ApplicationException Object
	 */
	private ArrayList<String> addContractCertificationOfFundDetailsForAmendment(String asOutputFilePath,
			String asHeaderLabel, String asContractId, SqlSession aoMyBatisSession, Font aoCatFont, Font aoLabelFont,
			ArrayList<String> aoPathList) throws ApplicationException, Exception
	{
		String lsFieldXpath = HHSConstants.CONTRACT_FIELD;
		Map<String, String> loFiscalYrMap = new HashMap<String, String>();
		Document loDocument = new Document();
		StringBuffer loSBfRealPath = new StringBuffer();
		loSBfRealPath.append(asOutputFilePath);
		loSBfRealPath.append(HHSConstants.FORWARD_SLASH);
		loSBfRealPath.append(HHSConstants.CONTRACT_CERTIFICATION_FUND_AMENDMENT);
		loSBfRealPath.append(HHSConstants.UNDERSCORE);
		loSBfRealPath.append(asContractId);
		try
		{
			FileOutputStream loFileOutputStream = new FileOutputStream(loSBfRealPath.toString());
			PdfWriter.getInstance(loDocument, loFileOutputStream);
			aoPathList.add(loSBfRealPath.toString());
			loDocument.open();
			org.jdom.Document loFieldDocumentMapping = XMLUtil.getDomObj(CreatePDFService.class
					.getResourceAsStream(HHSConstants.AMMENDMENT_BUDGET_SCREENFILED_MAPPING_XML));
			java.util.List<org.jdom.Element> loFieldElements = (java.util.List<org.jdom.Element>) XMLUtil
					.getElementList(lsFieldXpath, loFieldDocumentMapping);
			ProcurementCOF loProcCofBean = getContractAmmendDetails(asContractId, aoMyBatisSession, true);
			// Second parameter is the number of the chapter
			Paragraph loAmmendmentPara = new Paragraph(asHeaderLabel, aoCatFont);
			loAmmendmentPara.setAlignment(Element.ALIGN_LEFT);
			PdfPTable loTable = new PdfPTable(2);
			loTable.setWidths(new int[]
			{ 20, 100 });
			getElementsForContractCOFPdf(aoLabelFont, loFieldElements, loProcCofBean, loTable);
			loAmmendmentPara.add(loTable);
			loDocument.add(loAmmendmentPara);
			Map<String, List> loContractAllocationMap = getContractAllocationDetails(asContractId, loFiscalYrMap,
					aoMyBatisSession, true);
			createAllocationDataTable(HHSConstants.CHART_ACCOUNT_ALLOCATION_AMENDMENT, HHSConstants.CHART_ACCOUNTS,
					HHSConstants.ACC_TOTAL, loContractAllocationMap.get(HHSConstants.COA), loFiscalYrMap, loDocument,
					aoCatFont, aoLabelFont);
			createAllocationDataTable(HHSConstants.FUNDING_SOURCE_ALLOCATION_AMENDMENT, HHSConstants.FUNDING_SOURCE,
					HHSConstants.ACC_TOTAL, loContractAllocationMap.get(HHSConstants.FUND), loFiscalYrMap, loDocument,
					aoCatFont, aoLabelFont);
			loDocument.close();
			loFileOutputStream.close();
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error:: CreatePDFService:" + "addContractCertificationOfFundDetails - "
					+ "Error Occured in CreatePDFService While fetching Contract Certification Of Fund Details", aoExp);
			setMoState("error occured in CreatePDFService while Displaying ContractCertification Funding Details");
			throw new ApplicationException(
					"error occured in CreatePDFService while Displaying ContractCertification Funding Details", aoExp);
		}
		return aoPathList;
	}

	/**
	 * Method is used to get elements for Contract COF Pdf Details. This will
	 * call setTableContent method for table data for COF pdf details. It's
	 * using loLabelFont, loFieldElements,loProcCofBean,table as input
	 * parameter.
	 * 
	 * @param aoLabelFont -Font Object
	 * @param aoFieldElements -java.util.List<org.jdom.Element> Object
	 * @param aoProcCofBean - ProcurementCOF Object
	 * @param aoTable -PdfPTable Object
	 * @throws ApplicationException -ApplicationException Object
	 */
	private void getElementsForContractCOFPdf(Font aoLabelFont, java.util.List<org.jdom.Element> aoFieldElements,
			ProcurementCOF aoProcCofBean, PdfPTable aoTable) throws ApplicationException
	{
		String lsKeyValue;
		String lsColumnVal;
		Boolean loIsCurrencyType;
		StringBuilder loTemp;
		try
		{
			for (Iterator loElementItr = aoFieldElements.iterator(); loElementItr.hasNext();)
			{
				org.jdom.Element loFieldElement = (org.jdom.Element) loElementItr.next();
				String lsFieldKey = loFieldElement.getAttributeValue(HHSConstants.FEILD_VALUE);
				loIsCurrencyType = Boolean.valueOf(loFieldElement.getAttributeValue(HHSConstants.IS_CURRENCY));
				if (lsFieldKey.contains(HHSConstants.COMMA))
				{
					lsColumnVal = HHSConstants.EMPTY_STRING;
					String[] lsMapKeyArray = lsFieldKey.split(HHSConstants.COMMA);
					for (int liCount = 0; liCount < lsMapKeyArray.length; liCount++)
					{
						loTemp = new StringBuilder();
						loTemp.append(lsColumnVal
								+ HHSConstants.EMPTY_STRING
								+ new PropertyDescriptor(lsMapKeyArray[liCount], aoProcCofBean.getClass())
										.getReadMethod().invoke(aoProcCofBean));
						lsColumnVal = loTemp.toString();

					}
				}
				else
				{
					if (lsFieldKey.equals(HHSConstants.CONTRACT_TERM_DATE))
					{
						StringBuilder aoDateBuilder = new StringBuilder(aoProcCofBean.getContractStartDate());
						aoDateBuilder.append(HHSConstants.DATE_SPACE).append(aoProcCofBean.getContractEndDate());
						String aoContractTerm = aoDateBuilder.toString();
						aoProcCofBean.setContractTermDate(aoContractTerm);
						lsColumnVal = aoProcCofBean.getContractTermDate();

					}

					if (lsFieldKey.equals(HHSConstants.AMENDMENT_TERM_DATE))
					{
						StringBuilder aoDateBuilder = new StringBuilder(aoProcCofBean.getAmendmentStartDate());
						aoDateBuilder.append(HHSConstants.DATE_SPACE).append(aoProcCofBean.getAmendmentEndDate());
						String aoContractTerm = aoDateBuilder.toString();
						aoProcCofBean.setAmendmentTermDate(aoContractTerm);
						lsColumnVal = aoProcCofBean.getAmendmentTermDate();

					}

					lsColumnVal = (String) new PropertyDescriptor(lsFieldKey, aoProcCofBean.getClass()).getReadMethod()
							.invoke(aoProcCofBean);
				}
				if (null != lsColumnVal)
				{
					lsKeyValue = lsColumnVal;
				}
				else
				{
					lsKeyValue = HHSConstants.EMPTY_STRING;
				}
				setTableContent(loFieldElement.getAttributeValue(HHSConstants.LABEL), lsKeyValue, aoTable,
						loIsCurrencyType, aoLabelFont);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error:: CreatePDFService:" + "getElementsForContractCOFPdf - "
					+ "Error Occured in CreatePDFService While fetching CgetElementsForContractCOFPdf Details", aoExp);
			setMoState("error occured in CreatePDFService while Displaying ElementsForContractCOFPdf Details");
			throw new ApplicationException(
					"error occured in CreatePDFService while Displaying ElementsForContractCOFPdf Funding Details",
					aoExp);
		}
	}

	/**
	 * Used to get Contract Ammend Details on basis of contract id. This method
	 * returns object of ProcurementCOF and throws Application Exception
	 * 
	 * @param asContractId -String Object
	 * @param aoMyBatisSession -SqlSession Object
	 * @return loProcurementBean -ProcurementCOF Object
	 * @throws ApplicationException -Application Object
	 */
	private ProcurementCOF getContractAmmendDetails(String asContractId, SqlSession aoMyBatisSession,
			boolean isAmendment) throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		String lsBaseContractId = (String) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
				HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_BASE_CONTRACT_ID,
				HHSConstants.JAVA_LANG_STRING);
		List<ContractList> loContractList = loFinancialsListService.fetchBaseAmendmentContractDetails(aoMyBatisSession,
				lsBaseContractId);
		String lsBaseAwardEpin = loFinancialsListService.fetchBaseAwardEpin(aoMyBatisSession, lsBaseContractId);
		ContractList loContract = null;

		if (isAmendment)
		{
			for (ContractList loContractListBean : loContractList)
			{
				if (loContractListBean.getContractId().equalsIgnoreCase(asContractId))
				{
					loContract = loContractListBean;

					break;
				}
			}
		}
		else
		{
			loContract = (ContractList) loContractList.get(0);
		}

		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractID(asContractId);
		ProcurementCOF loPprocureCof = loBudgetManagementService.fetchContractCofDocDetails(aoMyBatisSession,
				asContractId);
		ProcurementCOF loAmountDetails = loBudgetManagementService.fetchBaseAmendmentContractAmount(aoMyBatisSession,
				asContractId);
		BigDecimal liNewContractValue = new BigDecimal(loAmountDetails.getContractValue()).add(new BigDecimal(
				loAmountDetails.getAmendedContractValue()));
		if (null != loAmountDetails.getProcurementTitle())
		{
			loPprocureCof.setProcurementTitle(loAmountDetails.getProcurementTitle());
		}
		loPprocureCof.setContractValue(loAmountDetails.getContractValue());
		loPprocureCof.setAmendedContractValue(loAmountDetails.getAmendedContractValue());
		loPprocureCof.setAmendmentStartDate(loAmountDetails.getAmendmentStartDate());
		loPprocureCof.setAmendmentEndDate(loAmountDetails.getAmendmentEndDate());
		loPprocureCof.setAmendmentEpin(loAmountDetails.getAmendmentEpin());
		loPprocureCof.setAmendmentTitle(loAmountDetails.getAmendmentTitle());
		loPprocureCof.setNewContractValue(String.valueOf(liNewContractValue));
		loPprocureCof.setAwardEpin(lsBaseAwardEpin);
		if (null != loContract && null != loContract.getContractStartDate())
		{
			loPprocureCof.setContractStartDate(DateUtil.getDateMMDDYYYYFormat(loContract.getContractStartDate()));
		}
		if (null != loContract && null != loContract.getContractEndDate())
		{
			loPprocureCof.setContractEndDate(DateUtil.getDateMMDDYYYYFormat(loContract.getContractEndDate()));
		}
		loPprocureCof.setAgencyName(HHSUtil.getAgencyName(loPprocureCof.getAgencyName()));
		return loPprocureCof;

	}

	/**
	 * This method is used to set the Data of Table on basis of
	 * asTDLabel,asTDValue,aoPdfTable and aoIsCurrencyType. for Header Section
	 * in COF budgetSummary detail PDF.
	 * 
	 * @param asTDLabel String Object
	 * @param asTDValue String Object
	 * @param aoPdfTable PdfPTable Object
	 * @param aoIsCurrencyType Boolean Object
	 * @throws ApplicationException -ApplicationException Object
	 */

	private void setTableContent(String asTDLabel, String asTDValue, PdfPTable aoPdfTable, Boolean aoIsCurrencyType,
			Font aoLabelFont) throws ApplicationException
	{
		Font loLabelFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD);
		Font loValueFont = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.NORMAL);
		Paragraph loCellOneParaGraph = new Paragraph();
		Paragraph loCellTwoParaGraph = new Paragraph();
		PdfPCell loCell;
		NumberFormat loCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
		if (!asTDLabel.isEmpty()
				&& (asTDLabel.equalsIgnoreCase(HHSConstants.CONTRACT_AMOUNT_PDF)
						|| asTDLabel.equalsIgnoreCase(HHSConstants.PROC_VALUE_PDF)
						|| asTDLabel.equalsIgnoreCase(HHSConstants.CONTRACT_VALUE_PDF) || asTDLabel
							.equalsIgnoreCase(HHSConstants.AMENDMENT_VALUE_PDF)))
		{
			if (aoIsCurrencyType
					&& (null != asTDValue && !asTDValue.isEmpty() && !asTDValue.equalsIgnoreCase(HHSConstants.NULL)))
			{

				if ((new BigDecimal(asTDValue)).compareTo(new BigDecimal(0)) < 0)
				{
					asTDValue = HHSConstants.LEFT_BRACKET
							+ loCurrencyFormatter.format((new BigDecimal(asTDValue)).multiply(new BigDecimal(-1)))
							+ HHSConstants.RIGHT_BRACKET;
				}
				else
				{
					asTDValue = loCurrencyFormatter.format((new BigDecimal(asTDValue)));
				}
			}
		}

		loCellOneParaGraph.setFont(loLabelFont);
		loCellOneParaGraph.add(asTDLabel);
		loCellOneParaGraph.setAlignment(Paragraph.ALIGN_RIGHT);
		loCell = new PdfPCell(loCellOneParaGraph);
		loCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		loCell.setBorder(0);

		aoPdfTable.addCell(loCell);
		loCellTwoParaGraph.setFont(loValueFont);
		loCellTwoParaGraph.add(asTDValue);
		loCell = new PdfPCell(loCellTwoParaGraph);
		loCell.setBorder(0);
		aoPdfTable.addCell(loCell);

	}

	/**
	 * This method is used to Set the data for Account allocation Amendment and
	 * Funding source Amnedmnet on basis of asParagraphHeaderText,
	 * asLastColumnText, aoTableContentList and aoFiscalYrMap .
	 * 
	 * @param asParagraphHeaderText -String Object
	 * @param asFirstColumnText -String Object
	 * @param asLastColumnText -String Object
	 * @param aoTableContentList -java.util.List<Object>
	 * @param aoFiscalYrMap -Map<String, String> Object
	 * @param aoDocumentObj -Document Object
	 * @param aoCatFont -Font Object
	 * @param aoLabelFont -Font Object
	 * @throws ApplicationException -ApplicationException Object
	 * 
	 */

	@SuppressWarnings("rawtypes")
	private void createAllocationDataTable(String asParagraphHeaderText, String asFirstColumnText,
			String asLastColumnText, java.util.List<Object> aoTableContentList, Map<String, String> aoFiscalYrMap,
			Document aoDocumentObj, Font aoCatFont, Font aoLabelFont) throws ApplicationException
	{
		Paragraph loChartOfAccParagraph = new Paragraph();
		Paragraph loChartOfAccLabelParagraph = new Paragraph(asParagraphHeaderText, aoCatFont);
		Paragraph loChartOfAccTableParagraph = new Paragraph();
		BigDecimal loTotalVal = new BigDecimal(0);
		NumberFormat loCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
		try
		{
			addEmptyLine(loChartOfAccLabelParagraph, 1);
			int liFiscalCount = Integer.valueOf(String.valueOf(aoFiscalYrMap.get(HHSConstants.LI_FYCOUNT)));
			PdfPTable loFundAllocationTable = new PdfPTable(liFiscalCount + 2);
			Paragraph loParaGraphCA = new Paragraph(asFirstColumnText, aoLabelFont);
			PdfPCell loCAPdfCell = new PdfPCell();
			loCAPdfCell.addElement(loParaGraphCA);
			loCAPdfCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			loFundAllocationTable.addCell(loCAPdfCell);
			Integer loStartFyYear = Integer
					.valueOf(String.valueOf(aoFiscalYrMap.get(HHSConstants.LI_START_FY_COUNTER)));
			for (int liCount = 0; liCount < liFiscalCount; liCount++)
			{
				Paragraph loHeaderParagraph = new Paragraph(HHSConstants.BASE_FY
						+ HHSUtil.getFiscalYearCounter(loStartFyYear), aoLabelFont);
				PdfPCell loPdfCell = new PdfPCell();
				loPdfCell.addElement(loHeaderParagraph);
				loPdfCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				loFundAllocationTable.addCell(loPdfCell);
				loStartFyYear++;
			}
			Paragraph loTotalHeaderParagraph = new Paragraph(asLastColumnText, aoLabelFont);
			PdfPCell loPdfCellTotal = new PdfPCell();
			loPdfCellTotal.addElement(loTotalHeaderParagraph);
			loPdfCellTotal.setBackgroundColor(BaseColor.LIGHT_GRAY);
			loFundAllocationTable.addCell(loPdfCellTotal);
			calculateOverall(aoTableContentList, aoLabelFont, loTotalVal, loCurrencyFormatter, liFiscalCount,
					loFundAllocationTable);

			for (Iterator loIterator = aoTableContentList.iterator(); loIterator.hasNext();)
			{
				setAllocationDataDetails(aoLabelFont, loTotalVal, loCurrencyFormatter, liFiscalCount,
						loFundAllocationTable, loIterator);
			}
			loChartOfAccParagraph.add(loChartOfAccLabelParagraph);
			loChartOfAccTableParagraph.add(loFundAllocationTable);
			loChartOfAccParagraph.add(loChartOfAccTableParagraph);
			aoDocumentObj.add(loChartOfAccParagraph);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error:: CreatePDFService:" + "createAllocationDataTable - "
					+ "Error Occured in CreatePDFService While fetching createAllocationDataTable", aoExp);
			setMoState("error occured in CreatePDFService while Displaying createAllocationDataTable");
			throw new ApplicationException("error occured while  createAllocationDataTable", aoExp);
		}
	}

	/**
	 * This method is used to fetch Entity Id and update the status for PDF
	 * Execute the query "fetchEntityIdForPdf" and get the Entity Id for Pdf.
	 * Execute the query "updateStatusForPdf" and Update the status for Pdf.
	 * 
	 * @param aoMybatisSession -SqlSession Object
	 * @param aoParamMap -HashMap Object
	 * @return loFetchEntityIdForPdf -List<PDFBatch> Object
	 * @throws ApplicationException -ApplicationException Object
	 */
	@SuppressWarnings("unchecked")
	public List<PDFBatch> fetchEntityIdAndUpdateStatusForPdf(SqlSession aoMybatisSession, HashMap aoParamMap)
			throws ApplicationException
	{
		List<PDFBatch> loFetchEntityIdForPdf = null;
		List<String> loPdf = new ArrayList<String>();
		try
		{
			loFetchEntityIdForPdf = (List<PDFBatch>) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_ENTITY_ID_FOR_PDF,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			for (PDFBatch loPdfBatch : loFetchEntityIdForPdf)
			{
				loPdf.add(loPdfBatch.getSubEntityId());
			}
			if (!loFetchEntityIdForPdf.isEmpty())
			{
				aoParamMap.put(HHSConstants.SUB_ENTITY_ID, loPdf);
				DAOUtil.masterDAO(aoMybatisSession, aoParamMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.UPDATE_STATUS_FOR_PDF, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(HHSConstants.AO_PARAM_MAP, aoParamMap);
			LOG_OBJECT.Error("Exception occured in CreatePDFService: fetchAmendmentListSummary method:: ", aoExp);
			throw aoExp;
		}
		return loFetchEntityIdForPdf;

	}

	/**
	 * This method update status to generated once pdf is generated and save in
	 * db. Execute the query "updateStatusForPdfAfterUpload" and update the
	 * status for Pdf after upload.
	 * 
	 * @param aoMybatisSession sql session as input
	 * @param aoParamMap map as input
	 * @return loSuccess success in case if status is set to generated
	 * @throws ApplicationException Exception in case a query fails.
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean updateStatusForPdfAfterUpload(SqlSession aoMybatisSession, HashMap aoParamMap)
			throws ApplicationException

	{
		Boolean loSuccess = false;
		try
		{
			aoParamMap.put(HHSConstants.STATUS_COLUMN,
					PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PDF_GENERATED));
			DAOUtil.masterDAO(aoMybatisSession, aoParamMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.UPDATE_STATUS_FOR_PDF_AFTER_UPLOAD, HHSConstants.JAVA_UTIL_HASH_MAP);
			loSuccess = true;
		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(HHSConstants.AO_PARAM_MAP, aoParamMap);
			LOG_OBJECT.Error("Exception occured in CreatePDFService: updateStatusForPdfAfterUpload method:: ", aoExp);
			throw aoExp;
		}
		return loSuccess;
	}

	/**
	 * This method is used to get the final list path and handle exception in
	 * case of Occurance of Exception.
	 * 
	 * @param aoPathListR3 -List<String> Object
	 * @param aoPathListR2 -List<String> Object
	 * @param aoPathListR2Contract -List<String> Object
	 * @return loFinalList -ArrayList<String> Object
	 * @throws ApplicationException -ApplicationException Object
	 */
	public ArrayList<String> getFinalListPath(List<String> aoPathListR3, List<String> aoPathListR2,
			List<String> aoPathListR2Contract) throws ApplicationException
	{

		ArrayList<String> loFinalList = new ArrayList<String>();
		try
		{
			loFinalList.addAll(aoPathListR3);
			loFinalList.addAll(aoPathListR2);
			loFinalList.addAll(aoPathListR2Contract);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error(
					"Exception occured in CreatePDFService while fetching contract budget amendment budget summary",
					aoExp);
			setMoState("Exception occured in CreatePDFService while fetching contract budget amendment budget summary");
			throw new ApplicationException(
					"Exception occured in CreatePDFService while fetching contract budget amendment budget summary"
							+ aoExp);
		}
		return loFinalList;
	}

	/**
	 * This method fetches RFP documents corresponding to the Procurement id
	 * 
	 * <ul>
	 * <li>1. Retrieve Map containing procurement Id and user org Id from the
	 * Channel object</li>
	 * <li>2. Create context data HashMap and populate the same with the
	 * parameter map</li>
	 * <li>3. If the fetched parameter map is not null then execute query
	 * <b>listOfRFPdocuments</b> to fetch the list of RFP documents</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId an String object
	 * @return loAwardDocumentList - an object of type List<ExtendedDocument>
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> fetchRFPDocuments(SqlSession aoMybatisSession, String asProcurementId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = null;
		HashMap<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		loHMContextData.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(loHMContextData);
		LOG_OBJECT.Debug("Entered into fetching RFP documements:::: " + param);
		//LOG_OBJECT.Debug("Entered into fetching RFP documements::" + loHMContextData.toString());
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		// checking if the param map contains data or not
		if (asProcurementId != null)
		{
			try
			{
				loRFPDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, loHMContextData,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.RFP_DOCUMENTS_LIST,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				setMoState("RFP Documents List fetched successfully for Proc Id:" + asProcurementId);
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				setMoState("Transaction Failed:: CreatePDFService:fetchRFPDocuments method - Error while fetching RFP Documents List for Procurement Id:"
						+ asProcurementId);
				aoExp.setContextData(loHMContextData);
				LOG_OBJECT.Error("Error while fetching RFP Documents List:", aoExp);
				throw aoExp;
			}
			// handling exception other than Application Exception
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Error while fetching Award Documents List for proposal Id:", aoExp);
				setMoState("Transaction Failed:: CreatePDFService:fetchRFPDocuments method - Error while fetching RFP Documents List for Procurement Id:"
						+ asProcurementId);
				throw new ApplicationException("Error occurred while RFP Documents List", aoExp);
			}
		}
		return loRFPDocumentList;
	}

	/**
	 * Changed method - Build 3.1.0, Enhancement id: 6025
	 * 
	 * This method is modified to put a if condition that check whether contract
	 * certification and budget is approved.
	 * 
	 * This method is used to check if Pdf is created or not. If Pdf is
	 * Generated then load it and if not then show the Error message. Execute
	 * the query "checkPdfCreated" for Pdf creation status.
	 * 
	 * @param aoMyBatisSession -SqlSession Object
	 * @param asContractId -String Object
	 * @param aoIsFinacialDocRequired -Boolean is Financial document required
	 * @return loPdfCreated -Boolean Object
	 * @throws ApplicationException -ApplicationException Object
	 */
	@SuppressWarnings("unchecked")
	public Boolean checkPdfCreated(SqlSession aoMyBatisSession, String asContractId, Boolean aoIsFinacialDocRequired,
			Integer aoContractCofNotApprovedCount, Integer aoContractBudgetNotApprovedCount)
			throws ApplicationException
	{
		List<String> loPdfCreationStatus = null;
		Boolean loPdfCreated = false;
		String lsErrorMessage = null;
		try
		{
			// If condition added for Build 3.1.0, Enhancement id: 6025 that
			// checks contract certification and budget is approved.
			if (aoContractCofNotApprovedCount == 0 && aoContractBudgetNotApprovedCount == 0)
			{
				loPdfCreationStatus = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.CHECK_PDF_CREATED,
						HHSConstants.JAVA_LANG_STRING);
				loPdfCreated = Boolean.TRUE;
				for (String lsPdfCreationStatus : loPdfCreationStatus)
				{
					if (aoIsFinacialDocRequired
							&& !(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.PDF_GENERATED).equalsIgnoreCase(lsPdfCreationStatus)))
					{
						loPdfCreated = Boolean.FALSE;
					}
				}

			}

		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error(lsErrorMessage, aoExp);
			setMoState(lsErrorMessage);
			throw aoExp;
		}
		return loPdfCreated;
	}

	/**
	 * Method added for 3.1.0 enhancement: 6025 This method is used to fetch
	 * proposal documents ids from db for which proposal documents needs to be
	 * fetched from filenet.
	 * <ul>
	 * <li>Execute query fetchProposalDocumentsForZip to fetch proposal
	 * documents ids.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession : SqlSession object
	 * @param asProposalId : List containing proposal ids for the procurement
	 * @param asProcurementId : String containing procurement id
	 * @return loProposalDocumentList : List of proposal documents
	 * @throws ApplicationException : If ApplicationException occurs.
	 */
	public List<ExtendedDocument> fetchProposalDocuments(SqlSession aoMybatisSession, List<String> asProposalId,
			String asProcurementId) throws ApplicationException
	{
		List<ExtendedDocument> loProposalDocumentList = null;
		Map<String, Object> loHMContextData = new HashMap<String, Object>();
		loHMContextData.put(HHSConstants.PARAM_MAP_LOWERCASE, asProposalId);
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
		loParamMap.put(HHSConstants.PROCUMENET_ID, asProcurementId);
		
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(loHMContextData);
		LOG_OBJECT.Debug("Entered into fetching RFP documements:::: " + param);
		//LOG_OBJECT.Debug("Entered into fetching RFP documements::" + loHMContextData.toString());
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		// checking if the param map contains data or not
		if (asProposalId != null)
		{
			try
			{

				loProposalDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_DOC_FOR_ZIP,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				setMoState("RFP Documents List fetched successfully for Proposal Id:" + asProposalId);
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				setMoState("Transaction Failed:: CreatePDFService:fetchRFPDocuments method - Error while fetching RFP Documents List for Procurement Id:"
						+ asProposalId);
				aoExp.setContextData(loHMContextData);
				LOG_OBJECT.Error("Error while fetching RFP Documents List:", aoExp);
				throw aoExp;
			}
			// handling exception other than Application Exception
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Error while fetching Award Documents List for proposal Id:", aoExp);
				setMoState("Transaction Failed:: CreatePDFService:fetchRFPDocuments method - Error while fetching RFP Documents List for Procurement Id:"
						+ asProposalId);
				throw new ApplicationException("Error occurred while RFP Documents List", aoExp);
			}
		}
		return loProposalDocumentList;
	}
}
