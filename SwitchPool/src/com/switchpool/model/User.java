package com.switchpool.model;

import java.io.Serializable; 

import android.R.integer;

public class User implements Serializable {

	private static final long serialVersionUID = 6598144297210617472L;

	public User() {
		// TODO Auto-generated constructor stub
	}

	public String cellphone;
	public String password;
	public String uid;
	public String ver;
	
	public String mod;
	public String prefix;
	public String uetype;
	public String ueid;
	public String uename;
	public String os;
	public String osver;
	public String vender;
	public String brand;
	public String ip;
	
	public int buglog;
	public int channel;
	public int inreg;
	public int topic;
	public String token;
	
	public String getCellphone() {
		return cellphone;
	}
	public String getPassword() {
		return password;
	}
	public String getUid() {
		return uid;
	}
	public String getVer() {
		return ver;
	}
	public String getMod() {
		return mod;
	}
	public String getPrefix() {
		return prefix;
	}
	public String getUetype() {
		return uetype;
	}
	public String getUeid() {
		return ueid;
	}
	public String getUename() {
		return uename;
	}
	public String getOs() {
		return os;
	}
	public String getOsver() {
		return osver;
	}
	public String getVender() {
		return vender;
	}
	public String getBrand() {
		return brand;
	}
	public String getIp() {
		return ip;
	}
	public int getBuglog() {
		return buglog;
	}
	public int getChannel() {
		return channel;
	}
	public int getInreg() {
		return inreg;
	}
	public int getTopic() {
		return topic;
	}
	public String getToken() {
		return token;
	}
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public void setMod(String mod) {
		this.mod = mod;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public void setUetype(String uetype) {
		this.uetype = uetype;
	}
	public void setUeid(String ueid) {
		this.ueid = ueid;
	}
	public void setUename(String uename) {
		this.uename = uename;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public void setOsver(String osver) {
		this.osver = osver;
	}
	public void setVender(String vender) {
		this.vender = vender;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setBuglog(int buglog) {
		this.buglog = buglog;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public void setInreg(int inreg) {
		this.inreg = inreg;
	}
	public void setTopic(int topic) {
		this.topic = topic;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
