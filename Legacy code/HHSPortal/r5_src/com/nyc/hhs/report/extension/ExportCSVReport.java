package com.nyc.hhs.report.extension;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.birt.report.engine.emitter.csv.CSVRenderOption;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.report.framework.impl.HHSExportReportDetails;

/**
 * This class is used to get report run and render map
 */
public class ExportCSVReport extends HHSExportReportDetails
{
	/**
	 * This method will get report run and render map.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ExportCSVReport.class);

    /**
	 * This Method is used to get Report Runnable Data.
	 * @return loReportRenderMap Map<String, Object> 
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getReportRunnableData() throws ApplicationException
	{
		ByteArrayOutputStream loOutputStream = new ByteArrayOutputStream();
		Map<String, Object> loReportRenderMap = new HashMap<String, Object>();
		try
		{
			// Creating CSV Render Option object here..
			CSVRenderOption loCsvOptions = new CSVRenderOption();
			String format = CSVRenderOption.OUTPUT_FORMAT_CSV;
			// Setting up various CSV Render Option before running the task
			// CSV Render Option to set the output format
			loCsvOptions.setOutputFormat(format);
			// CSV Render set ouputStream
			loCsvOptions.setOutputStream(loOutputStream);
			// CSV Render Option to set if Data Type of column need to be
			// rendered in second row of output
			loCsvOptions.setShowDatatypeInSecondRow(false);
			// CSV Render Option to Render a Table by Name
			loCsvOptions.setExportTableByName(HHSR5Constants.DATA);
			// CSV Render Options to specify the delimiter
			loCsvOptions.setDelimiter(ApplicationConstants.COMMA);
			// CSV Render Option to specify the character to be replaced if
			// delmiter appears in actual text
			loCsvOptions.setReplaceDelimiterInsideTextWith("");
			loReportRenderMap.put(HHSR5Constants.EMITTER_ID, "org.eclipse.birt.report.engine.emitter.csv");
			loReportRenderMap.put(HHSR5Constants.RENDER_OPTION, loCsvOptions);
			loReportRenderMap.put(HHSR5Constants.MIME_TYPE, HHSR5Constants.TEXT_CSV);
		}
		catch (Exception loException)
		{
			LOG_OBJECT.Error("Error in reporting Service while getting " + "run and render task info", loException);
			throw new ApplicationException("Error Occured While Getting Export Details", loException);
		}
		return loReportRenderMap;
	}

}
