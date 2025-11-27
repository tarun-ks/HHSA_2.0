package com.nyc.hhs.rule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;

/**
 * This class provides the Rule evaluation through Tags in JSP (for conditional
 * include based on the rule result)
 */
public class RuleTag extends BodyTagSupport
{

	private static final long serialVersionUID = -4170571813579802642L;
	private static final LogInfo LOG_OBJECT = new LogInfo(RuleTag.class);
	private String ruleId;
	private String requestAttName = HHSConstants.CHANNEL;

	@Override
	public int doStartTag() throws JspException
	{
		boolean lbResult = false;
		try
		{
			HttpServletRequest loReq = (HttpServletRequest) pageContext.getRequest();
			Channel loChannel = (Channel) loReq.getAttribute(requestAttName);
			lbResult = (Boolean) Rule.evaluateRule(ruleId, loChannel, true);
		}
		// Catch Exception of ApplicatinException type thrown from evaluateRule
		// method
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error in excecuting Rule Tag", aoAppEx);
		}
		if (lbResult)
		{
			return (EVAL_BODY_INCLUDE);
		}
		else
		{
			return (SKIP_BODY);
		}
	}

	/**
	 * @param asRuleId - the ruleId to set
	 */
	public void setRuleId(String asRuleId)
	{
		this.ruleId = asRuleId;
	}

	/**
	 * @param asRequestAttName - the msAequestAttName to set
	 */
	public void setRequestAttName(String asRequestAttName)
	{
		this.requestAttName = asRequestAttName;
	}
}