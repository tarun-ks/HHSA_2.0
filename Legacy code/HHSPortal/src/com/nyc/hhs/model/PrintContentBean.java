package com.nyc.hhs.model;

import java.util.List;
import java.util.Map;

/**
 * This class is a bean which maintains the printer content information.
 *
 */

public class PrintContentBean {
	private Map<String, Object> moFormContent;
	private List<DocumentBean> moDocContent;
	
	public Map<String, Object> getMoFormContent() {
		return moFormContent;
	}
	public void setMoFormContent(Map<String, Object> moFormContent) {
		this.moFormContent = moFormContent;
	}
	public List<DocumentBean> getMoDocContent() {
		return moDocContent;
	}
	public void setMoDocContent(List<DocumentBean> moDocContent) {
		this.moDocContent = moDocContent;
	}
	@Override
	public String toString() {
		return "PrintContentBean [moFormContent=" + moFormContent
				+ ", moDocContent=" + moDocContent + "]";
	}
}
