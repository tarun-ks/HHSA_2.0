package com.nyc.hhs.thread;

import java.util.List;

import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.util.FileNetOperationsUtils;

public class DocumentVaultParallelProcessor implements Runnable
{
	private Channel channel;
	private String userOrg, userOrgType, transactionName;
	private List<String> lockPathList;
	private static final LogInfo LOG_OBJECT = new LogInfo(DocumentVaultParallelProcessor.class);

	public Channel getChannel()
	{
		return channel;
	}
	/**
	 * This method is used for Parallel Processes in Document Vault
	 * 
	 * @param Channel aoChannel
	 * @param List<String> aoLockPathList
	 * @param String asUserOrg
	 * @param String asTransactionName
	 * @param String asUserOrgType
	 * @throws ApplicationException If an Exception occurs
	 */
	public DocumentVaultParallelProcessor(Channel aoChannel, List<String> aoLockPathList, String asUserOrg,
			String asUserOrgType, String asTransactionName)
	{
		channel = aoChannel;
		userOrg = asUserOrg;
		userOrgType = asUserOrgType;
		lockPathList = aoLockPathList;
		transactionName = asTransactionName;
	}

	@Override
	public void run()
	{
		try
		{
			HHSTransactionManager.executeTransaction(channel, transactionName, HHSR5Constants.TRANSACTION_ELEMENT_R5);
			channel.setData("processComplete", true);
		}
		catch (ApplicationException aoEx)
		{
			channel.setData("processComplete", false);
			channel.setData("exception", aoEx);
		}
		catch (Exception aoEx)
		{
			channel.setData("processComplete", false);
			channel.setData("exception", aoEx);
		}
		finally
		{
			try
			{
				FileNetOperationsUtils.removeLock(lockPathList, userOrg, userOrgType);
			}
			catch (ApplicationException aoEx)
			{
				LOG_OBJECT.Error("Exception occured in remove lock", aoEx);
			}
		}
	}
}