/**
 * 
 */
package com.nyc.hhs.controllers.util;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * This util class will be used to for Controllers. All decision making or
 * control flow is executed here.
 * </p>
 * 
 */
public class ControllerUtils
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ControllerUtils.class);

	/**This method adds the assignee to DB
	 * <li>This transaction used: validateAssignee</li>
	 *  <li>This transaction used: addAssigneeForBudget</li>
	 * @param aoChannel
	 * @return
	 * @@throws ApplicationException Application Exception
	 */
	public static String addAssignee(Channel aoChannel) throws ApplicationException
	{
		String lsMessage = HHSConstants.EMPTY_STRING;
		boolean lbValid = false;
		// Validate Assignee
		try
		{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.VALIDATE_ASSIGNEE);

			lbValid = (Boolean) aoChannel.getData(HHSConstants.S431_CHANNEL_KEY_LB_VALID);
			if (lbValid)
			{
				// Validation successful: Assignee does not exit, Add new
				HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.INSERT_ASSIGNEE_FOR_BUDGET);
			}
			else
			{
				lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MSG_KEY_DUPLICATE_ASSIGNEE_NOT_ADDED);

			}
		}
		catch (ApplicationException loException)
		{
			lsMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException Occured in addAssignee while adding Assignee to the database",
					loException);
			throw loException;

		}
		// handling exception other than Application Exception.
		catch (Exception loException)
		{
			lsMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception Occured in addAssignee while adding Assignee to the database", loException);
		}

		return lsMessage;

	}

}
