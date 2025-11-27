package com.nyc.hhs.frameworks.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.CommonUtil;

/**
 * This class helps in displaying content of the body of jsp file
 * 
 */
public class ContentDisplay extends BodyTagSupport
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ContentDisplay.class);

	private static final long serialVersionUID = 1L;
	private String section;
	private String authorize;
	private String readonlyRoles;
	private String readonlyStatuses;
	private String isReadOnly;
	private String readOnlyOrgType;
	private String readOnlyHref;

	/**
	 * This method is invoked by the JSP page implementation object It process
	 * the end tag for this instance.
	 * 
	 * @return int SKIP_BODY is the valid return value for doEndTag and
	 *         signifies that tag does not wants to process the body.
	 */
	// changed in R5
	@Override
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public int doEndTag()
	{
		try
		{
			HttpSession loSession = pageContext.getSession();
			HttpServletRequest loRequest = (HttpServletRequest) pageContext.getRequest();
			StringBuffer loStringBuffer = new StringBuffer();
			Map<String, Boolean> loRoleMappingFlag = null;
			if (section != null){
				loRoleMappingFlag = CommonUtil.getConditionalRoleDisplayMap(section, loSession);
			}
			if (section == null || (loRoleMappingFlag != null && loRoleMappingFlag.get("authorizeFlag")))
			{
				if (section != null && loRoleMappingFlag.get("readonlyFlag") && isReadOnly == null)
				{
					isReadOnly = "true";
				}
				BodyContent loBody = getBodyContent();
				String lsValue = loBody.getString();
				boolean lbReadonlyFlag = false;
				if (isReadOnly != null && isReadOnly.equalsIgnoreCase(HHSConstants.TRUE))
				{
					lsValue = applyReadonly(lsValue);
					lbReadonlyFlag = true;
				}
				else if (readonlyRoles != null)
				{
					StringTokenizer loToken = new StringTokenizer(readonlyRoles, HHSConstants.COMMA);
					StringTokenizer loOrgToken = new StringTokenizer(readOnlyOrgType, HHSConstants.COMMA);
					List loOrgList = new ArrayList();
					while (loOrgToken.hasMoreTokens())
					{
						String lsOrgtokenVal = loOrgToken.nextToken().trim();
						loOrgList.add(lsOrgtokenVal.toLowerCase());
					}
					String lsUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE);
					String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
					while (loToken.hasMoreTokens())
					{
						String lsTokenVal = loToken.nextToken().trim();
						if (lsUserRole.equalsIgnoreCase(lsTokenVal) && loOrgList.contains(lsOrgType.toLowerCase()))
						{
							lbReadonlyFlag = true;
							break;
						}
					}
					if (lbReadonlyFlag)
					{
						lsValue = applyReadonly(lsValue);
					}
				}
				if (readonlyStatuses != null && !lbReadonlyFlag)
				{
					StringTokenizer lsToken = new StringTokenizer(readonlyStatuses, HHSConstants.COMMA);
					String lsStatus = String.valueOf(loRequest.getAttribute(HHSConstants.STATUS));
					if (lsStatus != null)
					{
						while (lsToken.hasMoreTokens())
						{
							String lsVal = lsToken.nextToken().trim();
							if (lsStatus.equalsIgnoreCase(lsVal))
							{
								lbReadonlyFlag = true;
								break;
							}
						}
						if (lbReadonlyFlag)
						{
							lsValue = applyReadonly(lsValue);
						}
					}
				}
				JspWriter loOut = loBody.getEnclosingWriter();
				loOut.print(lsValue.trim());
			}
			else
			{
				if (authorize != null)
				{
					loStringBuffer.append(HHSConstants.NOT_AUTHORIZE);
					JspWriter loOut = pageContext.getOut();
					loOut.print(loStringBuffer.toString());
				}
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error in generating content", aoExp);
		}
		return (SKIP_BODY);
	}

	/**
	 * This method converts form fields into readonly
	 * @param asValue values with editable attributes
	 * @return String with readonly attributes
	 */
	private String applyReadonly(String asValue)
	{
		asValue = asValue.replaceAll("editable : true", "editable : false");
		asValue = asValue.replaceAll("del:true", "del:false");
		asValue = asValue.replaceAll("add:true", "add:false");
		asValue = asValue.replaceAll("cancel:true", "cancel:false");
		asValue = asValue.replaceAll("save:true", "save:false");
		asValue = asValue.replaceAll("edit:true", "edit:false");
		asValue = asValue.replaceAll("readonly=\"readonly\"", "");
		asValue = asValue.replaceAll("isGridReadOnly='false'", "isGridReadOnly='true'");
		asValue = asValue.replaceAll("disabled=\"disabled\"", "");
		asValue = asValue.replaceAll("disabled=\"true\"", "");
		asValue = asValue.replaceAll("form:select", "form:select disabled=\"true\"");
		asValue = asValue.replaceAll("<select", "<select disabled=\"true\"");
		if (readOnlyHref != null && HHSConstants.TRUE.equalsIgnoreCase(readOnlyHref))
		{
			asValue = asValue.replaceAll("<a", "<a onclick=\"return false;\" disabled=\"true\"");
			asValue = asValue.replaceAll("<A", "<A onclick=\"return false;\" disabled=\"true\"");

			asValue = asValue.replaceAll("<li", "<li onclick=\"return false;\" disabled=\"true\"");
			asValue = asValue.replaceAll("<LI", "<LI onclick=\"return false;\" disabled=\"true\"");
		}

		asValue = asValue
				.replaceAll("class=\"imgclassPlanned\"", "class=\"imgclassPlanned\" onclick=\"return false;\"");
		asValue = asValue.replaceAll("form:input", "form:input readonly=\"readonly\"");
		asValue = asValue.replaceAll("form:textarea", "form:textarea readonly=\"readonly\"");
		asValue = asValue.replaceAll("form:checkbox", "form:checkbox disabled=\"true\"");
		asValue = asValue.replaceAll("type=\"text\"", "type=\"text\" readonly=\"readonly\"");
		asValue = asValue.replaceAll("type=\"password\"", "type=\"password\" readonly=\"readonly\"");
		asValue = asValue.replaceAll("type=\"checkbox\"", "type=\"checkbox\" disabled=\"true\"");
		asValue = asValue.replaceAll("<textarea", "<textarea readonly=\"readonly\"");
		asValue = asValue.replaceAll("type=\"button\"", "type=\"button\" disabled=\"true\"");
		asValue = asValue.replaceAll("type=\"submit\"", "type=\"submit\" disabled=\"true\"");
		asValue = asValue.replaceAll("<option>Remove Document</option>", "");
		asValue = asValue.replaceAll("<option>Delete Document</option>", "");
		asValue = asValue
				.replaceAll("<select disabled=\"true\" name=documentDropdown", "<select name=documentDropdown");
		/* Start : R5 Added, for radio button readonly */
		asValue = asValue.replaceAll("type=\"radio\"", "type=\"radio\" disabled=\"true\"");
		/* End : R5 Added */
		return asValue;
	}

	/**
	 * @return the section
	 */
	public String getSection()
	{
		return section;
	}

	/**
	 * @param section the section to set
	 */
	public void setSection(String section)
	{
		this.section = section;
	}

	/**
	 * @return the authorize
	 */
	public String getAuthorize()
	{
		return authorize;
	}

	/**
	 * @param authorize the authorize to set
	 */
	public void setAuthorize(String authorize)
	{
		this.authorize = authorize;
	}

	/**
	 * @return the readonlyRoles
	 */
	public String getReadonlyRoles()
	{
		return readonlyRoles;
	}

	/**
	 * @param readonlyRoles the readonlyRoles to set
	 */
	public void setReadonlyRoles(String readonlyRoles)
	{
		this.readonlyRoles = readonlyRoles;
	}

	/**
	 * @return the readonlyStatuses
	 */
	public String getReadonlyStatuses()
	{
		return readonlyStatuses;
	}

	/**
	 * @param readonlyStatuses the readonlyStatuses to set
	 */
	public void setReadonlyStatuses(String readonlyStatuses)
	{
		this.readonlyStatuses = readonlyStatuses;
	}

	/**
	 * @return the isReadOnly
	 */
	public String getIsReadOnly()
	{
		return isReadOnly;
	}

	/**
	 * @param isReadOnly the isReadOnly to set
	 */
	public void setIsReadOnly(String isReadOnly)
	{
		this.isReadOnly = isReadOnly;
	}

	/**
	 * @return the readOnlyOrgType
	 */
	public String getReadOnlyOrgType()
	{
		return readOnlyOrgType;
	}

	/**
	 * @param readOnlyOrgType the readOnlyOrgType to set
	 */
	public void setReadOnlyOrgType(String readOnlyOrgType)
	{
		this.readOnlyOrgType = readOnlyOrgType;
	}

	/**
	 * @return the readOnlyHref
	 */
	public String getReadOnlyHref()
	{
		return readOnlyHref;
	}

	/**
	 * @param readOnlyHref the readOnlyHref to set
	 */
	public void setReadOnlyHref(String readOnlyHref)
	{
		this.readOnlyHref = readOnlyHref;
	}

}
