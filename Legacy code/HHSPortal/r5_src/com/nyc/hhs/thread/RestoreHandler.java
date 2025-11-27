package com.nyc.hhs.thread;

import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.PropertyLoader;

public class RestoreHandler extends DocumentVaultParallelProcessHandler
{
	/**
	 * This method is post Process Handler
	 * 
	 * @param Channel aoChannel
	 * @param ActionRequest aoRequest
	 * @param ActionResponse aoResponse
	 * @throws ApplicationException If an Exception occurs
	 */
	@Override
	public void postProcessHandler(Channel aoChannel, ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		HashMap<String, List> loReturnMap = (HashMap) aoChannel.getData(HHSR5Constants.LO_DB_MAP);
		Boolean loMaxCountCheck = (Boolean) aoChannel.getData("lbEntityFlag");
		if (!loMaxCountCheck)
		{
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.RESTORE_MESSAGE_MAX_COUNT));
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		else
		{
			List<Integer> loReturnList = (List<Integer>) loReturnMap.get(HHSConstants.STATUS_LIST);
			Integer loCount = loReturnList.get(0);
			if (loCount > 0)
			{
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE,
						PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.RESTORE_SUCCESS)
								+ " "
								+ loCount
								+ " "
								+ PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
										HHSR5Constants.RENAMED_DOC));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_PASS_TYPE);
			}
			else
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
						PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.RESTORE_SUCCESS));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		aoResponse.setRenderParameter(HHSR5Constants.JSP_NAME, HHSR5Constants.RECYCLE_BIN_JSP_NAME);
	}
}