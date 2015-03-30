package com.switchpool.model;

import java.io.Serializable;

public class Note implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6550357747212651038L;

	public Note() {
	}

	String path;
	String content;
	String poolid;
	String itemid;
	int size;
	Long time;
    int section;
	Boolean canBeDeleted;
	Boolean isPlaying;

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPoolid() {
		return poolid;
	}
	public void setPoolid(String poolid) {
		this.poolid = poolid;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public int getSection() {
		return section;
	}
	public void setSection(int section) {
		this.section = section;
	}
	public Boolean getCanBeDeleted() {
		return canBeDeleted;
	}
	public void setCanBeDeleted(Boolean canBeDeleted) {
		this.canBeDeleted = canBeDeleted;
	}
	public Boolean getIsPlaying() {
		return isPlaying;
	}
	public void setIsPlaying(Boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
}
