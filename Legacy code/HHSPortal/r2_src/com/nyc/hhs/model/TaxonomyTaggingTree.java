package com.nyc.hhs.model;

import java.util.List;

/**
 * This class is a bean which maintains the Taxonomy tree information.
 * 
 */

public class TaxonomyTaggingTree extends TaxonomyTree
{
	private List<TaxonomyModifiersBean> modifiersList;
	private String taxonomyTaggingId;

	/**
	 * @return the modifiersList
	 */
	public List<TaxonomyModifiersBean> getModifiersList()
	{
		return modifiersList;
	}

	/**
	 * @param modifiersList the modifiersList to set
	 */
	public void setModifiersList(List<TaxonomyModifiersBean> modifiersList)
	{
		this.modifiersList = modifiersList;
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

	@Override
	public String toString()
	{
		return "TaxonomyTaggingTree [modifiersList=" + modifiersList + ", taxonomyTaggingId=" + taxonomyTaggingId + "]";
	}
}
