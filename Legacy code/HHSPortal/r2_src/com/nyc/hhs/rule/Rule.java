package com.nyc.hhs.rule;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.handlers.ruleresulthandlers.BusinessRuleResultHandler;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * <p>
 * This class will serve as a utility for Business Rule application across all
 * the modules.
 * </p>
 */
public class Rule
{

	private static final String MSBUSINESSRULEPATH = HHSConstants.BUSINESS_RULE_XML_PATH;;
	private static final String MSRULESETPATH = HHSConstants.RULE_SET_XML_PATH;

	/**
	 * This method evaluates a result block (if or else) of ruleset.
	 * <ul>
	 * <li>Calls evaluateRule() if AltRuleSet defined for result block specified
	 * </li>
	 * <li>Calls formatRuleResult() for formatting result into specified format
	 * in ruleset configuration</li>
	 * </ul>
	 * 
	 * @param asIfElseEle - String (either "if" or "else")
	 * @param aoRuleSet - an Element type object - if/else object block of a
	 *            ruleset
	 * @param aoChannelValToCompare - channel object
	 * @return Object type object (formatted object as per ruleset result
	 *         (if/else) configuration)
	 * @throws ApplicationException object
	 */
	private static Object evaluateResultBlock(String asIfElseEle, Element aoRuleSet, Object aoChannelValToCompare)
			throws ApplicationException
	{
		String loAltRuleSet = HHSConstants.EMPTY_STRING;
		if (aoRuleSet.getChild(asIfElseEle) != null)
		{
			Element loRuleSetEle = aoRuleSet.getChild(asIfElseEle);
			if (null != loRuleSetEle.getAttributeValue(HHSConstants.ALT_RULE_SET))
			{
				loAltRuleSet = loRuleSetEle.getAttributeValue(HHSConstants.ALT_RULE_SET);
				if (!StringUtils.isBlank(loAltRuleSet))
				{
					return evaluateRule(loAltRuleSet, aoChannelValToCompare);
				}
			}
			if (null != loRuleSetEle.getAttributeValue(HHSConstants.VALUE))
			{
				return formatRuleResult(loRuleSetEle);
			}
			throw new ApplicationException("Could not locate 'value' attribute in " + asIfElseEle
					+ " child in ruleset - " + aoRuleSet.getAttributeValue(HHSConstants.ID));
		}
		throw new ApplicationException("Could not locate " + asIfElseEle + " child in ruleset - "
				+ aoRuleSet.getAttributeValue(HHSConstants.ID));
	}

	/**
	 * This method returns the final result of evaluated ruleset.
	 * <ul>
	 * <li>Calls appropriate Result Block of the ruleset depending upon the
	 * Expression result</li>
	 * <li>Evaluates 'if' block of ruleset if Expression returns true (calls
	 * evaluateResultBlock())</li>
	 * <li>Evaluates 'else' block ruleset if Expression returns false (calls
	 * evaluateResultBlock())</li>
	 * </ul>
	 * 
	 * @param abRulesResult - boolean type variable containing Expression result
	 * @param aoRuleSet - an Element type object - if/else object block of a
	 *            ruleset
	 * @param aoChannelValToCompare - channel object
	 * @return Object type object (formatted object as per ruleset result
	 *         (if/else) configuration)
	 * @throws ApplicationException object
	 */
	private static Object getEvaluationResult(boolean abRulesResult, Element aoRuleSet, Object aoChannelValToCompare)
			throws ApplicationException
	{

		if (abRulesResult)
		{
			return evaluateResultBlock(HHSConstants.IF, aoRuleSet, aoChannelValToCompare);
		}
		return evaluateResultBlock(HHSConstants.ELSE, aoRuleSet, aoChannelValToCompare);

	}

