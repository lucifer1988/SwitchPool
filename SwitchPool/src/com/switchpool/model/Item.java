package com.switchpool.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Item implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3029730234676572044L;

	public Item() {
		// TODO Auto-generated constructor stub
	}
	String caption;
	List<Item> itemArr ;
	int order;
	String id;
	String parentid;

	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public List<Item> getItemArr() {
		return itemArr;
	}
	public void setItemArr(List<Item> itemArr) {
		this.itemArr = itemArr;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	
	

}
