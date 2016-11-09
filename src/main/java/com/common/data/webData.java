package com.common.data;

/**
 * 需要存储的代理网站的信息
 * @author lmd
 *
 */
public class webData {
	//主键，主要是用来更新数据库中的测试次数，如果超过一定的阈值，那么
	private int index;
	private String Ip;
	private int port;
	private String degreeOfConfidentiality;
	private String type;
	private String address;
	private String webSite;
	//响应时间，主要是测试用的
	private long responseTime;
	//用来统计测试次数
	private int testTimes;
	//该代理ip是否可以正常工作{0,1}
	private boolean flag;
	//该代理ip是否是新抓下来的，如果是，则为1，不是，则为0
	private boolean isNew;
	
	
	public boolean getIsNew() {
		return isNew;
	}
	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public boolean getFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public long getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(long responseTime2) {
		this.responseTime = responseTime2;
	}
	public int getTestTimes() {
		return testTimes;
	}
	public void setTestTimes(int testTimes) {
		this.testTimes = testTimes;
	}
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
