package com.switchpool.model;

import java.io.Serializable;

public class SearchKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5747877155422633162L;

	public SearchKey() {
	}

	String itemid;
	String[] keywords;

	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String[] getKeywords() {
		return keywords;
	}
	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}
	
}
