package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains the Taxonomy Modifiers information.
 * 
 */

public class TaxonomyModifiersBean
{

	@Length(max = 50)
	private String elementId;
	@RegExp(value ="^\\d{0,22}")
	private String taxonomyTaggingId;
	private String taxonomyModifiersId;
	private String modifyBy;
	private String createdBy;

	/**
	 * @return the elementId
	 */
	public String getElementId()
	{
		return elementId;
	}

	/**
	 * @param elementId the elementId to set
	 */
	public void setElementId(String elementId)
	{
		this.elementId = elementId;
	}

	/**
	 * @return the taxonomyTaggingId
	 */
	public String getTaxonomyTaggingId()
	{
		return taxonomyTaggingId;
	}

	/**
	 * @param taxonomyTaggingId the taxonomyTaggingId to set
	 */
	public void setTaxonomyTaggingId(String taxonomyTaggingId)
	{
		this.taxonomyTaggingId = taxonomyTaggingId;
	}

	/**
	 * @return the taxonomyModifiersId
	 */
	public String getTaxonomyModifiersId()
	{
		return taxonomyModifiersId;
	}

	/**
	 * @param taxonomyModifiersId the taxonomyModifiersId to set
	 */
	public void setTaxonomyModifiersId(String taxonomyModifiersId)
	{
		this.taxonomyModifiersId = taxonomyModifiersId;
	}

	/**
	 * @return the modifyBy
	 */
	public String getModifyBy()
	{
		return modifyBy;
	}

	/**
	 * @param modifyBy the modifyBy to set
	 */
	public void setModifyBy(String modifyBy)
	{
		this.modifyBy = modifyBy;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	@Override
	public String toString()
	{
		return "TaxonomyModifiersBean [elementId=" + elementId + ", taxonomyTaggingId=" + taxonomyTaggingId
				+ ", taxonomyModifiersId=" + taxonomyModifiersId + ", modifyBy=" + modifyBy + ", createdBy="
				+ createdBy + "]";
	}
}
