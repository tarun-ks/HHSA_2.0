package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the EIN information.
 *
 */

public class EINBean {
	

	@Length(max = 50)
	private String msEINid;
	@Length(max = 4)
	private String msEINActive;
	
	public final String getMsEINid() {
		return msEINid;
	}
	
	public final void setMsEINid(String msEINid) {
		this.msEINid = msEINid;
	}
	
	public final String getMsEINActive() {
		return msEINActive;
	}
	
	public final void setMsEINActive(String msEINActive) {
		this.msEINActive = msEINActive;
	}

	@Override
	public String toString() {
		return "EINBean [msEINid=" + msEINid + ", msEINActive=" + msEINActive
				+ "]";
	}

}
