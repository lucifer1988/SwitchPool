package com.switchpool.model;

import java.io.Serializable;
import java.util.List;

public class SearchKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5747877155422633162L;

	public SearchKey() {
	}

	String itemid;
	List<String> keywords;

	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	
}
