package com.switchpool.model;

import java.io.Serializable;
import java.util.List;

public class Model implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8832749082009196198L;

	public Model() {
	}
	
	String itemid;
	List<File> fileArr;
	String modetype;
	String version;

	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public List<File> getFileArr() {
		return fileArr;
	}
	public void setFileArr(List<File> fileArr) {
		this.fileArr = fileArr;
	}
	public String getModetype() {
		return modetype;
	}
	public void setModetype(String modetype) {
		this.modetype = modetype;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}
