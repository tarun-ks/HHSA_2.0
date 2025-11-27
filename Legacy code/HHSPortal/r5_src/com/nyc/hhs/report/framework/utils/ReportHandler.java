package com.nyc.hhs.report.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.report.action.BirtEngine;
import com.nyc.hhs.report.extension.IBirtExtension;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * <p>
 * ReportHandler serves as the handler for report list page. Report page for
 * Accelerator/Agency/Provider is rendered through renderReport method of this
 * class.
 */
public final class ReportHandler
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ReportHandler.class);
	private static String msReportFolder = HHSR5Constants.FOLDER_PATH_REPORT;
	private static IReportEngine moReportEngine;
	private static Map<String, IReportRunnable> moIReportRunnableMap = new HashMap<String, IReportRunnable>();
	private static HTMLRenderOption moOptions = new HTMLRenderOption();
	private static Object moMutex = new Object();

	// Default Constructor
	private ReportHandler()
	{
	}

	/**
	 * This method contain render report related information.
	 * @param aoRequest HttpServletRequest object
	 * @param fileName Name of the file
	 * @param request request as input
	 * @param aoReportConfigData config data as input
	 * @param aoWriterObj writer object as input
	 * @param asFyId Fiscal year id of the report to be rendered
	 * @param abLoadingDashboard boolean to validate whether to display chart of dashboard or detail page
	 * @return void
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public static void renderReport(HttpServletRequest aoRequest, Map<String, Map<String, Object>> aoReportConfigData,
			JspWriter aoWriterObj, String asFyId, boolean abLoadingDashboard) throws ApplicationException
	{
		LOG_OBJECT.Error("START  renderReport ", CommonUtil.getCurrentTimeInMilliSec());
		ServletContext loServletContext = aoRequest.getSession().getServletContext();
		InputStream loReportIO = null;
		Map<String, Object> loReportConfigDetails = null;
		try
		{
			init(loServletContext);
			if (aoReportConfigData != null && !aoReportConfigData.isEmpty())
			{
				loReportConfigDetails = aoReportConfigData.get(HHSR5Constants.RENDER_DETAILS);
				if (loReportConfigDetails != null && !loReportConfigDetails.isEmpty())
				{
					loReportIO = renderReport(aoRequest, aoReportConfigData, aoWriterObj, asFyId, abLoadingDashboard,
							loServletContext, loReportConfigDetails);
				}
			}
			cleanup(loServletContext);
			LOG_OBJECT.Error("END  renderReport ", CommonUtil.getCurrentTimeInMilliSec());
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured while fethcing report information", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while fethcing report information", aoExp);
			throw new ApplicationException("Error occured while fethcing report information", aoExp);
		}
		finally
		{
			try
			{
				if (null != loReportIO)
				{
					loReportIO.close();
				}
			}
			catch (IOException aoExp)
			{
				LOG_OBJECT.Error("Exception occured in report handler", aoExp);
				throw new ApplicationException("Exception occured in report handler", aoExp);
			}
		}
	}

	/** This method is used to create and render a svg file
	 * @param aoRequest HttpServletRequest object
	 * @param aoReportConfigData config data as input
	 * @param aoWriterObj JspWriter object
	 * @param asFyId Fiscal year id of the report to be rendered
	 * @param abLoadingDashboard boolean to validate whether to display chart of dashboard or detail page
	 * @param aoServletContext ServletContext object
	 * @param aoReportConfigDetails map of Report Details
	 * @return Input Stream object
	 * @throws EngineException when any EngineException occurs
	 * @throws ClassNotFoundException when any ClassNotFoundException occurs
	 * @throws InstantiationException when any InstantiationException occurs
	 * @throws IllegalAccessException  when any IllegalAccessException occurs
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 * @throws IOException when any IO Exception occurs
	 */
	private static InputStream renderReport(HttpServletRequest aoRequest,
			Map<String, Map<String, Object>> aoReportConfigData, JspWriter aoWriterObj, String asFyId,
			boolean abLoadingDashboard, ServletContext aoServletContext, Map<String, Object> aoReportConfigDetails)
			throws EngineException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			ApplicationException, IOException
	{
		InputStream loReportIO;
		Map<String, Object> loParamDetails;
		Map<String, Object> loVariableDetails;
		String lsExtentionClass;
		String lsFileName;
		String lsDataSource;
		String lsReportId;
		loParamDetails = aoReportConfigData.get(HHSR5Constants.INPUT);
		loVariableDetails = aoReportConfigData.get(HHSR5Constants.VARIABLE);
		lsExtentionClass = (String) aoReportConfigDetails.get(HHSR5Constants.CUSTOM_HANDLER);
		lsReportId = (String) aoReportConfigDetails.get(HHSR5Constants.ID);
		if (abLoadingDashboard)
		{
			lsFileName = (String) aoReportConfigDetails.get(HHSR5Constants.DASHBOARD_CHART_DESIGN_ID);
		}
		else
		{
			lsFileName = (String) aoReportConfigDetails.get(HHSR5Constants.CHART_DESIGN_ID);
		}
		lsDataSource = (String) aoReportConfigDetails.get(HHSR5Constants.DATA_SOURCE);
		IReportRunnable loReportRunnable = moIReportRunnableMap.get(lsFileName);
		loReportIO = ReportHandler.class.getResourceAsStream(msReportFolder + HHSConstants.FORWARD_SLASH + lsFileName);
		loReportRunnable = moReportEngine.openReportDesign(loReportIO);
		moIReportRunnableMap.put(lsFileName, loReportRunnable);
		IRunAndRenderTask loRunAndRenderTask = moReportEngine.createRunAndRenderTask(loReportRunnable);
		for (Map.Entry<String, Object> loEntrySet : loParamDetails.entrySet())
		{
			loRunAndRenderTask.setParameterValue(loEntrySet.getKey(), loEntrySet.getValue());
		}
		if ((lsDataSource != null && !lsDataSource.isEmpty()) && lsDataSource.equalsIgnoreCase(HHSR5Constants.SCRIPTED))
		{
			loRunAndRenderTask.getAppContext()
					.put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, aoRequest);
			for (Map.Entry<String, Object> loEntrySet : loVariableDetails.entrySet())
			{
				aoRequest.getSession().setAttribute(loEntrySet.getKey(), loEntrySet.getValue());
			}
		}
		if (aoReportConfigDetails != null && !aoReportConfigDetails.isEmpty())
		{
			for (Map.Entry<String, Object> loEntrySet : aoReportConfigDetails.entrySet())
			{
				aoRequest.setAttribute(loEntrySet.getKey(), loEntrySet.getValue());
			}
		}
		aoRequest.getSession().setAttribute(HHSR5Constants.FY_YEAR, asFyId);
		OutputStream loOutputStream = new ByteArrayOutputStream();
		moOptions.setEnableInlineStyle(true);
		moOptions.setImageHandler(new HTMLServerImageHandler());
		moOptions.setEmbeddable(true);
		moOptions.setOutputFormat(HHSR5Constants.HTML);
		moOptions.setBaseImageURL(aoRequest.getContextPath() + HHSR5Constants.IMAGES_PATH);
		moOptions.setImageDirectory(aoServletContext.getRealPath(HHSR5Constants.IMAGES_PATH));
		moOptions.setOutputStream(loOutputStream);
		loRunAndRenderTask.setRenderOption(moOptions);
		/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
		/*if (lsExtentionClass != null)
		{
			Class loClassName = Class.forName(lsExtentionClass);
			((IBirtExtension) loClassName.newInstance()).updateRenderOption(aoRequest, loRunAndRenderTask);
		}*/
		/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
		LOG_OBJECT.Error("Start   run and render Report ", CommonUtil.getCurrentTimeInMilliSec());
		loRunAndRenderTask.run();
		LOG_OBJECT.Error("End   run and render Report ", CommonUtil.getCurrentTimeInMilliSec());
		LOG_OBJECT.Error("END  renderReport ", CommonUtil.getCurrentTimeInMilliSec());
		loRunAndRenderTask.close();
		loOutputStream = loRunAndRenderTask.getRenderOption().getOutputStream();
		aoWriterObj.write(loOutputStream.toString());
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		String lsUserOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		String lsHelpText = (String) loApplicationSettingMap.get(lsReportId + HHSConstants.UNDERSCORE
				+ lsUserOrgType);
		aoWriterObj.write("<div class='graphHelp' title='More Info'><div id='helpIcon' class='graphHelpImg'>"
				+ "<a href='javascript:void(0);' id='helpIconId'></a></div><div class='graphHelpText'>" + lsHelpText
				+ "</div></div>");
		aoWriterObj.flush();
		return loReportIO;
	}

	/**
	 * This method id used while exporting report
	 * @param aoRequest request as input
	 * @param asEmitterClass emitter class as input
	 * @param asReportTemplateName Template name as input
	 * @return map as output
	 * @throws ApplicationException Exception in case of exporting file
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static Map<String, Object> exportReport(HttpServletRequest aoRequest, String asEmitterClass,
			String asReportTemplateName) throws ApplicationException
	{
		LOG_OBJECT.Error("START  exportReport ", CommonUtil.getCurrentTimeInMilliSec());
		InputStream loReportIO = null;
		Map<String, Object> loRenderOptionDetailsMap = null;
		String lsFileName = null;
		Class[] loCInputParamType = new Class[1];
		Class loEmitterCass =null;
		try
		{
			ServletContext loServletContext = aoRequest.getSession().getServletContext();
			BirtEngine.getBirtReportEngine(loServletContext);
			IReportRunnable reportRunnable = moIReportRunnableMap.get(lsFileName);
			loReportIO = ReportHandler.class.getResourceAsStream(msReportFolder + HHSConstants.FORWARD_SLASH
					+ asReportTemplateName);
			reportRunnable = moReportEngine.openReportDesign(loReportIO);
			moIReportRunnableMap.put(lsFileName, reportRunnable);
			IRunAndRenderTask loRunAndRenderTask = moReportEngine.createRunAndRenderTask(reportRunnable);
			/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			asEmitterClass = HHSUtil.checkReportEmitterClassAccessControl(asEmitterClass); // throws ApplicationException if not valid class
			loEmitterCass = Class.forName(asEmitterClass);
			loCInputParamType[0] = Class.forName(HHSR5Constants.RUN_AND_RENDER);
			loRunAndRenderTask.getAppContext()
					.put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, aoRequest);
			Method loClassMethod = loEmitterCass.getMethod(HHSR5Constants.GET_OUTPUT_STREAM, loCInputParamType);
			loRenderOptionDetailsMap = (Map<String, Object>) loClassMethod.invoke(loEmitterCass.newInstance(),
					loRunAndRenderTask);
		
			LOG_OBJECT.Debug("END  exportReport ", CommonUtil.getCurrentTimeInMilliSec());
			/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			
			
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Exception occured in report handler", aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured in report handler", aoExp);
			throw new ApplicationException("Exception occured in report handler", aoExp);
		}
		finally
		{
			try
			{
				if (null != loReportIO)
				{
					loReportIO.close();
				}
			}
			catch (IOException aoEXP)
			{
				LOG_OBJECT.Error("Exception occured in report handler", aoEXP);
				throw new ApplicationException("Exception occured in report handler", aoEXP);
			}
		}
		return loRenderOptionDetailsMap;
	}

	/**
	 * Method used to initialize report engine
	 * @param aoServletContext context as input
	 * @throws ApplicationException Exception initializing engine
	 */
	private static void init(ServletContext aoServletContext) throws ApplicationException
	{
		if (moReportEngine == null)
		{
			synchronized (moMutex)
			{
				if (moReportEngine == null)
				{
					moReportEngine = BirtEngine.getBirtReportEngine(aoServletContext);
				}
			}
		}
	}

	/**
	 * This method clean up template
	 * @param aoServletContext context as input
	 * @throws ApplicationException Exception in case of cleaning template
	 */
	private static void cleanup(ServletContext aoServletContext) throws ApplicationException
	{
		File loImageFolder = new File(aoServletContext.getRealPath(HHSR5Constants.IMAGES_PATH));
		if (loImageFolder.exists())
		{
			HHSUtil.cleanup(loImageFolder);
		}
	}
}
