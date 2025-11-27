package com.nyc.hhs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.daomanager.service.PsrService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.PsrBean;

/**
 * <p>
 * This util class is used PsrService.generatePsrPdf() method.This class
 * generates the Psr Pdf file.All required information is passed in arguments
 * like ProcurementCOF details, Procurement Info.
 * </p>
 * 
 */
public class PsrPdfGenerationUtil
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	public static final LogInfo LOG_OBJECT = new LogInfo(PsrService.class);

	/**
	 * This method is called from subGeneratePsrPdf method. It generates the
	 * required fields for PSR PDF.
	 * @param aoPsrBean
	 * @param aoServiceList
	 * @param aoPCOFBean
	 * @param aoReturnedGridList
	 * @param aoFinanceGridList
	 * @param aoFormatter
	 * @param aoDocument
	 * @param aoDocFont
	 * @param asOutputPath
	 * @param aoShowFinanceGridFlag
	 * @return
	 * @throws ApplicationException
	 */
	public FileOutputStream subGeneratePsrPdf(PsrBean aoPsrBean, List aoServiceList, ProcurementCOF aoPCOFBean,
			List aoReturnedGridList, List aoFinanceGridList, NumberFormat aoFormatter, Document aoDocument,
			Font aoDocFont, String asOutputPath, Boolean aoShowFinanceGridFlag) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.subGeneratePsrPdf()");
			PdfPTable table = new PdfPTable(2);
			FileOutputStream loFileOutputStream = savePdfToPath(aoPsrBean, aoServiceList, aoFormatter, aoDocument,
					aoDocFont, asOutputPath, table);
			if (aoPsrBean.getConceptReportReleaseDt() != null)
			{
				createPdfRow(
						aoDocument,
						table,
						"Concept Report Release Date:",
						DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
								aoPsrBean.getConceptReportReleaseDt()));
			}
			createPdfRow(aoDocument, table, "RFP Release Date:",
					DateUtil.getDateMMddYYYYFormat(aoPsrBean.getRfpReleaseDate()));
			if (aoPsrBean.getIsOpenEndedRFP().equals(HHSConstants.ZERO))
			{
				getPdfDataOnOpenEnded(aoPsrBean, aoDocument, aoDocFont, table);
			}
			if (aoPsrBean.getRenewalOption() != null)
			{
				createPdfRow(aoDocument, table, "Renewal Options:", aoPsrBean.getRenewalOption());
			}
			createSubHeading(aoDocument, "Multi-Year Human Services Contract", aoDocFont);
			setMultiYearContractTable(aoDocument, aoPsrBean);
			if (aoPsrBean.getMultiYearHumanServOpt() != null)
			{
				createPdfRow(
						aoDocument,
						table,
						"The ACCO has determined that although the contract(s) awarded from this procurement require a contract term beyond nine years. Pursuant to § 2-04(e)(3), this is an extraordinary case and there are compelling circumstances warranting an award for a total term in excess of 9 years,because:",
						aoPsrBean.getMultiYearHumanServOpt());
			}
			if (aoPsrBean.getIsOpenEndedRFP().equals(HHSConstants.ZERO)
					&& aoPsrBean.getEstProcurementValue().compareTo(java.math.BigDecimal.ZERO) > 0)
			{
				getPcofInformation(aoPsrBean, aoPCOFBean, aoReturnedGridList, aoFinanceGridList, aoDocument, aoDocFont,
						table, aoShowFinanceGridFlag);
			}
			getSubHeaderInformation(aoPsrBean, aoDocument, aoDocFont, table);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.subGeneratePsrPdf()");
			return loFileOutputStream;
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error while Generating PDF File", loExp);
			throw loExp;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}
	}

	/**
	 * This method is called from subGeneratePsrPdf method. It generates the
	 * required fields for PSR PDF.
	 * @param aoPsrBean - PsrBean
	 * @param aoServiceList - List Object
	 * @param aoFormatter - Formatter Object
	 * @param aoDocument - Document Object
	 * @param aoDocFont - Font Object
	 * @param asOutputPath - String Object
	 * @param aoTable - Table Object
	 * @return loFileOutputStream - FileOutputStream Object
	 * @throws DocumentException - Exception
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ApplicationException
	 */
	private FileOutputStream savePdfToPath(PsrBean aoPsrBean, List aoServiceList, NumberFormat aoFormatter,
			Document aoDocument, Font aoDocFont, String asOutputPath, PdfPTable aoTable) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.savePdfToPath()");
			aoTable.setWidths(new int[]
			{ 2, 2 });
			aoTable.setWidthPercentage(93);
			File loPdfFile;
			FileOutputStream loFileOutputStream;
			loPdfFile = new File(asOutputPath);
			if (!loPdfFile.exists())
			{
				loPdfFile.createNewFile();
			}
			loFileOutputStream = new FileOutputStream(loPdfFile);
			PdfWriter writer = PdfWriter.getInstance(aoDocument, loFileOutputStream);
			aoDocument.setMarginMirroring(false);
			writer.setPageEvent(new HhsPdfPageEventHelper());
			aoDocument.open();
			getPdfHeaderInformation(aoPsrBean, aoDocument, aoDocFont, aoTable);
			createPdfRow(aoDocument, aoTable, "Consideration of Price:", aoPsrBean.getConsiderationPrice());
			if (aoPsrBean.getIsOpenEndedRFP().equals(ApplicationConstants.ZERO))
			{
				createPdfRow(aoDocument, aoTable, "Estimated No. of Contracts:", aoPsrBean.getEstNumberOfContracts()
						.toString());
				createPdfRow(aoDocument, aoTable, "Estimated Procurement Value:",
						aoFormatter.format(aoPsrBean.getEstProcurementValue()));
			}
			createServicesRecord(aoDocument, aoPsrBean, aoServiceList);
			createSubHeading(aoDocument, "Anticipated Procurement Dates", aoDocFont);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.savePdfToPath()");
			return loFileOutputStream;
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error while Generating PDF File", loExp);
			throw loExp;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}

	}

	/**
	 * This method is called from subGeneratePsrPdf method. It generates the
	 * required fields for PSR PDF.
	 * @param aoPsrBean - PsrBean Object
	 * @param aoDocument - itext Document Object
	 * @param aoDocFont - Font Object
	 * @param aoTable - Table Object
	 * @throws ApplicationException
	 */
	private void getSubHeaderInformation(PsrBean aoPsrBean, Document aoDocument, Font aoDocFont, PdfPTable aoTable)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.getSubHeaderInformation()");
		createSubHeading(aoDocument, "Agency Chief Contracting Officer Approval", aoDocFont);
		createPdfRow(aoDocument, aoTable, "Approved By:", aoPsrBean.getCreatedBy());
		createPdfRow(
				aoDocument,
				aoTable,
				"Approved Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getCreatedDate()));
		createSubHeading(aoDocument, "HHS Accelerator Director Approval", aoDocFont);
		createPdfRow(aoDocument, aoTable, "Approved By:", aoPsrBean.getApproverUserId());
		createPdfRow(
				aoDocument,
				aoTable,
				"Approved Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getContractStartFrom()));
		LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.getSubHeaderInformation()");
	}

	/**
	 * This method is called from savePdfToPath method. It generates the
	 * required fields for PSR PDF.
	 * @param aoPsrBean - PsrBean Object
	 * @param aoServiceList - List object
	 * @param aoDocument - itext Document Object
	 * @param aoDocFont - Font Object
	 * @param table - PdfPTable object
	 * @param aoTable - Table Object
	 * @throws ApplicationException - when any exception occurred we wrap it into this custom exception
	 */
	private void getPdfHeaderInformation(PsrBean aoPsrBean, Document aoDocument, Font aoDocFont, PdfPTable table)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.getPdfHeaderInformation()");
		createPdfHeader(aoDocument, aoPsrBean);
		createSubHeading(aoDocument, "Basic Information", aoDocFont);
		createPdfRow(aoDocument, table, "Procurement Title:", aoPsrBean.getProcurementTitle());
		createPdfRow(aoDocument, table, "Agency:", aoPsrBean.getAgencyName());
		createPdfRow(aoDocument, table, "Program Name:", aoPsrBean.getProgramName());
		createMailLabel(aoDocument, "Accelerator Primary Contact:", table, aoPsrBean.getAccPrimaryContact().split(
				"\\s" + "\\|\\|" + "\\s"));
		createMailLabel(aoDocument, "Accelerator Secondary Contact:", table, aoPsrBean.getAccSecondaryContact().split(
				"\\s" + "\\|\\|" + "\\s"));
		createMailLabel(aoDocument, "Agency Primary Contact:", table, aoPsrBean.getAgecncyPrimaryContact().split(
				"\\s" + "\\|\\|" + "\\s"));
		createMailLabel(aoDocument, "Agency Secondary Contact:", table, aoPsrBean.getAgecncySecondaryContact().split(
				"\\s" + "\\|\\|" + "\\s"));
		createPdfRow(aoDocument, table, "Agency Email Contact:", aoPsrBean.getEmail());
		createPdfRow(aoDocument, table, "Procurement Description:", aoPsrBean.getProcurementDescription());
		createContractDetails(aoDocument, aoPsrBean);
		LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.getPdfHeaderInformation()");
	}

	/**
	 * This method is called from subGeneratePsrPdf method. It generates the
	 * required fields for PSR PDF.
	 * @param aoPsrBean - PsrBean Object
	 * @param aoDocument - itext Document Object
	 * @param aoDocFont - Font Object
	 * @param aoTable - Table Object
	 * @throws ApplicationException
	 */
	private void getPdfDataOnOpenEnded(PsrBean aoPsrBean, Document aoDocument, Font aoDocFont, PdfPTable aoTable)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.getPdfDataOnOpenEnded()");
		createPdfRow(
				aoDocument,
				aoTable,
				"Proposal Due Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getProposalDueDateUpdated()));
		createSubHeading(aoDocument, "Anticipated Evaluation Dates", aoDocFont);
		createPdfRow(
				aoDocument,
				aoTable,
				"First Draft of RFP & Evaluation\n Criteria Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getFirstRFPEvalDateUpdated()));
		createPdfRow(
				aoDocument,
				aoTable,
				"Finalize RFP & Evaluation\n Criteria Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getFinalRFPEvalDateUpdated()));
		createPdfRow(
				aoDocument,
				aoTable,
				"Evaluator Training Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getEvaluatorTrainingDateUpdated()));
		createPdfRow(
				aoDocument,
				aoTable,
				"First Round of Evaluation\n Completion Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getFirstEvalCompletionDateUpdated()));
		createPdfRow(
				aoDocument,
				aoTable,
				"Finalize Evaluation Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getFinalEvalCompletionDateUpdated()));
		createPdfRow(
				aoDocument,
				aoTable,
				"Award Selection Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getAwardSelectionDateUpdated()));
		createSubHeading(aoDocument, "Anticipated Contract Dates", aoDocFont);
		createPdfRow(
				aoDocument,
				aoTable,
				"Contract Start Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getContractStartDatePlanned()));
		createPdfRow(
				aoDocument,
				aoTable,
				"Contract End Date:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPsrBean.getContractEndDateUpdated()));
		LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.getPdfDataOnOpenEnded()");
	}

	/**
	 * This method is called from subGeneratePsrPdf method. It generates the
	 * required fields for PSR PDF.
	 * @param aoPsrBean - PsrBean Object
	 * @param aoPCOFBean - ProcurementCOF Object
	 * @param aoReturnedGridList - List object
	 * @param aoFinanceGridList - List
	 * @param aoDocument - itext Document Object
	 * @param aoDocFont - Font Object
	 * @param aoTable - Table Object
	 * @param aoShowFinanceGridFlag
	 * @throws ApplicationException
	 */
	private void getPcofInformation(PsrBean aoPsrBean, ProcurementCOF aoPCOFBean, List aoReturnedGridList,
			List aoFinanceGridList, Document aoDocument, Font aoDocFont, PdfPTable aoTable,
			Boolean aoShowFinanceGridFlag) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.getPcofInformation()");
		aoDocument.newPage();
		Font loLabelFont = createFont(10, 1);
		createSubHeading(aoDocument, "Procurement Certification of Funds", aoDocFont);
		createPdfRow(aoDocument, aoTable, "Agency Code:", aoPCOFBean.getAgencyCode());
		createPdfRow(aoDocument, aoTable, "Submitted By:", aoPCOFBean.getCreatedByUserId());
		createPdfRow(
				aoDocument,
				aoTable,
				"Date Submitted:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPCOFBean.getCreatedDate()));
		createPdfRow(aoDocument, aoTable, "Approved By:", aoPCOFBean.getApproverFirstName());
		createPdfRow(
				aoDocument,
				aoTable,
				"Date Approved:",
				DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
						aoPCOFBean.getApprovedDate()));
		// JQ grid
		Map loContractMap = new HashMap();
		String lsContractStartDate = DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
				aoPsrBean.getContractStartDatePlanned());
		String lsContractEndDate = DateUtil.getDateByFormat(HHSR5Constants.YYYYMMDDFORMAT, HHSConstants.MMDDYYFORMAT,
				aoPsrBean.getContractEndDateUpdated());
		BaseControllerUtil.getContractFiscalYearsUtil(lsContractStartDate, lsContractEndDate, loContractMap);
		preProcessFinanceDetails(aoFinanceGridList, loContractMap);
		createAllocationDataTable(HHSConstants.ACCOUNT_ALLOCTAION_CHART, HHSConstants.ACC_TOTAL, aoFinanceGridList,
				loContractMap, aoDocument);
		if (aoShowFinanceGridFlag)
		{
			createAllocationDataTable(HHSConstants.FUNDING_SOURCE_ALLOCATION, HHSConstants.FUNDING_SOURCE,
					HHSConstants.ACC_TOTAL, aoReturnedGridList, loContractMap, aoDocument, aoDocFont, loLabelFont);
		}
		LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.getPcofInformation()");
	}

	/**
	 * This method processes the List<AccountsAllocationBean> and set required
	 * properties for charts of allocation grid in PSR PDF.
	 * @param aoFinanceGridList - List of FinanceGrid details
	 * @param aoFiscalYrMap - Fiscal year Hashmap
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	private void preProcessFinanceDetails(List aoFinanceGridList, Map aoFiscalYrMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.preProcessFinanceDetails()");
		if (aoFinanceGridList != null)
		{
			Integer loFYCount = Integer.valueOf(String.valueOf(aoFiscalYrMap.get(HHSConstants.LI_FYCOUNT)));
			List<AccountsAllocationBean> loAccountsAllocationBeanFinal = new ArrayList<AccountsAllocationBean>();
			for (int i = 0; i < aoFinanceGridList.size(); i = i + loFYCount)
			{
				AccountsAllocationBean loAccountsAllocationBean = (AccountsAllocationBean) aoFinanceGridList.get(i);
				for (int j = 1; j <= loFYCount; j++)
				{
					AccountsAllocationBean loAllocationBeanInt = (AccountsAllocationBean) aoFinanceGridList.get(i + j
							- 1);
					String lsMethodName = HHSConstants.SMALL_FY + j;
					try
					{
						BeanUtils.setProperty(loAccountsAllocationBean, lsMethodName, loAllocationBeanInt.getAmmount());
					}
					catch (IllegalAccessException loAppEx)
					{
						throw new ApplicationException(
								"Exception occured at PsrPdfGenerationUtil.preProcessFinanceDetails", loAppEx);
					}
					catch (InvocationTargetException loAppEx)
					{
						throw new ApplicationException(
								"Exception occured at PsrPdfGenerationUtil.preProcessFinanceDetails", loAppEx);
					}
				}
				loAccountsAllocationBeanFinal.add(loAccountsAllocationBean);
			}
			aoFinanceGridList.clear();
			aoFinanceGridList.addAll(loAccountsAllocationBeanFinal);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.preProcessFinanceDetails()");
		}
	}

	/**
	 * This method generates the Service Required Label inside PDF
	 * @param aoDocument - PDF Document Object
	 * @param aoPsrBean - PsrBean Object
	 * @param aoServiceList - Services selected List
	 * @throws ApplicationException - Exception
	 */
	private void createServicesRecord(Document aoDocument, PsrBean aoPsrBean, List<Procurement> aoServiceList)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.createServicesRecord()");
		Font loFontDocTable = createFont(12, 0);
		PdfPTable loPdfTable = new PdfPTable(2);
		loPdfTable.setWidthPercentage(100);
		Paragraph loDocTableParagraph = null;
		PdfPCell loPdfCell = null;
		loDocTableParagraph = createParagraph("HHS Accelerator Service\n Applications Required:", loFontDocTable,
				ApplicationConstants.EMPTY_STRING);
		loPdfCell = new PdfPCell(loDocTableParagraph);
		setCellPropertyLeft(loPdfCell);
		loPdfTable.addCell(loPdfCell);
		Chunk loSeviceTextInitial = new Chunk(
				"To receive this RFP in the HHS Accelerator system, you must have an approved Service Application for ");
		Chunk loSeviceTextMiddle = null;
		if (aoPsrBean.getServiceFilter().equals("1"))
		{
			loSeviceTextMiddle = new Chunk("at least one ", createFont(12, 1));
		}
		else
		{
			loSeviceTextMiddle = new Chunk("all ", createFont(12, 1));
		}
		Chunk loSeviceTextLast = new Chunk("of the following:");
		loDocTableParagraph = createParagraph(ApplicationConstants.EMPTY_STRING, loFontDocTable,
				ApplicationConstants.EMPTY_STRING);
		loDocTableParagraph.add(loSeviceTextInitial);
		loDocTableParagraph.add(loSeviceTextMiddle);
		loDocTableParagraph.add(loSeviceTextLast);
		loPdfCell = new PdfPCell();
		com.itextpdf.text.List loTableList = new com.itextpdf.text.List();
		loTableList.setIndentationRight(20);
		loTableList.setListSymbol("\u2022 ");
		for (int liServieCounter = 0; liServieCounter < aoServiceList.size(); liServieCounter++)
		{
			String lsService = aoServiceList.get(liServieCounter).getServiceName();
			ListItem loServiceItemList = new ListItem(lsService, loFontDocTable);
			loTableList.add(loServiceItemList);
		}
		Phrase loServicePharse = new Phrase();
		loServicePharse.add(loDocTableParagraph);// new
		loServicePharse.add(loTableList);
		loServicePharse.setFont(loFontDocTable);
		loPdfCell.addElement(loServicePharse);
		setCellProperty(loPdfCell);
		loPdfTable.addCell(loPdfCell);
		try
		{
			aoDocument.add(loPdfTable);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.createServicesRecord()");
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}

	}

	/**
	 * This method is used to Set the data for Account allocation details on
	 * basis of asParagraphHeaderText, asLastColumnText, aoTableContentList and
	 * aoFiscalYrMap .
	 * @param asParagraphHeaderText
	 * @param asFirstColumnText
	 * @param asLastColumnText
	 * @param aoTableContentList
	 * @param aoFiscalYrMap
	 * @param aoDocumentObj
	 * @throws ApplicationException
	 */
	private void createAllocationDataTable(String asParagraphHeaderText, String asLastColumnText,
			List aoTableContentList, Map<String, String> aoFiscalYrMap, Document aoDocumentObj)
			throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.createAllocationDataTable()");
			String lsVerdanaFontPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.VERDANA_BOLD_FONT_PATH);
			BaseFont loBaseFont = BaseFont.createFont(getJarPath() + lsVerdanaFontPath, BaseFont.WINANSI,
					BaseFont.NOT_EMBEDDED);
			Font loCatFont = new Font(loBaseFont, 13, Font.NORMAL, new BaseColor(0, 123, 164));
			Font loLabelFont = createFont(10, 1);
			Font loLabelFontRow = createFont(10, 0);
			Paragraph loChartOfAccPara = new Paragraph();
			Paragraph loChartOfAccLabelPara = new Paragraph(asParagraphHeaderText, loCatFont);
			Paragraph loChartOfAccTablePara = new Paragraph();
			BigDecimal loTotalVal = new BigDecimal(0);
			NumberFormat loCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
			loChartOfAccLabelPara.setSpacingAfter(10);
			int liFiscalCount = Integer.valueOf(String.valueOf(aoFiscalYrMap.get(HHSConstants.LI_FYCOUNT)));
			PdfPTable loFundAllocationTableObject = new PdfPTable(liFiscalCount + 4);
			loFundAllocationTableObject.setWidthPercentage(100);
			Paragraph loParaGraphCA = new Paragraph("UoA*-BC*-OC*", loLabelFont);
			PdfPCell loCAPdfCell = new PdfPCell();
			loCAPdfCell.addElement(loParaGraphCA);
			loCAPdfCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			loFundAllocationTableObject.addCell(loCAPdfCell);
			loParaGraphCA = new Paragraph("SubOC", loLabelFont);
			loCAPdfCell = new PdfPCell();
			loCAPdfCell.addElement(loParaGraphCA);
			loCAPdfCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			loFundAllocationTableObject.addCell(loCAPdfCell);
			loParaGraphCA = new Paragraph("RC", loLabelFont);
			loCAPdfCell = new PdfPCell();
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
			calculateOverall(aoTableContentList, loLabelFont, loCurrencyFormat, liFiscalCount,
					loFundAllocationTableObject);
			for (Iterator loIterator = aoTableContentList.iterator(); loIterator.hasNext();)
			{
				setAllocationDataDetails(loLabelFontRow, loTotalVal, loCurrencyFormat, liFiscalCount,
						loFundAllocationTableObject, loIterator);
			}
			loChartOfAccPara.add(loChartOfAccLabelPara);
			loChartOfAccTablePara.add(loFundAllocationTableObject);
			loChartOfAccPara.add(loChartOfAccTablePara);
			aoDocumentObj.add(loChartOfAccPara);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.createAllocationDataTable()");
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while Creating Allocation Data Table ", aoExp);
			throw new ApplicationException("error occured while Creating Allocation Data Table", aoExp);
		}
	}

	/**
	 * This method is used to add EmptyLine.
	 * @param aoParagraph Paragraph object
	 * @param ainumber integer
	 */
	private static void addEmptyLine(Paragraph aoParagraph, int ainumber)
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.addEmptyLine()");
		for (int liCount = 0; liCount < ainumber; liCount++)
		{
			aoParagraph.add(new Paragraph(HHSConstants.SPACE));
		}
		LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.addEmptyLine()");
	}

	/**
	 * This method is called from createAllocationDataTable method to calculate
	 * overall tab in Financial Details values based on Fiscal year
	 * @param aoTableContentList - List<AccountsAllocationBean> Object
	 * @param loLabelFont - Font Object
	 * @param loCurrencyFormat - Currency Format
	 * @param liFiscalCount - Fiscal year count
	 * @param loFundAllocationTableObject - PdfTable Object
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private static void calculateOverall(List<Object> aoTableContentList, Font loLabelFont,
			NumberFormat loCurrencyFormat, int liFiscalCount, PdfPTable loFundAllocationTableObject)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.calculateOverall()");
		BigDecimal[][] liNums = new BigDecimal[aoTableContentList.size()][liFiscalCount];
		int liTableCount = 0;
		Paragraph loIDTdParagraph = new Paragraph(HHSConstants.OVERALL, loLabelFont);
		PdfPCell loIDPdfCell = new PdfPCell();
		loIDPdfCell.addElement(loIDTdParagraph);
		loFundAllocationTableObject.addCell(loIDPdfCell);
		Iterator loIteratorRow = aoTableContentList.iterator();
		Object loBeanObj = loIteratorRow.next();
		if (loBeanObj instanceof AccountsAllocationBean)
		{
			loIDPdfCell = new PdfPCell();
			loFundAllocationTableObject.addCell(loIDPdfCell);
			loIDPdfCell = new PdfPCell();
			loFundAllocationTableObject.addCell(loIDPdfCell);
		}
		for (Iterator loIterator = aoTableContentList.iterator(); loIterator.hasNext(); liTableCount++)
		{
			liNums = setAllocationDataDetailsOverall(liFiscalCount, loIterator, liNums, liTableCount);
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
		LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.calculateOverall()");
	}

	/**
	 * This method is called from calculateOverall method to get amount values
	 * based on Fiscal year
	 * 
	 * @param aoLabelFont - itext Font
	 * @param aoTotalVal - Amount of fiscal year
	 * @param aoCurrencyFormatter - Currency Formatter
	 * @param aiFiscalCount - Count of Fiscal years
	 * @param aoFundAllocationTable - itext Table Object
	 * @param aoIterator - Iterator
	 * @param liNums - Initailised Fiscal year values
	 * @param loTableCount - Rows for itext table object Count
	 * @return - Amount with respect to Fiscal year
	 * @throws ApplicationException - Fiscal year
	 */
	private static BigDecimal[][] setAllocationDataDetailsOverall(int aiFiscalCount, Iterator aoIterator,
			BigDecimal[][] liNums, int loTableCount) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.setAllocationDataDetailsOverall()");
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
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.setAllocationDataDetailsOverall()");
			return liNums;

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while setting the allocation table data ", aoExp);
			throw new ApplicationException("Error Occured while setting the allocation table data:", aoExp);
		}
	}

	/**
	 * This method is used to populate Name and email into PDF.
	 * 
	 * @param aoDocument - Document Object
	 * @param asLabel - String Label
	 * @param aoPdfTable - PdfTable Object
	 * @param asNameMailList - List of Name and email
	 * @throws ApplicationException
	 */
	private void createMailLabel(Document aoDocument, String asLabel, PdfPTable aoPdfTable, String[] asNameMailList)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.createMailLabel()");
		aoPdfTable = new PdfPTable(2);
		aoPdfTable.setWidthPercentage(100);
		Paragraph loParagraph = createParagraph(asLabel, createFont(12, 0), HHSConstants.EMPTY_STRING);
		PdfPCell loPdfCell = new PdfPCell(loParagraph);
		setCellPropertyLeft(loPdfCell);
		aoPdfTable.addCell(loPdfCell);
		loParagraph = createParagraph(asNameMailList[0] + HHSConstants.COMMA + "\n" + asNameMailList[1],
				createFont(12, 0), HHSConstants.EMPTY_STRING);
		loPdfCell = new PdfPCell(loParagraph);
		setCellProperty(loPdfCell);
		aoPdfTable.addCell(loPdfCell);
		try
		{
			aoDocument.add(aoPdfTable);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.createMailLabel()");
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}
	}

	/**
	 * This method is used to insert Contract Term values
	 * 
	 * @param aoDocument - Document Object
	 * @param aoPsrBean - PsrBean Object
	 * @throws ApplicationException
	 */
	private void setMultiYearContractTable(Document aoDocument, PsrBean aoPsrBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.setMultiYearContractTable()");
		Font loFontDocTable = null;
		Paragraph loDocTableParagraph = null;
		try
		{
			loFontDocTable = createFont(12, 0);
			String[] lsSplitBContract = new String[]
			{ "9", "6", "0" };
			String[] lsSplitValContract = new String[]
			{ "Nine-year contract term", "Six-year contract term", "Not Applicable" };
			loDocTableParagraph = createParagraph(
					"The ACCO has determined that the needs of the population will continue beyond one year and that a multi-term contract will serve the best interests of the City by encouraging effective competition/promoting economies, pursuant to § 2-04(d)(2) of the PPB Rules, because:",
					loFontDocTable, "");
			PdfPTable loPdfTable = new PdfPTable(2);
			loPdfTable.setWidthPercentage(100);
			PdfPCell loPdfCell = new PdfPCell(loDocTableParagraph);
			setCellPropertyLeft(loPdfCell);
			loPdfTable.addCell(loPdfCell);
			loDocTableParagraph = createParagraph(aoPsrBean.getMultiYearHumanServContract(), loFontDocTable, "");
			loPdfCell = new PdfPCell(loDocTableParagraph);
			setCellProperty(loPdfCell);
			loPdfTable.addCell(loPdfCell);
			loDocTableParagraph = createParagraph(
					"The ACCO has determined, pursuant to § 2-04(e) of the PPB Rules that the contract(s) awarded from this procurement will fall within the parameters of:",
					loFontDocTable, "");
			loPdfCell = new PdfPCell(loDocTableParagraph);
			setCellPropertyLeft(loPdfCell);
			loPdfTable.addCell(loPdfCell);
			PdfPCell loPdfCellNested = new PdfPCell();
			PdfPTable loPdfTableNested = new PdfPTable(2);
			loPdfTableNested.setWidths(new int[]
			{ 1, 15 });
			loPdfTableNested.setWidthPercentage(93);
			for (int i = 0; i < lsSplitValContract.length; i++)
			{
				if (aoPsrBean.getContractTermInfo().contains(lsSplitBContract[i]))
				{
					loPdfCell = new PdfPCell(setCheckBoxImage(true));
				}
				else
				{
					loPdfCell = new PdfPCell(setCheckBoxImage(false));
				}
				loPdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				loPdfCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
				loDocTableParagraph = createParagraph(lsSplitValContract[i], loFontDocTable, "");
				PdfPCell cell2 = new PdfPCell(loDocTableParagraph);
				cell2.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
				loPdfCell.setPaddingTop(6);
				loPdfCell.setPaddingRight(6);
				loPdfTableNested.addCell(loPdfCell);
				loPdfTableNested.addCell(cell2);
				setCellProperty(loPdfCell);
				setCellProperty(cell2);
				loPdfCellNested.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
			}
			loPdfCellNested.addElement(loPdfTableNested);
			loPdfTable.addCell(loPdfCellNested);
			aoDocument.add(loPdfTable);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.setMultiYearContractTable()");
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}

	}

	/**
	 * This methods creates the Header of PDF.
	 * 
	 * @param aoDocument - Document Object
	 * @param aoPsrBean - PsrBean Object
	 * @throws ApplicationException
	 */
	private void createPdfHeader(Document aoDocument, PsrBean aoPsrBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.createPdfHeader()");
		Paragraph headerParagraph = null;
		Font loHeaderFont = null;
		String loAlignType = "CENTER";
		loHeaderFont = createFont(14, 1);
		headerParagraph = createParagraph("HHS Accelerator (PPB Rule 3-16)", loHeaderFont, loAlignType);
		try
		{
			aoDocument.add(headerParagraph);
			headerParagraph = createParagraph("Pre-Solicitation Review", loHeaderFont, loAlignType);
			aoDocument.add(headerParagraph);
			if (StringUtils.isNotBlank(aoPsrBean.getProcurementEpin()))
			{
				headerParagraph = createParagraph("EPIN:" + aoPsrBean.getProcurementEpin(), loHeaderFont, loAlignType);
			}
			else
			{
				headerParagraph = createParagraph("EPIN:" + HHSConstants.NA_KEY, loHeaderFont, loAlignType);
			}
			aoDocument.add(headerParagraph);
			aoDocument.add(Chunk.NEWLINE);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.createPdfHeader()");
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}
	}

	/**
	 * This method creates Paragraph for PDF and align it.
	 * 
	 * @param aoParagraphName - String Object
	 * @param aoFont - Font Object
	 * @param loAlignType - String AlignType
	 * @return - Paragraph Object
	 */
	private static Paragraph createParagraph(String aoParagraphName, Font aoFont, String loAlignType)
	{
		Paragraph loParagraph = new Paragraph(aoParagraphName, aoFont);
		if ("CENTER".equals(loAlignType))
		{
			loParagraph.setAlignment(Element.ALIGN_CENTER);
		}
		if ("LEFT".equals(loAlignType))
		{
			loParagraph.setAlignment(Element.ALIGN_LEFT);
		}
		return loParagraph;
	}

	/**
	 * This method returns the Font for PDF Document
	 * @param aiFontType - Type of font where input 1 means BOLD and 0 meaning NORMAL Fonts.
	 * @param aoFontSize - float value containing sixe of font
	 * @return loFont - Font
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	private Font createFont(float aoFontSize, int aiFontType) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.createFont()");
		Font loFont = null;
		BaseFont loBaseFont = null;
		String lsVerdanaBoldFontPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				HHSR5Constants.VERDANA_BOLD_FONT_PATH);
		String lsVerdanaRegularFontPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				HHSR5Constants.VERDANA_REGULAR_FONT_PATH);
		String loFontPathRegular = getJarPath() + lsVerdanaRegularFontPath;
		String loFontPathBold = getJarPath() + lsVerdanaBoldFontPath;
		try
		{
			if (aiFontType == 0)
			{
				loBaseFont = BaseFont.createFont(loFontPathRegular, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
				loFont = new Font(loBaseFont, aoFontSize, aiFontType, BaseColor.BLACK);
			}
			else
			{
				loBaseFont = BaseFont.createFont(loFontPathBold, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
				loFont = new Font(loBaseFont, aoFontSize, aiFontType, BaseColor.BLACK);
			}
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}
		LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.createFont()");
		return loFont;
	}

	/**
	 * This method adds the PSR data to PDF
	 * @param aoDocument - PDF document object
	 * @param aoTable - PDF table Object
	 * @param asKey - The required fields of PDF
	 * @param asValue - The values corresponding to fields
	 * @throws ApplicationException- when any exception occurred wrap it into
	 *             application exception.
	 */
	private void createPdfRow(Document aoDocument, PdfPTable aoTable, String asKey, String asValue)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.createPdfRow()");
		aoTable = new PdfPTable(2);
		aoTable.setWidthPercentage(100);
		Font loFontDocTable = createFont(12, 0);
		Paragraph loDocTableParagraph = null;
		PdfPCell loPdfCell = null;
		loDocTableParagraph = createParagraph(asKey, loFontDocTable, "");
		loPdfCell = new PdfPCell(loDocTableParagraph);
		setCellPropertyLeft(loPdfCell);
		aoTable.addCell(loPdfCell);
		loDocTableParagraph = createParagraph(asValue, loFontDocTable, "");
		loPdfCell = new PdfPCell(loDocTableParagraph);
		setCellProperty(loPdfCell);
		aoTable.addCell(loPdfCell);
		try
		{
			aoDocument.add(aoTable);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.createPdfRow()");
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}
	}

	/**
	 * This method sets the alignment properties for PDF contents.
	 * @param aoPdfCell - PdfPCell object
	 */
	private static void setCellPropertyLeft(PdfPCell aoPdfCell)
	{
		aoPdfCell.setMinimumHeight(23f);
		aoPdfCell.setPaddingLeft(10);
		aoPdfCell.setPaddingBottom(2.8f);
		aoPdfCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		aoPdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		aoPdfCell.setBackgroundColor(new BaseColor(242, 242, 242));
		aoPdfCell.setBorderColor(BaseColor.WHITE);
		aoPdfCell.setBorderWidth(3f);
	}

	/**
	 * This method sets the alignment properties for PDF contents.
	 * @param aoPdfCell - PdfPCell object
	 */
	private static void setCellProperty(PdfPCell aoPdfCell)
	{
		aoPdfCell.setPaddingLeft(10);
		aoPdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		aoPdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		aoPdfCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);

	}

	/**
	 * This method populates the checked or unchecked boxes on pdf.
	 * @param aoImageFlag - Boolean value that determines which check/unchecked image is
	 *            required.
	 * @return Image - Image Object for PDF.
	 * @throws ApplicationException
	 */
	private Image setCheckBoxImage(Boolean aoImageFlag) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.setCheckBoxImage()");
		try
		{
			String lsCheckBoxFontPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.CHECKBOX_IMAGE_PATH_CHECKED);
			String lsUnCheckBoxFontPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.CHECKBOX_IMAGE_PATH_UNCHECKED);
			if (aoImageFlag)
			{
				String loImagePath = getJarPath() + lsCheckBoxFontPath;
				Image image = Image.getInstance(loImagePath);
				image.scaleAbsolute(8, 8);
				LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.setCheckBoxImage()");
				return image;
			}
			else
			{
				String loImagePath = getJarPath() + lsUnCheckBoxFontPath;
				Image image = Image.getInstance(loImagePath);
				image.scaleAbsolute(8, 8);
				LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.setCheckBoxImage()");
				return image;
			}
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}

	}

	/**
	 * This methods creates contract details for PDF.
	 * 
	 * @param aoDocument - Document Object
	 * @param aoPsrBean - PsrBean Object
	 * @param aoServiceList - Selected Services List
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	private void createContractDetails(Document aoDocument, PsrBean aoPsrBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.createContractDetails()");
		Font loFontDocTable = null;
		Paragraph loDocTableParagraph = null;
		PdfPCell loPdfcell = null;
		String lsBasisContractOut = aoPsrBean.getBasisContractOut();
		String lsAnticipateLevelComp = aoPsrBean.getAnticipateLevelComp();
		String[] lsAnticipateLevelDefined = new String[]
		{ "H", "M", "L" };
		String[] lsBasisContractOutDefined = new String[]
		{ "Develop/maintain/strengthen relationship between  non-profits/charities & communities served",
				"Obtain cost effective services", "Obtain special expertise",
				"Obtain personnel or expertise not available in the agency",
				"Provide services not needed on a long-term basis", "Accomplish work within a limited amount of time" };
		String[] lsAnticipateLevelValues = new String[]
		{ "High", "Medium", "Low" };
		PdfPTable loTable = new PdfPTable(2);
		loFontDocTable = createFont(12, 0);
		try
		{
			loTable.setWidths(new int[]
			{ 2, 2 });
			loTable.getDefaultCell().setBorderWidth(0f);
			loTable.setWidthPercentage(100);
			loPdfcell = new PdfPCell(createParagraph("Basis for Contracting Out:", loFontDocTable, ""));
			setCellPropertyLeft(loPdfcell);
			loTable.addCell(loPdfcell);
			// Nested Table
			PdfPCell cellTable = new PdfPCell();
			PdfPTable loPdfPtableNested = new PdfPTable(2);
			loPdfPtableNested.setWidths(new int[]
			{ 1, 15 });
			loPdfPtableNested.setWidthPercentage(93);
			for (int liBasisContractCounter = 0; liBasisContractCounter < lsBasisContractOutDefined.length; liBasisContractCounter++)
			{
				if (lsBasisContractOut.contains(String.valueOf(liBasisContractCounter)))
				{
					loPdfcell = new PdfPCell(setCheckBoxImage(true));
				}
				else
				{
					loPdfcell = new PdfPCell(setCheckBoxImage(false));
				}
				loPdfcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				loPdfcell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
				loDocTableParagraph = createParagraph(lsBasisContractOutDefined[liBasisContractCounter],
						loFontDocTable, "");
				PdfPCell loCell = new PdfPCell(loDocTableParagraph);
				loCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
				loPdfcell.setPaddingTop(6);
				loPdfcell.setPaddingRight(6);

				loPdfPtableNested.addCell(loPdfcell);
				loPdfPtableNested.addCell(loCell);
				setCellProperty(loPdfcell);
				setCellProperty(loCell);
				cellTable.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
			}
			cellTable.addElement(loPdfPtableNested);
			loTable.addCell(cellTable);
			loPdfcell = new PdfPCell(createParagraph("Anticipated Level of Competition:", loFontDocTable, ""));
			setCellPropertyLeft(loPdfcell);
			loTable.addCell(loPdfcell);
			PdfPCell loPdfPCell = new PdfPCell();
			PdfPTable loPdfTable = new PdfPTable(2);
			loPdfTable.setWidths(new int[]
			{ 1, 15 });
			loPdfTable.setWidthPercentage(93);
			for (int liAnticipateCounter = 0; liAnticipateCounter < lsAnticipateLevelValues.length; liAnticipateCounter++)
			{
				if (lsAnticipateLevelComp.contains(lsAnticipateLevelDefined[liAnticipateCounter]))
				{
					loPdfcell = new PdfPCell(setCheckBoxImage(true));
				}
				else
				{
					loPdfcell = new PdfPCell(setCheckBoxImage(false));
				}
				loPdfcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				loPdfcell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
				loDocTableParagraph = createParagraph(lsAnticipateLevelValues[liAnticipateCounter], loFontDocTable, "");
				PdfPCell loPdfCellForTable = new PdfPCell(loDocTableParagraph);
				loPdfCellForTable.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
				loPdfcell.setPaddingTop(6);
				loPdfcell.setPaddingRight(6);
				loPdfTable.addCell(loPdfcell);
				loPdfTable.addCell(loPdfCellForTable);
				setCellProperty(loPdfcell);
				setCellProperty(loPdfCellForTable);
				loPdfPCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
			}
			loPdfPCell.addElement(loPdfTable);
			loTable.addCell(loPdfPCell);
			aoDocument.add(loTable);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.createContractDetails()");
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}
	}

	/**
	 * This method puts the Pdf sub-heading and align it.
	 * 
	 * @param aoDocument - Document Object
	 * @param asDocHeading - String Object
	 * @param aoDocFont - Font Object
	 * @throws ApplicationException - when any exception occurs wrap it into
	 *             application exception.
	 */
	private void createSubHeading(Document aoDocument, String asDocHeading, Font aoDocFont) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.createSubHeading()");
		String lsDocAlignType = "LEFT";
		Paragraph loDocParagraph = createParagraph(asDocHeading, aoDocFont, lsDocAlignType);
		loDocParagraph.setSpacingAfter(10);
		try
		{
			aoDocument.add(loDocParagraph);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.createSubHeading()");
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at Generating PDF File", loAppEx);
			throw new ApplicationException("Exception occured  at Generating PDF File", loAppEx);
		}
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
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.createAllocationDataTable()");
		Font loLabelFontRow = createFont(10, 0);
		Paragraph loChartOfAccParagraph = new Paragraph();
		Paragraph loChartOfAccLabelParagraph = new Paragraph(asParagraphHeaderText, aoCatFont);
		Paragraph loChartOfAccTableParagraph = new Paragraph();
		BigDecimal loTotalVal = new BigDecimal(0);
		NumberFormat loCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
		try
		{
			loChartOfAccLabelParagraph.setSpacingAfter(10);
			int liFiscalCount = Integer.valueOf(String.valueOf(aoFiscalYrMap.get(HHSConstants.LI_FYCOUNT)));
			PdfPTable loFundAllocationTable = new PdfPTable(liFiscalCount + 2);
			loFundAllocationTable.setWidthPercentage(100);
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
			calculateOverall(aoTableContentList, aoLabelFont, loCurrencyFormatter, liFiscalCount, loFundAllocationTable);

			for (Iterator loIterator = aoTableContentList.iterator(); loIterator.hasNext();)
			{
				setAllocationDataDetails(loLabelFontRow, loTotalVal, loCurrencyFormatter, liFiscalCount,
						loFundAllocationTable, loIterator);
			}
			loChartOfAccParagraph.add(loChartOfAccLabelParagraph);
			loChartOfAccTableParagraph.add(loFundAllocationTable);
			loChartOfAccParagraph.add(loChartOfAccTableParagraph);
			aoDocumentObj.add(loChartOfAccParagraph);
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.createAllocationDataTable()");
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error:: CreatePDFService:" + "createAllocationDataTable - "
					+ "Error Occured in CreatePDFService While fetching createAllocationDataTable", aoExp);
			throw new ApplicationException("error occured while  createAllocationDataTable", aoExp);
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
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.setAllocationDataDetails()");
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
				loIDTdParagraph = new Paragraph(((AccountsAllocationBean) loBeanObj).getUnitOfAppropriation() + "-"
						+ ((AccountsAllocationBean) loBeanObj).getBudgetCode() + "-"
						+ ((AccountsAllocationBean) loBeanObj).getObjectCode(), aoLabelFont);
				PdfPCell loIDPdfCell = new PdfPCell();
				loIDPdfCell.setPaddingLeft(8);
				loIDPdfCell.addElement(loIDTdParagraph);
				aoFundAllocationTable.addCell(loIDPdfCell);
				loBeanObj = (AccountsAllocationBean) loBeanObj;
				loIDTdParagraph = new Paragraph(((AccountsAllocationBean) loBeanObj).getObjectCode(), aoLabelFont);
				loIDPdfCell = new PdfPCell();
				loIDPdfCell.addElement(loIDTdParagraph);
				aoFundAllocationTable.addCell(loIDPdfCell);
				loIDTdParagraph = new Paragraph(((AccountsAllocationBean) loBeanObj).getRc(), aoLabelFont);
				loIDPdfCell = new PdfPCell();
				loIDPdfCell.addElement(loIDTdParagraph);
				aoFundAllocationTable.addCell(loIDPdfCell);
			}
			else if (loBeanObj instanceof FundingAllocationBean)
			{
				loBeanObj = (FundingAllocationBean) loBeanObj;
				lsId = lsId.substring(lsId.lastIndexOf(HHSConstants.HYPHEN) + 1, lsId.length());
				loIDTdParagraph = new Paragraph(lsId, aoLabelFont);
				PdfPCell loIDPdfCell = new PdfPCell();
				loIDPdfCell.addElement(loIDTdParagraph);
				loIDPdfCell.setPaddingLeft(8);
				aoFundAllocationTable.addCell(loIDPdfCell);
			}
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
			LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.setAllocationDataDetails()");
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured while setting the allocation table data ", aoExp);
			throw new ApplicationException("Error Occured while setting the allocation table data:", aoExp);
		}
	}

	/**
	 * This method is used to getPath of Fonts, Images for Psr Pdf file.
	 * 
	 * @return lsParentPath - String of Path
	 */
	public String getJarPath()
	{
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil.getJarPath()");
		File loJarPath = new File(PropertyLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String lsParentPath = loJarPath.getAbsolutePath();
		LOG_OBJECT.Info("Entered PsrPdfGenerationUtil - lsParentPath-->" + lsParentPath);
		if (lsParentPath.contains(HHSConstants.DOT))
		{
			lsParentPath = lsParentPath.substring(0, lsParentPath.lastIndexOf(HHSConstants.CHAR_FORWARDSLASH));
		}
		LOG_OBJECT.Info("Exited PsrPdfGenerationUtil.getJarPath()");
		return lsParentPath;
	}
}
