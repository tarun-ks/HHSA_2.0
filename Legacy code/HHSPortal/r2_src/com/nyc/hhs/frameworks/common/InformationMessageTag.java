package com.nyc.hhs.frameworks.common;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.ContractList;

/**
 * This class is added in R7. This class will generate the tld for contract
 * level message
 * @author amit.kumar.bansal
 * 
 */
public class InformationMessageTag extends TagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(InformationMessageTag.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String contractId;

	public String getContractId()
	{
		return contractId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	@Override
	public int doStartTag() throws JspException
	{
		LOG_OBJECT.Info("Entered in Information Message TLD" + getContractId());

		JspWriter loWriterObj = pageContext.getOut();
		try
		{
			ContractList loContractList = new ContractList();
			loContractList = fetchFlagContractMessage(getContractId());
			StringBuffer loStringBuffer = new StringBuffer();
			if (null != loContractList)
			{
				String message = loContractList.getContractMessage();
				String lsflaggedBy = loContractList.getModifyBy();
				if (message != null && !message.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
				{
					loStringBuffer.append("<div  class='clear' > </div>");
	                loStringBuffer.append("<div id='contractMessage' class='flagInfoMessage' style='display:block;margin: 2px 0px'>");
	                loStringBuffer.append("<table style='width:100%'> <tr> <td class='flagIconR7' style='width:96%'>   </td>");
	                
	                loStringBuffer.append("<td style='width:98%' >"
	                            + "This contract has been flagged by " + loContractList.getModifyBy()
	                            + " for the following reason(s): " + loContractList.getContractMessage() + "</td> </tr></table> </div>");
	                loStringBuffer.append("<div  class='clear' > </div>");

				}
			}
			loWriterObj.write(loStringBuffer.toString());
		}
		catch (ApplicationException apEx)
		{
			LOG_OBJECT.Error("Application Exception in doStartTag" + apEx);

		}
		catch (Exception e)
		{
			LOG_OBJECT.Error("Exception in doStartTag" + e);
		}
		return SKIP_BODY;
	}

	/**
	 * This method is added in R7. This method will fetch the contract message
	 * for the input contract Id.
	 * @param asContractId
	 * @return
	 * @throws ApplicationException
	 */
	public ContractList fetchFlagContractMessage(String asContractId) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetchFlagContractMessage with contract Id" + asContractId);
		ContractList loContractList = new ContractList();
		Channel loChannel = new Channel();
		try
		{
			if (null != asContractId && !asContractId.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
			{
				loChannel.setData(HHSConstants.CONTRACT_ID, asContractId);
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_FLAG_CONTRACT_MESSAGE,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				loContractList = (ContractList) loChannel.getData(HHSR5Constants.CONTRACT_MEESAGE_BEAN);
			}
		}
		catch (ApplicationException apEx)
		{
			LOG_OBJECT.Error("Application Exception in fetchFlagContractMessage with contract Id" + asContractId);
			throw apEx;
		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception in fetchFlagContractMessage with contract Id" + asContractId);
			throw new ApplicationException("Exception occured while fetching details:" + ex);
		}
		return loContractList;
	}
}
