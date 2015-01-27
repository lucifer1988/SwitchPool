package com.switchpool.model;

import java.io.Serializable;

public class Subject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1466776551039250613L;

	public Subject() {
		// TODO Auto-generated constructor stub
	}
	
	public String title;
	public String subjectid;
	public String desc;
	public String seq;
	public String type;
	public int bgImage;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubjectid() {
		return subjectid;
	}
	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getBgImage() {
		return bgImage;
	}
	public void setBgImage(int bgImage) {
		this.bgImage = bgImage;
	}
	
}
