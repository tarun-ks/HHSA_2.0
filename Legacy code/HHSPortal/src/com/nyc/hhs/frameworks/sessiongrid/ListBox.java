package com.nyc.hhs.frameworks.sessiongrid;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a bean which maintains the state of the List Box and is 
 * used by the ListBoxTag class to create a ListBox element in the Grid.
 * 
 */

public class ListBox implements FormElement {

	public ListBox(String id) {
		this.id = id;
	}

	private String id;
	private String name;
	private String size;
	private String multiple;
	
	private List<ListOption> listValues = new ArrayList<ListOption>();

	@Override
	public int getType() {
		return 5;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		if(null == name){
			return "";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		if(null == size){
			return "";
		}
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMultiple() {
		if(null == multiple){
			return "";
		}
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public List<ListOption> getListValues() {
		return listValues;
	}

	public void setListValues(List<ListOption> listValues) {
		this.listValues = listValues;
	}
}
