package com.nyc.hhs.model;

import java.util.List;

/**
 * This class is used to persist the data for taxonomy parent child relationship
 * 
 */

public class TaxonomyParentChild {
	
	private String msElamentId;
	
	private String msElementName;
	
	private String msElementDescription;
	
	private List<TaxonomyParentChild> childList;
	
	public String getMsElamentId() {
		return msElamentId;
	}
	
	public void setMsElamentId(String msElamentId) {
		this.msElamentId = msElamentId;
	}
	
	public String getMsElementName() {
		return msElementName;
	}
	
	public void setMsElementName(String msElementName) {
		this.msElementName = msElementName;
	}
	
	public String getMsElementDescription() {
		return msElementDescription;
	}
	
	public void setMsElementDescription(String msElementDescription) {
		this.msElementDescription = msElementDescription;
	}
	
	public List<TaxonomyParentChild> getChildList() {
		return childList;
	}
	
	public void setChildList(List<TaxonomyParentChild> childList) {
		this.childList = childList;
	}

	@Override
	public String toString() {
		return "TaxonomyParentChild [msElamentId=" + msElamentId
				+ ", msElementName=" + msElementName
				+ ", msElementDescription=" + msElementDescription
				+ ", childList=" + childList + "]";
	}
}
