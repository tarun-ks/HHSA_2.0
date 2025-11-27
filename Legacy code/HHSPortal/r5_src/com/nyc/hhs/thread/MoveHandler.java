package com.nyc.hhs.thread;

import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.PropertyLoader;

public class MoveHandler extends DocumentVaultParallelProcessHandler
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
		String lsSelectedFolderId = (String)aoChannel.getData(HHSR5Constants.MOVE_TO);
		HashMap<String, HashMap<String, Object>> loDataMap = (HashMap) aoChannel.getData(HHSR5Constants.LO_DB_MAP);
		HashMap<String, Object> loInterDataMap = (HashMap<String, Object>) loDataMap.get(HHSR5Constants.SHARED_FLAG);
		aoResponse.setRenderParameter(HHSR5Constants.HEADER_CLICK, "true");
		if(null != lsSelectedFolderId && !lsSelectedFolderId.isEmpty() && !lsSelectedFolderId.equalsIgnoreCase(HHSR5Constants.NULL)){
			aoResponse.setRenderParameter(HHSR5Constants.FOLDER_ID, lsSelectedFolderId);
		}
		if (!(Boolean) aoChannel.getData(HHSR5Constants.ENTITY_FLAG_FOR_MAX_COUNT))
		{
			aoResponse.setRenderParameter(HHSR5Constants.STRING_SELETED_FLDR, lsSelectedFolderId);
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MOVE_TRANSACTION_MESSAGE_MAX_COUNT));
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		else if (!(Boolean) aoChannel.getData(HHSR5Constants.ENTITY_COUNT_FLAG))
		{
			aoResponse.setRenderParameter(HHSR5Constants.STRING_SELETED_FLDR, lsSelectedFolderId);
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MOVE_TRANSACTION_MESSAGE_MAX_LENGTH));
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);

		}
		else if (null != loInterDataMap && (Boolean) loInterDataMap.get(HHSR5Constants.SHARED_FLAG))
		{
			aoResponse.setRenderParameter(HHSR5Constants.STRING_SELETED_FLDR, lsSelectedFolderId);
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M112));
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);

		}
		else if ((Integer) aoChannel.getData(HHSR5Constants.DB_UPDATE_COUNT) > 0)
		{
			aoResponse.setRenderParameter(HHSR5Constants.STRING_SELETED_FLDR, lsSelectedFolderId);
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M100));
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
		}
		else
		{
			aoResponse.setRenderParameter(HHSR5Constants.STRING_SELETED_FLDR, lsSelectedFolderId);
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M100));
			// change in error message of move
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
		}
	}
}