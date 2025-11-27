package com.nyc.hhs.preprocessor;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

/**
 * @author ritu.raaj
 *
 */
public interface PreprocessorApproval
{
	public String [] isAutoApprovalApplicable(P8UserSession aoUserSession,String asBudgetId, String asWobnumber, TaskDetailsBean aoTaskBean,String asTaskType)
			throws ApplicationException;
}
