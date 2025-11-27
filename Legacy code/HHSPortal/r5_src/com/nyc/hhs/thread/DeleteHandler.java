package com.nyc.hhs.thread;

import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.PropertyLoader;

public class DeleteHandler extends DocumentVaultParallelProcessHandler
{

	@Override
	public void postProcessHandler(Channel aoChannel, ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{

		HashMap<String, List<String>> loMessageMap = (HashMap) aoChannel.getData("loEntityMap");
		String lsSelectedFolderId = (String)aoChannel.getData("presentFolderId");
		if(null != lsSelectedFolderId && !lsSelectedFolderId.isEmpty() && !lsSelectedFolderId.equalsIgnoreCase(HHSR5Constants.NULL)){
			aoResponse.setRenderParameter(HHSR5Constants.FOLDER_ID, lsSelectedFolderId);
		}
		if (null != loMessageMap && !loMessageMap.isEmpty())
		{
			List<String> loMessageList = loMessageMap.get("lsMessageList");
			if (null != loMessageList && !loMessageList.isEmpty())
			{
				String lsMessage = loMessageList.get(0);
				String lsMessageType = loMessageList.get(1);
				if (null != lsMessage)
				{
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsMessage);
					aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE, lsMessageType);
				}
			}
		}
		if (!(Boolean) aoChannel.getData(HHSR5Constants.ENTITY_FLAG_FOR_MAX_COUNT))
		{
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.DELETE_TRANSACTION_MESSAGE_MAX_COUNT));
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		aoResponse.setRenderParameter(HHSR5Constants.HEADER_CLICK, "true");
	}
}