	/**
	 * This method will format the result of evaluated result of business rule
	 * set and returns an Object type object.
	 * <ul>
	 * <li>Evaluates RuleSet identified by unique id</li>
	 * <li>Evaluates all business rules listed in the Rule Set</li>
	 * <li>Returns the custom result (ArrayList, String, Integer) depending upon
	 * the result expectation configuration</li>
	 * </ul>
	 * 
	 * @param aoRuleSetChildEle - an Element type object - if/else object block
	 *            of a ruleset
	 * @return Object type object (formatted object as per ruleset result
	 *         (if/else) configuration)
	 * @throws ApplicationException object
	 */
	private static Object formatRuleResult(Element aoRuleSetChildEle) throws ApplicationException
	{

		Object loResult = null;

		try
		{
			// Extract Return Type expected
			String lsReturnType = aoRuleSetChildEle.getAttributeValue(HHSConstants.RETURN);

			// check if return type expected is neither null nor blank string
			if (null != lsReturnType && !(StringUtils.isBlank(lsReturnType)))
			{
				String lsType = aoRuleSetChildEle.getAttributeValue(HHSConstants.TYPE);
				if (null == lsType || StringUtils.isBlank(lsType))
				{
					lsType = HHSConstants.STRING;
				}
				String lsSeparator = aoRuleSetChildEle.getAttributeValue(HHSConstants.SEPARATOR);
				// Set the operator ',' if no any operator is defined yet
				if (lsSeparator == null || lsSeparator.isEmpty())
				{
					lsSeparator = HHSConstants.COMMA;
				}

				String lsHandlerClassName = (String) PropertyLoader.getProperty(
						HHSConstants.RULE_RESULT_FORMAT_HANDLER_LEFT_SUB_STR, lsReturnType + HHSConstants.UNDERSCORE
								+ lsType);
				Class loClassObj = Class.forName(lsHandlerClassName);
				BusinessRuleResultHandler loHandler = (BusinessRuleResultHandler) loClassObj.newInstance();
				loResult = loHandler.formatRuleResult(lsSeparator,
						aoRuleSetChildEle.getAttributeValue(HHSConstants.VALUE));
			}
			// check if return type expected is either null or blank string
			// return the value block
			else
			{
				loResult = aoRuleSetChildEle.getAttributeValue(HHSConstants.VALUE);
			}
		}
		// Catch ApplicaitonException thrown from evaluateBusinessRule
		// method of selected handler and throw it the caller
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		// Catch ClassNotFoundException thrown when no handler class is found
		// while selecting it dynamically
		catch (ClassNotFoundException aoCNFEx)
		{
			throw new ApplicationException("Class not found exception occured.", aoCNFEx);
		}
		// Catch rest of all Exception which can be of any type
		catch (Exception aoExc)
		{
			throw new ApplicationException("Security exception occured.", aoExc);
		}
		return loResult;
	}

	/**
	 * This method will evaluate the business rule set and return an Object type
	 * object.
	 * <ul>
	 * <li>Evaluates RuleSet identified by unique id</li>
	 * <li>Evaluates all business rules listed in the Rule Set</li>
	 * <li>Returns the custom result (ArrayList, String, Integer) depending upon
	 * the result expectation configuration</li>
	 * </ul>
	 * 
	 * @param asRuleId - a String object - unique 'id' attribute of ruleset.xml
	 * @param aoChannelValToCompare - channel object
	 * @return Object type object (can be type casted by the caller depending
	 *         upon the need.)
	 * @throws ApplicationException object
	 */
	public static Object evaluateRule(String asRuleId, Object aoChannelValToCompare) throws ApplicationException
	{
		return evaluateRule(asRuleId, aoChannelValToCompare, false);
	}

