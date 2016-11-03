package com.common.data;

public class ProxyQualified {
	private String Ip;
	private int Port;
	private int ResponseTime;
	private String degreeOfConfidentiality;

	public String getDegreeOfConfidentiality() {
		return degreeOfConfidentiality;
	}

	public void setDegreeOfConfidentiality(String degreeOfConfidentiality) {
		this.degreeOfConfidentiality = degreeOfConfidentiality;
	}

	public String getIp() {
		return Ip;
	}

	public void setIp(String ip) {
		Ip = ip;
	}

	public int getPort() {
		return Port;
	}

	public void setPort(int port) {
		Port = port;
	}

	public int getResponseTime() {
		return ResponseTime;
	}

	public void setResponseTime(int responseTime) {
		ResponseTime = responseTime;
	}
}
