package com.nyc.hhs.report.framework.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;


/**
 * This class is used to get output stream for run and render option
 */
public abstract class HHSExportReportDetails
{
	public abstract Map<String, Object> getReportRunnableData() throws ApplicationException;

	private static final LogInfo LOG_OBJECT = new LogInfo(HHSExportReportDetails.class);

	/**
	 * This method will get output stream for run and render option.
	 * @param aoRunAndRenderTask run and render task as input
	 * @return Map of object with export content information
	 * @throws ApplicationException Exception in case of code failure.
	 */
	public Map<String, Object> getOutputStream(IRunAndRenderTask aoRunAndRenderTask) throws ApplicationException
	{
		Map<String, Object> loMap = new HashMap<String, Object>();
		try
		{
			loMap = this.getReportRunnableData();
			aoRunAndRenderTask.setEmitterID((String) loMap.get(HHSR5Constants.EMITTER_ID));
			aoRunAndRenderTask.setRenderOption((IRenderOption) loMap.get(HHSR5Constants.RENDER_OPTION));
			aoRunAndRenderTask.run();
			aoRunAndRenderTask.close();
			loMap.put(HHSR5Constants.EXPORT_CONTENT, aoRunAndRenderTask.getRenderOption().getOutputStream());
		}
		catch (EngineException aoExp)
		{
			LOG_OBJECT.Error("Exception occured while getting output stream information", aoExp);
			throw new ApplicationException("Error Occured while getting the report content", aoExp);
		}
		return loMap;
	}
}
