package com.nyc.hhs.rule;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.handlers.BusinessRuleHandler;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class represents the Expression
 */
public class Expression
{

	private String msMethod;
	private Object moChannel;
	private String msChannelVariable;
	private String msChannelVariable2;
	private String msValue;
	private String msType;
	private boolean mbResult;
	private String msOperator;
	private String msHandlerName;
	private List<Expression> mLChildren;

	/**
	 * This method is constructor for the class which assigns the Element
	 * Attributes to properties
	 * <ul>
	 * <li>Sets all Element properties to respective Expression properties</li>
	 * </ul>
	 * 
	 * @param aoElement - an Element object
	 */
	@SuppressWarnings("unchecked")
	public Expression(Element aoElement)
	{
		this.setChannelVariable(aoElement.getAttributeValue(HHSConstants.CHANNEL_VARIABLE));
		this.setChannelVariable2(aoElement.getAttributeValue(HHSConstants.CHANNEL_VARIABLE2));
		this.setMethod(aoElement.getAttributeValue(HHSConstants.METHOD));
		this.setType(aoElement.getAttributeValue(HHSConstants.TYPE));
		this.setValue(aoElement.getAttributeValue(HHSConstants.VALUE));
		this.setOperator(aoElement.getAttributeValue(HHSConstants.OPERATOR));
		this.setHandlerName(aoElement.getAttributeValue(HHSConstants.HANDLER_NAME));
		this.mLChildren = new ArrayList<Expression>();
		List<Element> loNodeList = aoElement.getChildren();
		for (Element loCurrentNode : loNodeList)
		{
			Expression loCurrentExpression = new Expression(loCurrentNode);
			mLChildren.add(loCurrentExpression);
		}
	}

