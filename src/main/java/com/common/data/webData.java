package com.common.data;

/**
 * 需要存储的代理网站的信息
 * @author lmd
 *
 */
public class webData {
	private String Ip;
	private int port;
	private String degreeOfConfidentiality;
	private String type;
	private String address;
	private String webSite;
	
	public String getWebSite() {
		return webSite;
	}
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	public String getIp() {
		return Ip;
	}
	public void setIp(String ip) {
		Ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getDegreeOfConfidentiality() {
		return degreeOfConfidentiality;
	}
	public void setDegreeOfConfidentiality(String degreeOfConfidentiality) {
		this.degreeOfConfidentiality = degreeOfConfidentiality;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
