package com.nyc.hhs.frameworks.grid;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a bean which maintains the state of the List Box and is 
 * used by the ListBoxTag class to create a ListBox element in the Grid.
 * 
 */

public class ListBox implements FormElement {

	public ListBox(String asId) {
		this.msId = asId;
	}

	private String msId;
	private String msName;
	private String msSize;
	private String msMultiple;
	
	private List<ListOption> moLListValues = new ArrayList<ListOption>();

	@Override
	public int getType() {
		return 5;
	}

	
	public String getId() {
		return msId;
	}

	public void setId(String asId) {
		this.msId = asId;
	}

	public String getName() {
		if(null == msName){
			return "";
		}
		return msName;
	}

	public void setName(String asName) {
		this.msName = asName;
	}

	public String getSize() {
		if(null == msSize){
			return "";
		}
		return msSize;
	}

	public void setSize(String asSize) {
		this.msSize = asSize;
	}

	public String getMultiple() {
		if(null == msMultiple){
			return "";
		}
		return msMultiple;
	}

	public void setMultiple(String asMultiple) {
		this.msMultiple = asMultiple;
	}

	public List<ListOption> getListValues() {
		return moLListValues;
	}

	public void setListValues(List<ListOption> aoLListValues) {
		this.moLListValues = aoLListValues;
	}
}
