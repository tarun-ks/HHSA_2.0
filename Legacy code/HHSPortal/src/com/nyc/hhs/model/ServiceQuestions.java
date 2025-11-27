package com.nyc.hhs.model;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the Service Questions information.
 *
 */

public class ServiceQuestions {

	@Length(max = 20)
	private String msQuestion1;
	@Length(max = 20)
	private String msQuestion2;
	@Length(max = 20)
	private String msQuestion3;
	
	public String getMsQuestion1() {
		return msQuestion1;
	}
	
	public void setMsQuestion1(String msQuestion1) {
		this.msQuestion1 = msQuestion1;
	}
	
	public String getMsQuestion2() {
		return msQuestion2;
	}

	public void setMsQuestion2(String msQuestion2) {
		this.msQuestion2 = msQuestion2;
	}
	
	public String getMsQuestion3() {
		return msQuestion3;
	}
	
	public void setMsQuestion3(String msQuestion3) {
		this.msQuestion3 = msQuestion3;
	}

	@Override
	public String toString() {
		return "ServiceQuestions [msQuestion1=" + msQuestion1
				+ ", msQuestion2=" + msQuestion2 + ", msQuestion3="
				+ msQuestion3 + "]";
	}
	
}
