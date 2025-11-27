
package com.nyc.hhs.model;

import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the Provider Expiry Rule information.
 *
 */

public class ProviderExpiryRuleBean {

	@Length(max = 20) 
	private String msProviderId;
	
	private int miNumDays;
	
	private Date mdExpiryDate;
	
	public int getNumDays() {
		return miNumDays;
	}
	public void setNumDays(int miNumDays) {
		this.miNumDays = miNumDays;
	}
	public String getProviderId() {
		return msProviderId;
	}
	public void setProviderId(String msProviderId) {
		this.msProviderId = msProviderId;
	}
	public void setExpiryDate(Date mdExpiryDate) {
		this.mdExpiryDate = mdExpiryDate;
	}
	public Date getExpiryDate() {
		return mdExpiryDate;
	}
	@Override
	public String toString() {
		return "ProviderExpiryRuleBean [msProviderId=" + msProviderId
				+ ", miNumDays=" + miNumDays + ", mdExpiryDate=" + mdExpiryDate
				+ "]";
	}
	
	
} 