	/**
	 * This method will evaluate the business rule set and return an Object type
	 * object. Basically overridden for TLD usage
	 * <ul>
	 * <li>Evaluates RuleSet identified by unique id</li>
	 * <li>Evaluates all business rules listed in the Rule Set</li>
	 * <li>Returns the custom result (ArrayList, String, Integer) depending upon
	 * the result expectation configuration</li>
	 * </ul>
	 * 
	 * @param asRuleId - a String object - unique 'id' attribute of ruleset.xml
	 * @param aoChannelValToCompare - channel object
	 * @param abForTLDFlag - Flag for whether used for TLD - true for TLD usage
	 * @return Object type object (can be type casted by the caller depending
	 *         upon the need.)
	 * @throws ApplicationException object
	 */
	public static Object evaluateRule(String asRuleId, Object aoChannelValToCompare, boolean abForTLDFlag)
			throws ApplicationException
	{
		boolean lbRulesResult = false;
		try
		{
			// Set Document object for business-rules.xml - for business rules
			Document loBusinessRuleDom = XMLUtil.getDomObj(Rule.class.getResourceAsStream(MSBUSINESSRULEPATH));
			Element loRootElement = loBusinessRuleDom.getRootElement();
			// Set Document object for ruleset.xml - for business rules
			Document loRuleSetDom = XMLUtil.getDomObj(Rule.class.getResourceAsStream(MSRULESETPATH));
			String lsXPath = HHSConstants.RULE_SET_XPATH_STR_LEFT_PART + asRuleId
					+ HHSConstants.RULE_SET_XPATH_STR_RIGHT_PART;
			Element loRuleSet = XMLUtil.getElement(lsXPath, loRuleSetDom);
			// Throw ApplicationException if no Rule-set found with the provided
			// name in the ruleset.xml
			if (loRuleSet == null)
			{
				throw new ApplicationException("Could not locate ruleset - " + asRuleId);
			}
			List<Boolean> loAllResult = new ArrayList<Boolean>();
			String lsRulesToEvaluate = null;
			String lsOperator = HHSConstants.AND;
			lsRulesToEvaluate = loRuleSet.getAttributeValue(HHSConstants.RULES);
			if (loRuleSet.getAttributeValue(HHSConstants.OPERATOR) != null)
			{
				lsOperator = loRuleSet.getAttributeValue(HHSConstants.OPERATOR);
			}
			for (String lsRuleId : lsRulesToEvaluate.split(HHSConstants.COMMA))
			{
				String lsXpath = HHSConstants.RULE_XPATH_STR_LEFT_PART + lsRuleId.trim()
						+ HHSConstants.RULE_SET_XPATH_STR_RIGHT_PART;
				Element loFormEle = XMLUtil.getElement(lsXpath, loRootElement);
				if (loFormEle == null)
				{
					throw new ApplicationException("Could not locate rule - " + lsRuleId);
				}
				Element loRootExpression = loFormEle.getChild(HHSConstants.EXPRESSION);
				if (loRootExpression.getName().equals(HHSConstants.EXPRESSION))
				{
					Expression loExpression = new Expression(loRootExpression, aoChannelValToCompare);
					loExpression.evaluate();
					loAllResult.add(loExpression.isResult());
				}
			}
			if (lsOperator.equals(HHSConstants.AND))
			{
				for (boolean lbExpResult : loAllResult)
				{
					if (lbExpResult)
					{
						lbRulesResult = lbExpResult;
					}
					else
					{
						lbRulesResult = false;
						break;
					}
				}
			}
			else
			{
				for (boolean lbExpResult : loAllResult)
				{
					if (lbExpResult)
					{
						lbRulesResult = lbExpResult;
						break;
					}
				}
			}
			if (!abForTLDFlag)
			{
				return getEvaluationResult(lbRulesResult, loRuleSet, aoChannelValToCompare);
			}
			else
			{
				return lbRulesResult;
			}
		}
		// Catch the ApplicationException - could be thrown from
		// getEvaluationResult, evaluate or if no rule set is defined
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		// Catch the rest of all Exceptions
		catch (Exception aoEx)
		{
			throw new ApplicationException("An Exception occured in Rule: evaluateRule() methd", aoEx);
		}
	}
}
