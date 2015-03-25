package com.switchpool.model;

import java.io.Serializable;

public class SPFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2221775071202390361L;

	public SPFile() {
	}

	String fid;
	String ftype;
	String itemid;
	int seq;
	String path;

	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getFtype() {
		return ftype;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
}