	/**
	 * This method is constructor for the class which assigns the Element
	 * Attributes to properties
	 * <ul>
	 * <li>1. Sets all Element properties to respective Expression properties</li>
	 * <li>2. Sets Channel passed as parameter</li>
	 * </ul>
	 * 
	 * @param aoElement - an Element object
	 * @param aoChannelValToCompare - an Object type object
	 * 
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public Expression(Element aoElement, Object aoChannelValToCompare) throws ApplicationException
	{
		// Check if the Element passed is not null
		if (aoElement == null)
		{
			throw new ApplicationException("Found an Expression defined improperly.");
		}
		this.setChannel(aoChannelValToCompare);
		this.setChannelVariable(aoElement.getAttributeValue(HHSConstants.CHANNEL_VARIABLE));
		this.setChannelVariable2(aoElement.getAttributeValue(HHSConstants.CHANNEL_VARIABLE2));
		this.setMethod(aoElement.getAttributeValue(HHSConstants.METHOD));
		this.setType(aoElement.getAttributeValue(HHSConstants.TYPE));
		this.setValue(aoElement.getAttributeValue(HHSConstants.VALUE));
		this.setOperator(aoElement.getAttributeValue(HHSConstants.OPERATOR));
		this.setHandlerName(aoElement.getAttributeValue(HHSConstants.HANDLER_NAME));
		this.mLChildren = new ArrayList<Expression>();

		List<Element> loNodeList = aoElement.getChildren();

		// Set the Expression's children from the hierarchy of Element
		for (Element loCurrentNode : loNodeList)
		{
			Expression loCurrentExpression = new Expression(loCurrentNode, aoChannelValToCompare);
			mLChildren.add(loCurrentExpression);
		}
	}

	/**
	 * This method Evaluates the Expression and sets the final result in
	 * Expression property
	 * <ul>
	 * <li>1. Makes recursive call for all children to evaluate till depth of
	 * the children's levels</li>
	 * 
	 * @throws ApplicationException object
	 */
	public void evaluate() throws ApplicationException
	{
		try
		{
			if (!mLChildren.isEmpty())
			{
				for (Expression loExpression : mLChildren)
				{
					// Recursive call for evaluating the Expression
					loExpression.evaluate();
				}
				// Evaluate the result for all children
				evaluateExpression(mLChildren);
			}
			else
			{
				// Evaluate the Expression individually
				evaluateExpression();
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
	}

	/**
	 * This method is used within the class for evaluating the Expression
	 * calling the respective handler for evaluation
	 * <ul>
	 * <li>1. Calls the handler on a n Expression (Individual)</li>
	 * <li>1. Sets the result of the handler to it's mbResult property</li>
	 * </ul>
	 * 
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	private void evaluateExpression() throws ApplicationException
	{
		try
		{
			// Get the handler- call the dynamically selected handler and call
			// it's evaluateBusinessRule method through reflection
			String lsHandlerClassName = (String) PropertyLoader.getProperty(HHSConstants.RULE_HANDLER_LEFT_SUB_STR,
					this.msType + HHSConstants.UNDERSCORE + this.msMethod);
			Class loClassObj = Class.forName(lsHandlerClassName);
			BusinessRuleHandler loHandler = (BusinessRuleHandler) loClassObj.newInstance();
			this.mbResult = (Boolean) loHandler.evaluateBusinessRule(this);
		}
		// Catch ApplicaitonException thrown from evaluateBusinessRule
		// method of selected handler and throw it the caller
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		// Catch ClassNotFoundException thrown when no handler class is found
		// while selecting it dynamically
		catch (ClassNotFoundException aoCNFE)
		{
			throw new ApplicationException("Class not found exception occured.", aoCNFE);
		}
		// Catch rest of all Exception which can be of any type
		catch (Exception aoExc)
		{
			throw new ApplicationException("Security exception occured.", aoExc);
		}
	}

	/**
	 * This method is used within the class for evaluating the Expression's
	 * result between two Expressions
	 * <ul>
	 * <li>1. Evaluates the final result (boolean) for OR and AND</li>
	 * </ul>
	 * 
	 * @param aoLChildren - a List<Expression> object
	 */
	private void evaluateExpression(List<Expression> aoLChildren)
	{
		boolean lbResult = false;
		if (this.msOperator != null)
		{
			// For OR condition
			if (this.msOperator.trim().equalsIgnoreCase(HHSConstants.OR))
			{
				for (Expression loExpression : aoLChildren)
				{
					lbResult = lbResult || loExpression.mbResult;
				}
			}
			// For AND condition
			else if (this.msOperator.trim().equalsIgnoreCase(HHSConstants.AND))
			{
				lbResult = true;
				for (Expression loExpression : aoLChildren)
				{
					lbResult = lbResult && loExpression.mbResult;
				}
			}
		}
		this.mbResult = lbResult;
	}

	/**
	 * @return the msChannelVariable
	 */
	public String getChannelVariable()
	{
		return msChannelVariable;
	}

	/**
	 * @param asChannelVariable the msChannelVariable to set
	 */
	public void setChannelVariable(String asChannelVariable)
	{
		this.msChannelVariable = asChannelVariable;
	}

	/**
	 * @return the msChannelVariable2
	 */
	public String getChannelVariable2()
	{
		return msChannelVariable2;
	}

	/**
	 * @param asChannelVariable the msChannelVariable2 to set
	 */
	public void setChannelVariable2(String asChannelVariable)
	{
		this.msChannelVariable2 = asChannelVariable;
	}

	/**
	 * @return the msOperator
	 */
	public String getOperator()
	{
		return msOperator;
	}

	/**
	 * @param asOperator the msOperator to set
	 */
	public void setOperator(String asOperator)
	{
		this.msOperator = asOperator;
	}

	/**
	 * @return the msMethod
	 */
	public String getMethod()
	{
		return msMethod;
	}

	/**
	 * @param asMethod the msMethod to set
	 */
	public void setMethod(String asMethod)
	{
		this.msMethod = asMethod;
	}

	/**
	 * @return the moChannel
	 */
	public Object getChannel()
	{
		return moChannel;
	}

	/**
	 * @param aoChannelValueToCompare the moChannel to set
	 */
	public void setChannel(Object aoChannelValueToCompare)
	{
		this.moChannel = aoChannelValueToCompare;
	}

	/**
	 * @return the msValue
	 */
	public String getValue()
	{
		return msValue;
	}

	/**
	 * @param asValue the msValue to set
	 */
	public void setValue(String asValue)
	{
		this.msValue = asValue;
	}

	/**
	 * @return the msType
	 */
	public String getType()
	{
		return msType;
	}

	/**
	 * @param asType the msType to set
	 */
	public void setType(String asType)
	{
		this.msType = asType;
	}

	/**
	 * @return the mbResult
	 */
	public boolean isResult()
	{
		return mbResult;
	}

	/**
	 * @param abResult the mbResult to set
	 */
	public void setResult(boolean abResult)
	{
		this.mbResult = abResult;
	}

	/**
	 * @return the msHandlerName
	 */
	public String getHandlerName()
	{
		return msHandlerName;
	}

	/**
	 * @param asHandlerName the msHandlerName to set
	 */
	public void setHandlerName(String asHandlerName)
	{
		this.msHandlerName = asHandlerName;
	}

}
