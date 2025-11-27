package com.nyc.hhs.taskhandlers;

import java.util.Map;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.TaskDetailsBean;

/**
 * This MainTaskHandler will extend all Task Handlers and provide the abstract
 * method for the handlers
 * 
 */
public abstract class MainTaskHandler
{
	public abstract Map taskApprove(TaskDetailsBean loTaskDetailsBean) throws ApplicationException;

	public abstract Map taskReturn(TaskDetailsBean loTaskDetailsBean) throws ApplicationException;

}
