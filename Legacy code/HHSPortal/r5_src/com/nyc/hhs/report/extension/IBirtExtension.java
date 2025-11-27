package com.nyc.hhs.report.extension;

//This is a extension file added for Release 5
import javax.servlet.http.HttpServletRequest;

import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

public interface IBirtExtension
{
	public void updateRenderOption(HttpServletRequest aoRequest, IRunAndRenderTask aoRunAndRenderTask);
}
